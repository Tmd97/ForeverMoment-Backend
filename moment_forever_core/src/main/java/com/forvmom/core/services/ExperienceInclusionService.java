package com.forvmom.core.services;

import com.forvmom.common.dto.request.ExperienceInclusionRequestDto;
import com.forvmom.common.dto.response.ExperienceInclusionResponseDto;

import java.util.List;

public interface ExperienceInclusionService {

    /** Create a new reusable master inclusion */
    ExperienceInclusionResponseDto createInclusion(ExperienceInclusionRequestDto requestDto);

    /** List all master inclusions (admin library) */
    List<ExperienceInclusionResponseDto> getAllInclusions();

    /** Update a master inclusion record */
    ExperienceInclusionResponseDto updateInclusion(Long id, ExperienceInclusionRequestDto requestDto);

    /**
     * Soft-delete master + all its junction rows (inclusion removed from all
     * experiences)
     */
    boolean deleteInclusion(Long id);

    /** Attach a master inclusion to an experience (creates junction row) */
    void attachToExperience(Long experienceId, Long inclusionId, Integer displayOrder);

    /**
     * Detach an inclusion from one experience only (removes junction row, master
     * stays)
     */
    void detachFromExperience(Long experienceId, Long inclusionId);

    /** Get all inclusions attached to an experience (for detail response) */
    List<ExperienceInclusionResponseDto> getInclusionsForExperience(Long experienceId);
}
