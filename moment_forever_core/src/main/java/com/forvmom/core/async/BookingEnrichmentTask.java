package com.forvmom.core.async;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.forvmom.common.dto.events.BookingRequestEvent;
import com.forvmom.common.dto.events.BookingRequestEvent.BookedAddonSnapshot;
import com.forvmom.common.dto.snapshot.*;
import com.forvmom.core.services.CatalogCacheService;
import com.forvmom.core.producer.BookingEventProducer;
import com.forvmom.data.dao.*;
import com.forvmom.data.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Async enrichment task that runs <em>after</em> the booking outbox transaction
 * commits.
 *
 * <p>
 * Flow:
 * <ol>
 * <li>Load the outbox record by bookingReferenceId.</li>
 * <li>Parse the minimal payload (IDs only).</li>
 * <li>Fetch all snapshot data from Redis; on miss → DB fallback + re-warm.</li>
 * <li>Resolve pricing chain (BASE → LOCATION → SLOT).</li>
 * <li>Build fully enriched {@link BookingRequestEvent}.</li>
 * <li>Publish to Kafka and mark the outbox record as PUBLISHED.</li>
 * <li>On any failure → mark FAILED so the {@link com.forvmom.core.services.OutboxCleanupService}
 * retries.</li>
 * </ol>
 */
@Component
public class BookingEnrichmentTask {

    private static final Logger logger = LoggerFactory.getLogger(BookingEnrichmentTask.class);

    private final BookingOutboxDao outboxDao;
    private final ExperienceTimeSlotMapperDao slotMapperDao;
    private final ExperienceAddonMapperDao addonMapperDao;
    private final CatalogCacheService catalogCache;
    private final BookingEventProducer bookingEventProducer;
    private final ObjectMapper objectMapper;

    // DB fallback DAOs
    private final ApplicationUserDao userDao;

    public BookingEnrichmentTask(BookingOutboxDao outboxDao,
            ExperienceTimeSlotMapperDao slotMapperDao,
            ExperienceAddonMapperDao addonMapperDao,
            CatalogCacheService catalogCache,
            BookingEventProducer bookingEventProducer,
            ObjectMapper objectMapper,
            ApplicationUserDao userDao) {
        this.outboxDao = outboxDao;
        this.slotMapperDao = slotMapperDao;
        this.addonMapperDao = addonMapperDao;
        this.catalogCache = catalogCache;
        this.bookingEventProducer = bookingEventProducer;
        this.objectMapper = objectMapper;
        this.userDao = userDao;
    }

    /**
     * Entry point — called by
     * {@link com.forvmom.core.services.BookingOrchestrationService}
     * post-commit and by {@link com.forvmom.core.services.OutboxCleanupService} for retries.
     *
     * <p>
     * Runs on the {@code bookingTaskExecutor} thread pool.
     */
    @Async("bookingTaskExecutor")
    @Transactional
    public void enrich(String bookingReferenceId) {
        logger.info("Starting enrichment for bookingReferenceId={}", bookingReferenceId);

        BookingOutbox outbox = outboxDao.findByBookingReferenceId(bookingReferenceId);
        if (outbox == null) {
            logger.error("Outbox record not found for bookingReferenceId={}", bookingReferenceId);
            return;
        }

        if (BookingOutbox.STATUS_PUBLISHED.equals(outbox.getStatus())) {
            logger.warn("Outbox already published, skipping: bookingReferenceId={}", bookingReferenceId);
            return;
        }

        // --- Concurrency Control: Atomic Claim ---
        // Attempt to atomically set the status to PROCESSING.
        // If this returns 0, another thread or poller already claimed it, or it was
        // just published.
        int updated = outboxDao.markAsProcessing(bookingReferenceId);
        if (updated == 0) {
            logger.info("Could not claim outbox record (already processing/published): bookingReferenceId={}",
                    bookingReferenceId);
            return;
        }

        try {
            // ── 1. Parse minimal payload ─────────────────────────────────────
            Map<String, Object> payload = objectMapper.readValue(
                    outbox.getPayload(), new TypeReference<>() {
                    });

            Long userId = toLong(payload.get("userId"));
            Long slotMapperId = toLong(payload.get("slotMapperId"));
            Integer guestCount = toInt(payload.get("guestCount"));
            String pincode = (String) payload.get("pincode");

            List<Long> addonMapperIds = payload.get("addonMapperIds") != null
                    ? toListLong((List<?>) payload.get("addonMapperIds"))
                    : Collections.emptyList();

            // ── 2 & 3. Fetch snapshots from Redis (with DB fallback + re-warm) ────
            UserSnapshot userSnap = getOrWarmUser(userId);

            // Fetch slot snapshot first (we have its ID)
            SlotSnapshot slotSnap = catalogCache.getSlotSnapshot(slotMapperId);

            ExperienceTimeSlotMapper slotMapper = null; // Lazy load if needed
            ExperienceLocationMapper expLocation = null;
            Experience experience = null;
            Location location = null;
            TimeSlot timeSlot = null;

            if (slotSnap == null) {
                // Cache miss -> fallback to DB
                slotMapper = slotMapperDao.findById(slotMapperId);
                if (slotMapper == null) {
                    throw new IllegalStateException("SlotMapper not found: " + slotMapperId);
                }
                expLocation = slotMapper.getExperienceLocation();
                experience = expLocation.getExperience();
                location = expLocation.getLocation();
                timeSlot = slotMapper.getTimeSlot();

                catalogCache.warmSlotCache(slotMapper);
                slotSnap = new SlotSnapshot(slotMapper.getId(),
                        experience.getId(), location.getId(), timeSlot.getId(),
                        timeSlot.getLabel(),
                        timeSlot.getStartTime() != null ? timeSlot.getStartTime().toString() : null,
                        timeSlot.getEndTime() != null ? timeSlot.getEndTime().toString() : null,
                        slotMapper.getPriceOverride(), slotMapper.getMaxCapacity());
            }

            ExperienceSnapshot expSnap = catalogCache.getExperienceSnapshot(slotSnap.getExperienceId());
            if (expSnap == null) {
                // Should be very rare if cached property, but if so we must hit the DB
                if (experience == null) {
                    ExperienceTimeSlotMapper tempMapper = slotMapperDao.findById(slotMapperId);
                    if (tempMapper == null)
                        throw new IllegalStateException("SlotMapper not found: " + slotMapperId);
                    experience = tempMapper.getExperienceLocation().getExperience();
                }
                catalogCache.warmExperienceCache(experience);
                expSnap = new ExperienceSnapshot(experience.getId(), experience.getName(),
                        experience.getSlug(), experience.getBasePrice());
            }

            LocationSnapshot locSnap = catalogCache.getLocationSnapshot(slotSnap.getExperienceId(),
                    slotSnap.getLocationId());
            if (locSnap == null) {
                // Again, rare but we need the DB if so
                if (expLocation == null) {
                    ExperienceTimeSlotMapper tempMapper = slotMapperDao.findById(slotMapperId);
                    if (tempMapper == null)
                        throw new IllegalStateException("SlotMapper not found: " + slotMapperId);
                    expLocation = tempMapper.getExperienceLocation();
                    experience = expLocation.getExperience();
                    location = expLocation.getLocation();
                }
                catalogCache.warmLocationCache(expLocation);
                locSnap = new LocationSnapshot(location.getId(), location.getName(), expLocation.getPriceOverride());
            }

            // ── 4. Resolve pricing chain (BASE → LOCATION → SLOT) ────────────
            BigDecimal resolvedPrice = expSnap.getBasePrice();
            String pricingLevel = "BASE";

            if (locSnap.getPriceOverride() != null) {
                resolvedPrice = locSnap.getPriceOverride();
                pricingLevel = "LOCATION";
            }
            if (slotSnap.getPriceOverride() != null) {
                resolvedPrice = slotSnap.getPriceOverride();
                pricingLevel = "SLOT";
            }

            BigDecimal totalAmount = resolvedPrice.multiply(BigDecimal.valueOf(guestCount));

            // ── 5. Resolve add-ons ────────────────────────────────────────────
            List<BookedAddonSnapshot> addonSnapshots = new ArrayList<>();
            BigDecimal addonsTotal = BigDecimal.ZERO;

            for (Long addonMapperId : addonMapperIds) {
                AddonSnapshot addonSnap = catalogCache.getAddonSnapshot(addonMapperId);
                if (addonSnap == null) {
                    ExperienceAddonMapper addonMapper = addonMapperDao.findById(addonMapperId);
                    if (addonMapper == null) {
                      //  throw new IllegalStateException("AddonMapper not found: " + addonMapperId);
                    }
                    catalogCache.warmAddonCache(addonMapper);
                    addonSnap = new AddonSnapshot(addonMapperId, addonMapper.getAddon().getName(),
                            addonMapper.effectivePrice(), Boolean.TRUE.equals(addonMapper.getIsFree()));
                }
                addonSnapshots.add(new BookedAddonSnapshot(
                        addonSnap.getAddonMapperId(),
                        addonSnap.getAddonName(),
                        addonSnap.getEffectivePrice(),
                        addonSnap.isFree()));
                addonsTotal = addonsTotal.add(addonSnap.getEffectivePrice());
            }

            BigDecimal grandTotal = totalAmount.add(addonsTotal);
            Integer availableCapacity = slotSnap.getMaxCapacity() != null
                    ? slotSnap.getMaxCapacity() - guestCount
                    : null;

            // ── 6. Build enriched event ───────────────────────────────────────
            BookingRequestEvent event = new BookingRequestEvent();
            event.setBookingId(bookingReferenceId);
            event.setUserId(userSnap.getUserId());
            event.setUserEmail(userSnap.getEmail());
            event.setUserFullName(userSnap.getFullName());
            event.setExperienceId(expSnap.getId());
            event.setExperienceName(expSnap.getName());
            event.setExperienceSlug(expSnap.getSlug());
            event.setLocationId(locSnap.getLocationId());
            event.setLocationName(locSnap.getLocationName());
            event.setTimeSlotMapperId(slotSnap.getSlotMapperId());
            event.setTimeSlotId(slotSnap.getTimeSlotId());
            event.setTimeSlotLabel(slotSnap.getLabel());
            event.setStartTime(slotSnap.getStartTime());
            event.setEndTime(slotSnap.getEndTime());
            event.setGuestCount(guestCount);
            event.setPincode(pincode);
            event.setResolvedPricePerPerson(resolvedPrice);
            event.setPricingLevel(pricingLevel);
            event.setTotalAmount(totalAmount);
            event.setAvailableCapacity(availableCapacity);
            event.setAddons(addonSnapshots);
            event.setAddonsTotal(addonsTotal);
            event.setGrandTotal(grandTotal);

            // ── 7. Publish to Kafka (blocking get so we know it succeeded) ────
            bookingEventProducer.sendBookingRequested(event);

            // ── 8. Mark outbox as published ───────────────────────────────────
            outbox.markPublished();
            outboxDao.update(outbox);
            logger.info("Enrichment complete and published: bookingReferenceId={}", bookingReferenceId);

        } catch (Exception e) {
            logger.error("Enrichment failed for bookingReferenceId={}: {}", bookingReferenceId, e.getMessage(), e);
            if (outbox != null) {
                // If we grabbed the PROCESSING lock, we must revert it to FAILED so it can be
                // retried
                outbox.markFailed();
                outboxDao.update(outbox);
            }
        }
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    private UserSnapshot getOrWarmUser(Long userId) {
        UserSnapshot snap = catalogCache.getUserSnapshot(userId);
        if (snap != null)
            return snap;
        ApplicationUser user = userDao.findById(userId);
        if (user == null)
            throw new IllegalStateException("User not found: " + userId);
        catalogCache.warmUserCache(user);
        return new UserSnapshot(user.getId(), user.getEmail(), user.getFullName());
    }

    private static Long toLong(Object o) {
        if (o == null)
            return null;
        if (o instanceof Number)
            return ((Number) o).longValue();
        return Long.parseLong(o.toString());
    }

    private static Integer toInt(Object o) {
        if (o == null)
            return null;
        if (o instanceof Number)
            return ((Number) o).intValue();
        return Integer.parseInt(o.toString());
    }

    private static List<Long> toListLong(List<?> raw) {
        List<Long> result = new ArrayList<>();
        for (Object o : raw)
            result.add(toLong(o));
        return result;
    }
}
