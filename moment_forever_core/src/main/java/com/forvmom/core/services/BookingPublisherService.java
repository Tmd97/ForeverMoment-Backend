package com.forvmom.core.services;

import com.forvmom.common.dto.events.BookingRequestEvent;
import com.forvmom.common.dto.events.BookingRequestEvent.BookedAddonSnapshot;
import com.forvmom.common.dto.request.BookingRequestDto;
import com.forvmom.common.dto.response.AppUserResponseDto;
import com.forvmom.common.errorhandler.ResourceNotFoundException;
import com.forvmom.core.producer.BookingEventProducer;
import com.forvmom.data.dao.ExperienceAddonMapperDao;
import com.forvmom.data.dao.ExperienceTimeSlotMapperDao;
import com.forvmom.data.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Orchestrates the booking request flow in the core service.
 *
 * <p>
 * Responsibilities (in order):
 * <ol>
 * <li>Resolve authenticated user</li>
 * <li>Fetch and validate the {@code ExperienceTimeSlotMapper}</li>
 * <li>Check slot capacity against requested guest count</li>
 * <li>Validate booking lead-time against
 * {@code ExperienceDetail.min_hours}</li>
 * <li>Resolve price using the 3-level chain (BASE → LOCATION → SLOT).
 * Level 4 (PINCODE) is a future enhancement — skipped if no DAO exists.</li>
 * <li>Resolve requested add-ons, compute effective prices</li>
 * <li>Build the {@link BookingRequestEvent} and delegate to
 * {@link BookingEventProducer}</li>
 * </ol>
 */
@Service
public class BookingPublisherService {

    private static final Logger logger = LoggerFactory.getLogger(BookingPublisherService.class);

    private final UserProfileService userProfileService;
    private final ExperienceTimeSlotMapperDao timeSlotMapperDao;
    private final ExperienceAddonMapperDao addonMapperDao;
    private final BookingEventProducer bookingEventProducer;

    public BookingPublisherService(UserProfileService userProfileService,
            ExperienceTimeSlotMapperDao timeSlotMapperDao,
            ExperienceAddonMapperDao addonMapperDao,
            BookingEventProducer bookingEventProducer) {
        this.userProfileService = userProfileService;
        this.timeSlotMapperDao = timeSlotMapperDao;
        this.addonMapperDao = addonMapperDao;
        this.bookingEventProducer = bookingEventProducer;
    }

    /**
     * Validates the booking request, resolves all pricing and add-ons, then
     * publishes a {@link BookingRequestEvent}.
     *
     * @param dto the inbound booking request from the controller
     * @return the generated booking reference ID (e.g. {@code MFB-...})
     */
    public String publishBookingRequest(BookingRequestDto dto) {

        // ── 1. Authenticated user ────────────────────────────────────────────
        AppUserResponseDto user = userProfileService.getCurrentUserProfile();

        // ── 2. Fetch & validate slot mapper ─────────────────────────────────
        ExperienceTimeSlotMapper slotMapper = timeSlotMapperDao.findById(dto.getTimeSlotMapperId());
        if (slotMapper == null) {
            throw new ResourceNotFoundException(
                    "TimeSlot mapping not found: " + dto.getTimeSlotMapperId());
        }
        if (!Boolean.TRUE.equals(slotMapper.getIsActive())) {
            throw new IllegalStateException("Selected time slot is not active.");
        }
        if (!slotMapper.isValidOnDate(dto.getBookingDate())) {
            throw new IllegalStateException(
                    "Selected time slot is not available on " + dto.getBookingDate());
        }

        // ── 3. Capacity check ────────────────────────────────────────────────
        Integer available = slotMapper.getAvailableCapacity();
        if (available != null && dto.getGuestCount() > available) {
            throw new IllegalStateException(
                    "Not enough capacity. Requested: " + dto.getGuestCount()
                            + ", available: " + available);
        }

        // ── 4. Lead-time validation ──────────────────────────────────────────
        ExperienceLocationMapper expLocation = slotMapper.getExperienceLocation();
        Experience experience = expLocation.getExperience();
        ExperienceDetail detail = experience.getDetail();

        if (detail != null && detail.getMinHours() != null && detail.getMinHours() > 0) {
            LocalTime slotStart = slotMapper.getTimeSlot().getStartTime();
            LocalDateTime slotStartDateTime = dto.getBookingDate().atTime(slotStart);
            LocalDateTime cutoff = slotStartDateTime.minusHours(detail.getMinHours());
            if (LocalDateTime.now().isAfter(cutoff)) {
                throw new IllegalStateException(
                        "Bookings must be made at least " + detail.getMinHours()
                                + " hour(s) before the slot starts.");
            }
        }

        Location location = expLocation.getLocation();

        // ── 5. Resolve price (3-level chain; Level 4 PINCODE — future) ───────
        BigDecimal resolvedPrice = experience.getBasePrice();
        String pricingLevel = "BASE";

        BigDecimal locationOverride = expLocation.getPriceOverride();
        if (locationOverride != null) {
            resolvedPrice = locationOverride;
            pricingLevel = "LOCATION";
        }

        BigDecimal slotOverride = slotMapper.getPriceOverride();
        if (slotOverride != null) {
            resolvedPrice = slotOverride;
            pricingLevel = "SLOT";
        }

        // Level 4 (PINCODE): ExperiencePincodePrice DAO not yet implemented.
        // When added, query here using (slotMapper.id, dto.getPincode()) and
        // set pricingLevel = "PINCODE" if a match is found.

        BigDecimal totalAmount = resolvedPrice.multiply(BigDecimal.valueOf(dto.getGuestCount()));

        // ── 6. Resolve add-ons ───────────────────────────────────────────────
        List<BookedAddonSnapshot> addonSnapshots = new ArrayList<>();
        BigDecimal addonsTotal = BigDecimal.ZERO;

        List<Long> addonMapperIds = dto.getAddonMapperIds() != null
                ? dto.getAddonMapperIds()
                : Collections.emptyList();

        for (Long addonMapperId : addonMapperIds) {
            ExperienceAddonMapper addonMapper = addonMapperDao.findById(addonMapperId);
            if (addonMapper == null) {
                throw new ResourceNotFoundException(
                        "Add-on mapping not found: " + addonMapperId);
            }
            // Guard: addon must belong to the same experience
            if (!addonMapper.getExperience().getId().equals(experience.getId())) {
                throw new IllegalArgumentException(
                        "Add-on " + addonMapperId + " does not belong to this experience.");
            }
            if (!Boolean.TRUE.equals(addonMapper.getIsActive())) {
                throw new IllegalStateException(
                        "Add-on " + addonMapper.getAddon().getName() + " is not available.");
            }

            BigDecimal effectiveAddonPrice = addonMapper.effectivePrice();
            boolean isFree = Boolean.TRUE.equals(addonMapper.getIsFree());

            addonSnapshots.add(new BookedAddonSnapshot(
                    addonMapperId,
                    addonMapper.getAddon().getName(),
                    effectiveAddonPrice,
                    isFree));

            addonsTotal = addonsTotal.add(effectiveAddonPrice);
        }

        BigDecimal grandTotal = totalAmount.add(addonsTotal);

        // ── 7. Build event ───────────────────────────────────────────────────
        TimeSlot timeSlot = slotMapper.getTimeSlot();

        BookingRequestEvent event = new BookingRequestEvent();
        event.setUserId(user.getId());
        event.setUserEmail(user.getEmail());
        event.setUserFullName(user.getFullName());

        event.setExperienceId(experience.getId());
        event.setExperienceName(experience.getName());
        event.setExperienceSlug(experience.getSlug());

        event.setLocationId(location.getId());
        event.setLocationName(location.getName());

        event.setTimeSlotMapperId(slotMapper.getId());
        event.setTimeSlotId(timeSlot.getId());
        event.setTimeSlotLabel(timeSlot.getLabel());
        event.setStartTime(timeSlot.getStartTime() != null ? timeSlot.getStartTime().toString() : null);
        event.setEndTime(timeSlot.getEndTime() != null ? timeSlot.getEndTime().toString() : null);

        event.setBookingDate(dto.getBookingDate());
        event.setGuestCount(dto.getGuestCount());
        event.setPincode(dto.getPincode());

        event.setResolvedPricePerPerson(resolvedPrice);
        event.setPricingLevel(pricingLevel);
        event.setTotalAmount(totalAmount);
        event.setAvailableCapacity(available);

        event.setAddons(addonSnapshots);
        event.setAddonsTotal(addonsTotal);
        event.setGrandTotal(grandTotal);

        logger.info("Booking request assembled: experienceId={}, date={}, guests={}, "
                + "pricingLevel={}, resolvedPrice={}, grandTotal={}",
                experience.getId(), dto.getBookingDate(), dto.getGuestCount(),
                pricingLevel, resolvedPrice, grandTotal);

        // ── 8. Publish to Kafka via producer ─────────────────────────────────
        return bookingEventProducer.sendBookingRequested(event);
    }
}