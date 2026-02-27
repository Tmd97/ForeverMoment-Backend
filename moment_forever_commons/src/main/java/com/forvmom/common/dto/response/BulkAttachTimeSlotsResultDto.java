package com.forvmom.common.dto.response;

import java.util.List;

/**
 * Response DTO for the bulk-attach timeslots endpoint.
 *
 * POST
 * /admin/experiences/{experienceId}/locations/{locationId}/timeslots/bulk-attach
 *
 * Contains two lists:
 * - attached : successfully created ExperienceTimeSlotMapper records
 * - skipped : timeSlotIds that were already attached (DUPLICATE) or not found
 * (NOT_FOUND)
 */
public class BulkAttachTimeSlotsResultDto {

    private List<ExperienceTimeSlotResponseDto> attached;
    private List<SkippedTimeSlotDto> skipped;

    public BulkAttachTimeSlotsResultDto(List<ExperienceTimeSlotResponseDto> attached,
            List<SkippedTimeSlotDto> skipped) {
        this.attached = attached;
        this.skipped = skipped;
    }

    public List<ExperienceTimeSlotResponseDto> getAttached() {
        return attached;
    }

    public void setAttached(List<ExperienceTimeSlotResponseDto> attached) {
        this.attached = attached;
    }

    public List<SkippedTimeSlotDto> getSkipped() {
        return skipped;
    }

    public void setSkipped(List<SkippedTimeSlotDto> skipped) {
        this.skipped = skipped;
    }

    // ── Inner class ───────────────────────────────────────────────────────────

    public static class SkippedTimeSlotDto {

        public enum Reason {
            DUPLICATE, NOT_FOUND
        }

        private Long timeSlotId;
        private Reason reason;
        private String message;

        public SkippedTimeSlotDto(Long timeSlotId, Reason reason, String message) {
            this.timeSlotId = timeSlotId;
            this.reason = reason;
            this.message = message;
        }

        public Long getTimeSlotId() {
            return timeSlotId;
        }

        public void setTimeSlotId(Long timeSlotId) {
            this.timeSlotId = timeSlotId;
        }

        public Reason getReason() {
            return reason;
        }

        public void setReason(Reason reason) {
            this.reason = reason;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
