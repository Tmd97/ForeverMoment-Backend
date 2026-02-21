package com.forvmom.core.mapper;

import com.forvmom.common.dto.request.ExperienceCreateRequestDto;
import com.forvmom.common.dto.request.ExperienceDetailRequestDto;
import com.forvmom.common.dto.request.ExperienceRequestDto;
import com.forvmom.common.dto.response.ExperienceDetailResponseDto;
import com.forvmom.common.dto.response.ExperienceHighlightResponseDto;
import com.forvmom.common.dto.response.ExperienceResponseDto;
import com.forvmom.data.entities.Experience;
import com.forvmom.data.entities.ExperienceDetail;

public class ExperienceBeanMapper {

    // ─── Experience mapping ───────────────────────────────────────────────────

    /**
     * Maps basic Experience fields from ExperienceRequestDto (used for standalone
     * update)
     */
    public static void mapDtoToEntity(ExperienceRequestDto dto, Experience entity) {
        if (dto == null || entity == null)
            return;
        entity.setName(dto.getName());
        entity.setSlug(dto.getSlug());
        entity.setTagName(dto.getTagName());
        entity.setBasePrice(dto.getBasePrice());
        entity.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);
        entity.setIsFeatured(dto.getIsFeatured() != null ? dto.getIsFeatured() : false);
        if (dto.getIsActive() != null)
            entity.setActive(dto.getIsActive());
    }

    /** Maps basic Experience fields from the combined create/update DTO */
    public static void mapCreateDtoToEntity(ExperienceCreateRequestDto dto, Experience entity) {
        if (dto == null || entity == null)
            return;
        entity.setName(dto.getName());
        entity.setSlug(dto.getSlug());
        entity.setTagName(dto.getTagName());
        entity.setBasePrice(dto.getBasePrice());
        entity.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);
        entity.setIsFeatured(dto.getIsFeatured() != null ? dto.getIsFeatured() : false);
        if (dto.getIsActive() != null)
            entity.setActive(dto.getIsActive());
    }

    /**
     * Maps entity to lightweight highlight DTO for list endpoints.
     * Surfaces shortDescription, durationMinutes, maxCapacity from detail (if
     * loaded)
     * without embedding the full detail block.
     */
    public static ExperienceHighlightResponseDto mapEntityToHighlightDto(Experience entity) {
        if (entity == null)
            return null;

        ExperienceHighlightResponseDto dto = new ExperienceHighlightResponseDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setSlug(entity.getSlug());
        dto.setTagName(entity.getTagName());
        dto.setBasePrice(entity.getBasePrice());
        dto.setDisplayOrder(entity.getDisplayOrder());
        dto.setIsFeatured(entity.getIsFeatured());
        dto.setIsActive(entity.isActive());
        dto.setCreatedOn(entity.getCreatedOn());
        dto.setUpdatedOn(entity.getUpdatedOn());

        if (entity.getSubCategory() != null) {
            dto.setSubCategoryId(entity.getSubCategory().getId());
            dto.setSubCategoryName(entity.getSubCategory().getName());
            if (entity.getSubCategory().getCategory() != null) {
                dto.setCategoryId(entity.getSubCategory().getCategory().getId());
                dto.setCategoryName(entity.getSubCategory().getCategory().getName());
            }
        }

        // Surface key detail fields for card display (no full detail object embedded)
        if (entity.getDetail() != null) {
            dto.setShortDescription(entity.getDetail().getShortDescription());
            dto.setDurationMinutes(entity.getDetail().getDurationMinutes());
            dto.setMaxCapacity(entity.getDetail().getMaxCapacity());
        }

        return dto;
    }

    /**
     * Maps entity to response DTO.
     * 
     * @param includeDetail if true, embeds the full ExperienceDetail block; false
     *                      for list views
     */
    // TODO: well fetching the subcategory while creating the experience, it is N+1 query issue?
    public static ExperienceResponseDto mapEntityToDto(Experience entity, boolean includeDetail) {
        if (entity == null)
            return null;

        ExperienceResponseDto dto = new ExperienceResponseDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setSlug(entity.getSlug());
        dto.setTagName(entity.getTagName());
        dto.setBasePrice(entity.getBasePrice());
        dto.setDisplayOrder(entity.getDisplayOrder());
        dto.setIsFeatured(entity.getIsFeatured());
        dto.setIsActive(entity.isActive());
        dto.setCreatedOn(entity.getCreatedOn());
        dto.setUpdatedOn(entity.getUpdatedOn());

        if (entity.getSubCategory() != null) {
            dto.setSubCategoryId(entity.getSubCategory().getId());
            dto.setSubCategoryName(entity.getSubCategory().getName());
            if (entity.getSubCategory().getCategory() != null) {
                dto.setCategoryId(entity.getSubCategory().getCategory().getId());
                dto.setCategoryName(entity.getSubCategory().getCategory().getName());
            }
        }

        if (includeDetail && entity.getDetail() != null) {
            dto.setDetail(mapDetailToDto(entity.getDetail()));
        }

        return dto;
    }

    /** Maps detail fields from the combined create DTO */
    public static void mapCreateDtoToDetail(ExperienceCreateRequestDto dto, ExperienceDetail entity) {
        if (dto == null || entity == null)
            return;
        entity.setShortDescription(dto.getShortDescription());
        entity.setDescription(dto.getDescription());
        entity.setDurationMinutes(dto.getDurationMinutes());
        entity.setMaxCapacity(dto.getMaxCapacity());
        entity.setMinAge(dto.getMinAge());
        entity.setCancellationPolicy(dto.getCancellationPolicy());
        entity.setTermsConditions(dto.getTermsConditions());
        entity.setInclusions(dto.getInclusions());
        entity.setExclusions(dto.getExclusions());
        entity.setWhatToBring(dto.getWhatToBring());
    }

    /** Maps detail fields from the standalone detail upsert DTO */
    public static void mapDetailDtoToEntity(ExperienceDetailRequestDto dto, ExperienceDetail entity) {
        if (dto == null || entity == null)
            return;
        entity.setShortDescription(dto.getShortDescription());
        entity.setDescription(dto.getDescription());
        entity.setDurationMinutes(dto.getDurationMinutes());
        entity.setMaxCapacity(dto.getMaxCapacity());
        entity.setMinAge(dto.getMinAge());
        entity.setCancellationPolicy(dto.getCancellationPolicy());
        entity.setTermsConditions(dto.getTermsConditions());
        entity.setInclusions(dto.getInclusions());
        entity.setExclusions(dto.getExclusions());
        entity.setWhatToBring(dto.getWhatToBring());
        if (dto.getIsActive() != null)
            entity.setIsActive(dto.getIsActive());
    }

    public static ExperienceDetailResponseDto mapDetailToDto(ExperienceDetail entity) {
        if (entity == null)
            return null;

        ExperienceDetailResponseDto dto = new ExperienceDetailResponseDto();
        dto.setId(entity.getId());
        dto.setExperienceId(entity.getExperience() != null ? entity.getExperience().getId() : null);
        dto.setShortDescription(entity.getShortDescription());
        dto.setDescription(entity.getDescription());
        dto.setDurationMinutes(entity.getDurationMinutes());
        dto.setMaxCapacity(entity.getMaxCapacity());
        dto.setMinAge(entity.getMinAge());
        dto.setCancellationPolicy(entity.getCancellationPolicy());
        dto.setTermsConditions(entity.getTermsConditions());
        dto.setInclusions(entity.getInclusions());
        dto.setExclusions(entity.getExclusions());
        dto.setWhatToBring(entity.getWhatToBring());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedOn(entity.getCreatedOn());
        dto.setUpdatedOn(entity.getUpdatedOn());
        return dto;
    }
}
