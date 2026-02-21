package com.forvmom.core.services;

import com.forvmom.common.dto.request.ExperienceCreateRequestDto;
import com.forvmom.common.dto.request.ExperienceDetailRequestDto;
import com.forvmom.common.dto.response.ExperienceDetailResponseDto;
import com.forvmom.common.dto.response.ExperienceHighlightResponseDto;
import com.forvmom.common.dto.response.ExperienceResponseDto;

import java.util.List;

public interface ExperienceService {

    /**
     * Creates both Experience and ExperienceDetail rows from one combined request
     */
    ExperienceResponseDto createExperience(ExperienceCreateRequestDto requestDto);

    /**
     * Updates both Experience and ExperienceDetail rows from one combined request
     */
    ExperienceResponseDto updateExperience(Long id, ExperienceCreateRequestDto requestDto);

    ExperienceResponseDto getById(Long id);

    ExperienceResponseDto getBySlug(String slug);

    /**
     * Returns lightweight list of all experiences (basic + highlight fields, no
     * full detail)
     */
    List<ExperienceHighlightResponseDto> getAll();

    /** Returns lightweight list of active experiences only */
    List<ExperienceHighlightResponseDto> getAllActive();

    /** Returns lightweight list filtered by sub-category */
    List<ExperienceHighlightResponseDto> getBySubCategory(Long subCategoryId);

    /** Returns lightweight list of featured active experiences */
    List<ExperienceHighlightResponseDto> getFeatured();

    boolean deleteExperience(Long id);

    void toggleActive(Long id);

    void toggleFeatured(Long id);

    // ExperienceDetail operations (upsert — create or update)
    ExperienceDetailResponseDto upsertDetail(Long experienceId, ExperienceDetailRequestDto requestDto);

    ExperienceDetailResponseDto getDetail(Long experienceId);
}
