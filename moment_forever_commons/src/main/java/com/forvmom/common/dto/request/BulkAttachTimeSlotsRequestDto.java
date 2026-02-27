package com.forvmom.common.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Request body for the bulk-attach endpoint.
 *
 * POST
 * /admin/experiences/{experienceId}/locations/{locationId}/timeslots/bulk-attach
 *
 * Each item in the list carries a timeSlotId plus the optional override
 * settings
 * (priceOverride, maxCapacity, validFrom, validTo, isActive).
 * Items that refer to an already-attached timeSlotId are skipped (reported as
 * DUPLICATE in the result list).
 */
public class BulkAttachTimeSlotsRequestDto {

    private List<TimeSlotAttachItem> items;

    public List<TimeSlotAttachItem> getItems() {
        return items;
    }

    public void setItems(List<TimeSlotAttachItem> items) {
        this.items = items;
    }

    // ── Inner item ────────────────────────────────────────────────────────────

    public static class TimeSlotAttachItem extends ExperienceTimeSlotAttachRequestDto {

        /** ID of the master TimeSlot to attach. */
        private Long timeSlotId;

        public Long getTimeSlotId() {
            return timeSlotId;
        }

        public void setTimeSlotId(Long timeSlotId) {
            this.timeSlotId = timeSlotId;
        }
    }
}
