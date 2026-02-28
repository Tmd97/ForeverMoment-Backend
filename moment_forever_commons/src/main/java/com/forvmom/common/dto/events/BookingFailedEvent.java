package com.forvmom.common.dto.events;

import java.time.LocalDateTime;

/**
 * Published to topic {@code booking-failed} by the Booking Service when a
 * booking cannot be fulfilled (payment failure or validation failure).
 *
 * <p>
 * Consumed by Core Service to roll back the {@code current_bookings} counter
 * that was incremented atomically during the booking request transaction.
 *
 * <p>
 * IMPORTANT: {@code timeSlotMapperId} and {@code guestCount} are required
 * so Core can perform the rollback without any additional DB lookup.
 */
public class BookingFailedEvent extends BaseEvent {

    private String bookingId;
    private Long userId;
    private String userEmail;
    private Long experienceId;
    /** Required for Core to identify which slot row to decrement */
    private Long timeSlotMapperId;
    /** Required for Core to know how much to decrement */
    private Integer guestCount;
    private String failureReason;
    private LocalDateTime failedAt;

    public BookingFailedEvent() {
        super();
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Long getExperienceId() {
        return experienceId;
    }

    public void setExperienceId(Long experienceId) {
        this.experienceId = experienceId;
    }

    public Long getTimeSlotMapperId() {
        return timeSlotMapperId;
    }

    public void setTimeSlotMapperId(Long timeSlotMapperId) {
        this.timeSlotMapperId = timeSlotMapperId;
    }

    public Integer getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(Integer guestCount) {
        this.guestCount = guestCount;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public LocalDateTime getFailedAt() {
        return failedAt;
    }

    public void setFailedAt(LocalDateTime failedAt) {
        this.failedAt = failedAt;
    }
}
