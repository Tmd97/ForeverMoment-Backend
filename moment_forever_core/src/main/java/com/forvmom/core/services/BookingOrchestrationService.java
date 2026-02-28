package com.forvmom.core.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.forvmom.common.dto.request.BookingRequestDto;
import com.forvmom.common.dto.response.AppUserResponseDto;
import com.forvmom.common.utils.AppConstants;
import com.forvmom.core.async.BookingEnrichmentTask;
import com.forvmom.data.dao.BookingOutboxDao;
import com.forvmom.data.dao.ExperienceTimeSlotMapperDao;
import com.forvmom.data.entities.BookingOutbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Orchestrates the booking initiation flow in the Core service.
 *
 * <p>
 * <strong>Responsibilities (in order):</strong>
 * <ol>
 * <li>Generate a unique {@code bookingId} <em>before</em> the transaction.</li>
 * <li>Inside a single DB transaction:
 * <ul>
 * <li>Validate the slot mapper (active, valid on date).</li>
 * <li>Validate lead-time against {@code ExperienceDetail.minHours}.</li>
 * <li>Atomically increment {@code current_bookings} with a capacity
 * guard SQL UPDATE (zero rows → capacity exceeded).</li>
 * <li>Insert a minimal {@link BookingOutbox} record (IDs only, no names).</li>
 * </ul>
 * </li>
 * <li>After commit, fire an async enrichment task via
 * {@link BookingEnrichmentTask#enrich}.</li>
 * <li>Return the {@code bookingId} immediately (202 Accepted).</li>
 * </ol>
 *
 * <p>
 * Full enrichment (fetching names, resolving prices, publishing Kafka) happens
 * asynchronously in {@link BookingEnrichmentTask}. A scheduled poller
 * ({@link com.forvmom.core.scheduler.BookingOutboxPoller}) retries any failed
 * records.
 */
@Service
public class BookingOrchestrationService {

    private static final Logger logger = LoggerFactory.getLogger(BookingOrchestrationService.class);

    private final UserProfileService userProfileService;
    private final ExperienceTimeSlotMapperDao slotMapperDao;
    private final BookingOutboxDao outboxDao;
    private final BookingEnrichmentTask enrichmentTask;
    private final ObjectMapper objectMapper;

    public BookingOrchestrationService(UserProfileService userProfileService,
            ExperienceTimeSlotMapperDao slotMapperDao,
            BookingOutboxDao outboxDao,
            BookingEnrichmentTask enrichmentTask,
            ObjectMapper objectMapper) {
        this.userProfileService = userProfileService;
        this.slotMapperDao = slotMapperDao;
        this.outboxDao = outboxDao;
        this.enrichmentTask = enrichmentTask;
        this.objectMapper = objectMapper;
    }

    /**
     * Initiates a booking request: validates, locks capacity, writes outbox, fires
     * async enrichment.
     *
     * @param dto inbound booking request
     * @return generated booking reference ID (e.g. {@code MFB-1735000-A3F2})
     */
    public String initiateBooking(BookingRequestDto dto) {

        // ── 1. Resolve authenticated user ────────────────────────────────────
        AppUserResponseDto user = userProfileService.getCurrentUserProfile();

        // ── 2. Generate bookingId BEFORE the transaction ──────────────────────
        String bookingId = generateBookingId();

        // ── 3. Run transactional block ────────────────────────────────────────
        executeTransaction(bookingId, dto, user.getId());

        // ── 4. Post-commit: fire async enrichment (does not block) ─────────────
        enrichmentTask.enrich(bookingId);

        logger.info("Booking initiated: bookingId={}, userId={}, slotMapperId={}",
                bookingId, user.getId(), dto.getTimeSlotMapperId());

        return bookingId;
    }

    // ── Private transactional step ─────────────────────────────────────────────

    @Transactional
    protected void executeTransaction(String bookingId, BookingRequestDto dto, Long userId) {

        // 3a. Atomic capacity increment (SQL-level, avoids TOCTOU race)
        // Delegates to DAO which runs a JPQL UPDATE with WHERE guard.
        // It reads `maxCapacity` natively from the row during the update.
        // 0 rows updated = slot doesn't exist, is deleted, or capacity exceeded.
        int rowsUpdated = slotMapperDao.atomicIncrementCapacity(
                dto.getTimeSlotMapperId(), dto.getGuestCount());

        if (rowsUpdated == 0) {
            throw new IllegalStateException(
                    "Time slot is unavailable, inactive, or not enough capacity. Requested: " + dto.getGuestCount()
                            + " for slot: " + dto.getTimeSlotMapperId());
        }

        // 3b. Build and persist minimal outbox record
        BookingOutbox outbox = new BookingOutbox();
        outbox.setBookingReferenceId(bookingId);
        // save payload as json,if in future events changes, no need to add extra (or modify) columns
        outbox.setPayload(buildMinimalPayload(userId, dto));
        outbox.setStatus(BookingOutbox.STATUS_PENDING);
        outboxDao.save(outbox);

        logger.debug("Outbox record persisted: bookingId={}", bookingId);
    }

    private String buildMinimalPayload(Long userId, BookingRequestDto dto) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("slotMapperId", dto.getTimeSlotMapperId());
        payload.put("guestCount", dto.getGuestCount());
        payload.put("addonMapperIds", dto.getAddonMapperIds() != null ? dto.getAddonMapperIds() : List.of());
        payload.put("pincode", dto.getPincode());
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize booking payload", e);
        }
    }

    private String generateBookingId() {
        return AppConstants.BOOKING_REFERENCE_PREFIX
                + System.currentTimeMillis()
                + "-"
                + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}
