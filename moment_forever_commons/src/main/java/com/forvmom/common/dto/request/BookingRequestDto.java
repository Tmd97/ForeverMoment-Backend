package com.forvmom.common.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Request DTO for creating a new booking.
 *
 * <p>
 * Identifier notes:
 * <ul>
 * <li>{@code timeSlotMapperId} — ID of {@code ExperienceTimeSlotMapper}, which
 * encodes a specific (Experience, Location, TimeSlot) combination including
 * its capacity and price override.</li>
 * <li>{@code addonMapperIds} — IDs of {@code ExperienceAddonMapper} rows (not
 * master {@code Addon} IDs). This lets the service directly read the
 * per-experience price override and {@code is_free} flag without an extra
 * join.</li>
 * </ul>
 */
public class BookingRequestDto {

    /**
     * ExperienceTimeSlotMapper ID — identifies the exact slot the user wants to
     * book.
     */
    @NotNull(message = "Time slot mapper ID is required")
    private Long timeSlotMapperId;

    /** Desired booking date. Must be today or a future date. */
    @NotNull(message = "Booking date is required")
    @FutureOrPresent(message = "Booking date must be today or in the future")
    private LocalDate bookingDate;

    /** Number of guests. Must be at least 1. */
    @NotNull(message = "Guest count is required")
    @Min(value = 1, message = "Guest count must be at least 1")
    @Max(value = 50, message = "Guest count cannot exceed 50")
    private Integer guestCount;

    /**
     * Optional pincode — when provided the service will attempt to resolve a
     * Level 4 (pincode) price override via {@code ExperiencePincodePrice}.
     */
    private String pincode;

    /**
     * IDs of {@code ExperienceAddonMapper} rows representing the add-ons the
     * user wants to include with this booking. May be null or empty.
     */
    private List<Long> addonMapperIds;

    // ── Getters & Setters ────────────────────────────────────────────────────

    public Long getTimeSlotMapperId() {
        return timeSlotMapperId;
    }

    public void setTimeSlotMapperId(Long timeSlotMapperId) {
        this.timeSlotMapperId = timeSlotMapperId;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Integer getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(Integer guestCount) {
        this.guestCount = guestCount;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public List<Long> getAddonMapperIds() {
        return addonMapperIds;
    }

    public void setAddonMapperIds(List<Long> addonMapperIds) {
        this.addonMapperIds = addonMapperIds;
    }
}
