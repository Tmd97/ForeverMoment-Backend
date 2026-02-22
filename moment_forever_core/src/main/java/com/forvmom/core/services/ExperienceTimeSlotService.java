package com.forvmom.core.services;

import com.forvmom.common.dto.request.ExperienceTimeSlotAttachRequestDto;
import com.forvmom.common.dto.request.TimeSlotRequestDto;
import com.forvmom.common.dto.response.ExperienceTimeSlotResponseDto;
import com.forvmom.common.dto.response.TimeSlotResponseDto;

import java.util.List;

/**
 * Unified service for master TimeSlot CRUD + experience-location attachment.
 *
 * Mirrors ExperienceCancellationPolicyService which handles both
 * master record CRUD and experience association in one service.
 *
 * ER flow: Experience → ExperienceLocationMapper → ExperienceTimeSlotMapper
 */
public interface ExperienceTimeSlotService {

    // ── Master TimeSlot CRUD ──────────────────────────────────────────────────

    TimeSlotResponseDto createTimeSlot(TimeSlotRequestDto requestDto);

    List<TimeSlotResponseDto> getAllTimeSlots();

    TimeSlotResponseDto getTimeSlotById(Long id);

    List<TimeSlotResponseDto> getTimeSlotsByLabel(String label);

    List<TimeSlotResponseDto> getTimeSlotsByTimeRange(String startTime, String endTime);

    TimeSlotResponseDto updateTimeSlot(Long id, TimeSlotRequestDto requestDto);

    void deleteTimeSlot(Long id);

    void toggleTimeSlotActive(Long id);

    // ── Experience-Location Attachment ────────────────────────────────────────

    /**
     * Attaches a TimeSlot to an experience-location pair.
     * (experienceId + locationId) → service resolves ExperienceLocationMapper
     * internally.
     */
    ExperienceTimeSlotResponseDto attachTimeSlot(Long experienceId, Long locationId, Long timeSlotId,
            ExperienceTimeSlotAttachRequestDto requestDto);

    void detachTimeSlot(Long experienceId, Long locationId, Long timeSlotId);

    List<ExperienceTimeSlotResponseDto> getTimeSlotsForExperienceLocation(Long experienceId, Long locationId);

    ExperienceTimeSlotResponseDto updateAttachment(Long experienceId, Long locationId, Long timeSlotId,
            ExperienceTimeSlotAttachRequestDto requestDto);

    void toggleAttachmentActive(Long mapperId);
}
