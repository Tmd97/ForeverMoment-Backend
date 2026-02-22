package com.forvmom.core.controller.admin;

import com.forvmom.common.dto.request.ExperienceTimeSlotAttachRequestDto;
import com.forvmom.common.dto.request.ReorderRequestDto;
import com.forvmom.common.dto.request.TimeSlotRequestDto;
import com.forvmom.common.dto.response.ExperienceTimeSlotResponseDto;
import com.forvmom.common.dto.response.TimeSlotResponseDto;
import com.forvmom.common.response.ApiResponse;
import com.forvmom.common.response.ResponseUtil;
import com.forvmom.common.utils.AppConstants;
import com.forvmom.core.services.ExperienceTimeSlotService;
import com.forvmom.core.services.ReorderingService;
import com.forvmom.data.entities.Experience;
import com.forvmom.data.entities.TimeSlot;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin controller for master TimeSlot CRUD and experience-location attachment.
 *
 * Mirrors ExperienceCancellationPolicyControllerAdmin — one controller,
 * two URL groups:
 *
 * Master CRUD → /api/admin/timeslots
 * Attachment →
 * /api/admin/experiences/{experienceId}/locations/{locationId}/timeslots
 */
@RestController
@RequestMapping("/admin")
@Tag(name = "Admin TimeSlot API", description = "Master TimeSlot CRUD and per-experience-location attachment with price override (Admin only)")
public class ExperienceTimeSlotControllerAdmin {

        @Autowired
        private ExperienceTimeSlotService timeSlotService;

        @Autowired
        private ReorderingService reorderingService;

        // ── Master TimeSlot CRUD ──────────────────────────────────────────────────

        @PostMapping("/timeslots")
        @Operation(summary = "Create TimeSlot", description = "Creates a reusable master time slot record")
        public ResponseEntity<ApiResponse<?>> createTimeSlot(
                        @Valid @RequestBody TimeSlotRequestDto requestDto) {
                TimeSlotResponseDto response = timeSlotService.createTimeSlot(requestDto);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ResponseUtil.buildCreatedResponse(response, AppConstants.MSG_CREATED));
        }

        @GetMapping("/timeslots")
        @Operation(summary = "Get All TimeSlots", description = "Returns all master time slot records")
        public ResponseEntity<ApiResponse<?>> getAllTimeSlots() {
                List<TimeSlotResponseDto> response = timeSlotService.getAllTimeSlots();
                return ResponseEntity.ok(ResponseUtil.buildOkResponse(response, AppConstants.MSG_FETCHED));
        }

        @GetMapping("/timeslots/{id}")
        @Operation(summary = "Get TimeSlot by ID")
        public ResponseEntity<ApiResponse<?>> getTimeSlotById(@PathVariable Long id) {
                TimeSlotResponseDto response = timeSlotService.getTimeSlotById(id);
                return ResponseEntity.ok(ResponseUtil.buildOkResponse(response, AppConstants.MSG_FETCHED));
        }

        @GetMapping("/timeslots/search")
        @Operation(summary = "Search TimeSlots by Label")
        public ResponseEntity<ApiResponse<?>> getTimeSlotsByLabel(@RequestParam String label) {
                List<TimeSlotResponseDto> response = timeSlotService.getTimeSlotsByLabel(label);
                return ResponseEntity.ok(ResponseUtil.buildOkResponse(response, AppConstants.MSG_FETCHED));
        }

        @GetMapping("/timeslots/time-range")
        @Operation(summary = "Get TimeSlots by Time Range", description = "Returns timeslots whose start/end falls within the given HH:mm range")
        public ResponseEntity<ApiResponse<?>> getTimeSlotsByTimeRange(
                        @RequestParam String startTime,
                        @RequestParam String endTime) {
                List<TimeSlotResponseDto> response = timeSlotService.getTimeSlotsByTimeRange(startTime, endTime);
                return ResponseEntity.ok(ResponseUtil.buildOkResponse(response, AppConstants.MSG_FETCHED));
        }

        @PutMapping("/timeslots/{id}")
        @Operation(summary = "Update TimeSlot")
        public ResponseEntity<ApiResponse<?>> updateTimeSlot(
                        @PathVariable Long id,
                        @Valid @RequestBody TimeSlotRequestDto requestDto) {
                TimeSlotResponseDto response = timeSlotService.updateTimeSlot(id, requestDto);
                return ResponseEntity.ok(ResponseUtil.buildOkResponse(response, AppConstants.MSG_UPDATED));
        }

        @DeleteMapping("/timeslots/{id}")
        @Operation(summary = "Delete TimeSlot (soft)", description = "Soft-deletes the master time slot record")
        public ResponseEntity<ApiResponse<?>> deleteTimeSlot(@PathVariable Long id) {
                timeSlotService.deleteTimeSlot(id);
                return ResponseEntity.ok(ResponseUtil.buildOkResponse(null, AppConstants.MSG_DELETED));
        }

        @PatchMapping("/timeslots/{id}/toggle")
        @Operation(summary = "Toggle TimeSlot Active Status")
        public ResponseEntity<ApiResponse<?>> toggleTimeSlotActive(@PathVariable Long id) {
                timeSlotService.toggleTimeSlotActive(id);
                return ResponseEntity.ok(ResponseUtil.buildOkResponse(null, "TimeSlot status toggled successfully"));
        }

        // ── Experience-Location Attachment ────────────────────────────────────────

        @GetMapping("/experiences/{experienceId}/locations/{locationId}/timeslots")
        @Operation(summary = "Get TimeSlots for Experience-Location", description = "Lists all timeslots attached to a specific experience-location pair with pricing and capacity")
        public ResponseEntity<ApiResponse<?>> getTimeSlotsForExperienceLocation(
                        @PathVariable Long experienceId,
                        @PathVariable Long locationId) {
                List<ExperienceTimeSlotResponseDto> response = timeSlotService
                                .getTimeSlotsForExperienceLocation(experienceId, locationId);
                return ResponseEntity.ok(ResponseUtil.buildOkResponse(response, AppConstants.MSG_FETCHED));
        }

        @PostMapping("/experiences/{experienceId}/locations/{locationId}/timeslots/{timeSlotId}")
        @Operation(summary = "Attach TimeSlot to Experience-Location", description = "Links a master TimeSlot to an experience-location pair. "
                        + "Optional body: priceOverride (null = no override), maxCapacity, validFrom, validTo")
        public ResponseEntity<ApiResponse<?>> attachTimeSlot(
                        @PathVariable Long experienceId,
                        @PathVariable Long locationId,
                        @PathVariable Long timeSlotId,
                        @RequestBody(required = false) @Valid ExperienceTimeSlotAttachRequestDto requestDto) {
                if (requestDto == null)
                        requestDto = new ExperienceTimeSlotAttachRequestDto();
                ExperienceTimeSlotResponseDto response = timeSlotService.attachTimeSlot(experienceId, locationId,
                                timeSlotId, requestDto);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ResponseUtil.buildCreatedResponse(response, AppConstants.MSG_CREATED));
        }

        @PutMapping("/experiences/{experienceId}/locations/{locationId}/timeslots/{timeSlotId}")
        @Operation(summary = "Update TimeSlot Attachment", description = "Updates priceOverride, maxCapacity, or validity dates for an existing attachment")
        public ResponseEntity<ApiResponse<?>> updateAttachment(
                        @PathVariable Long experienceId,
                        @PathVariable Long locationId,
                        @PathVariable Long timeSlotId,
                        @Valid @RequestBody ExperienceTimeSlotAttachRequestDto requestDto) {
                ExperienceTimeSlotResponseDto response = timeSlotService.updateAttachment(experienceId, locationId,
                                timeSlotId, requestDto);
                return ResponseEntity.ok(ResponseUtil.buildOkResponse(response, AppConstants.MSG_UPDATED));
        }

        @DeleteMapping("/experiences/{experienceId}/locations/{locationId}/timeslots/{timeSlotId}")
        @Operation(summary = "Detach TimeSlot from Experience-Location", description = "Soft-deletes the junction row only. Master TimeSlot record is NOT deleted.")
        public ResponseEntity<ApiResponse<?>> detachTimeSlot(
                        @PathVariable Long experienceId,
                        @PathVariable Long locationId,
                        @PathVariable Long timeSlotId) {
                timeSlotService.detachTimeSlot(experienceId, locationId, timeSlotId);
                return ResponseEntity.ok(ResponseUtil.buildOkResponse(null, AppConstants.MSG_DELETED));
        }

        @PatchMapping("/experiences/{experienceId}/locations/{locationId}/timeslots/{mapperId}/toggle")
        @Operation(summary = "Toggle TimeSlot Attachment Active", description = "Toggles is_active on a junction mapping row by its mapperId")
        public ResponseEntity<ApiResponse<?>> toggleAttachmentActive(
                        @PathVariable Long experienceId,
                        @PathVariable Long locationId,
                        @PathVariable Long mapperId) {
                timeSlotService.toggleAttachmentActive(mapperId);
                return ResponseEntity.ok(
                                ResponseUtil.buildOkResponse(null, "TimeSlot mapping status toggled successfully"));
        }

        @PatchMapping("/reorder")
        public ResponseEntity<ApiResponse<?>> reOrderTheItems(
                @RequestBody ReorderRequestDto reorderRequestDto) {
                reorderingService.reorderItems(reorderRequestDto.getId(), reorderRequestDto.getNewPosition(), TimeSlot.class);
                return ResponseEntity.ok(
                        ResponseUtil.buildOkResponse(null, AppConstants.MSG_UPDATED));
        }
}
