package com.forvmom.core.mapper;

import com.forvmom.common.dto.request.ExperienceCreateRequestDto;
import com.forvmom.common.dto.request.ExperienceDetailRequestDto;
import com.forvmom.common.dto.request.ExperienceRequestDto;
import com.forvmom.common.dto.response.ExperienceDetailResponseDto;
import com.forvmom.common.dto.response.ExperienceHighlightResponseDto;
import com.forvmom.common.dto.response.ExperienceLocationResponseDto;
import com.forvmom.common.dto.response.ExperienceResponseDto;
import com.forvmom.common.dto.response.ExperienceTimeSlotResponseDto;
import com.forvmom.core.mapper.TimeSlotBeanMapper;
import com.forvmom.data.entities.Experience;
import com.forvmom.data.entities.ExperienceDetail;
import com.forvmom.data.entities.ExperienceLocationMapper;
import com.forvmom.data.entities.Location;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ExperienceBeanMapper {

    private ExperienceBeanMapper() {
    }

    // ─── Experience Entity Mapping ─────────────────────────────────────────────

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

    // ─── ExperienceDetail Mapping ─────────────────────────────────────────────

    /**
     * Maps detail fields from the combined create/update DTO.
     * Inclusions and cancellation policies are separate M:M relationships —
     * handled by ExperienceInclusionService / ExperienceCancellationPolicyService.
     */
    public static void mapCreateDtoToDetail(ExperienceCreateRequestDto dto, ExperienceDetail entity) {
        if (dto == null || entity == null)
            return;
        entity.setShortDescription(dto.getShortDescription());
        entity.setDescription(dto.getDescription());
        entity.setDurationMinutes(dto.getDurationMinutes());
        entity.setMaxCapacity(dto.getMaxCapacity());
        entity.setMinAge(dto.getMinAge());
        entity.setCompletionTime(dto.getCompletionTime());
        entity.setMinHours(dto.getMinHours());
        entity.setTermsConditions(dto.getTermsConditions());
        entity.setWhatToBring(dto.getWhatToBring());
    }

    public static void mapDetailDtoToEntity(ExperienceDetailRequestDto dto, ExperienceDetail entity) {
        if (dto == null || entity == null)
            return;
        entity.setShortDescription(dto.getShortDescription());
        entity.setDescription(dto.getDescription());
        entity.setDurationMinutes(dto.getDurationMinutes());
        entity.setMaxCapacity(dto.getMaxCapacity());
        entity.setMinAge(dto.getMinAge());
        entity.setCompletionTime(dto.getCompletionTime());
        entity.setMinHours(dto.getMinHours());
        entity.setTermsConditions(dto.getTermsConditions());
        entity.setWhatToBring(dto.getWhatToBring());
        if (dto.getIsActive() != null)
            entity.setIsActive(dto.getIsActive());
    }

    // ─── Response DTO Mapping ─────────────────────────────────────────────────

    /**
     * Lightweight DTO for list endpoints.
     * Surfaces shortDescription, durationMinutes, maxCapacity from detail (if
     * loaded).
     * Inclusions & cancellation policies are NOT included in list views.
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
        if (entity.getDetail() != null) {
            dto.setShortDescription(entity.getDetail().getShortDescription());
            dto.setDurationMinutes(entity.getDetail().getDurationMinutes());
            dto.setMaxCapacity(entity.getDetail().getMaxCapacity());
        }
        return dto;
    }

    /**
     * Full response DTO for single-item fetches (GET /experiences/{id} or
     * /slug/{slug}).
     * Inclusions and cancellation policies are populated separately by the service
     * via InclusionPolicyBeanMapper after this call.
     *
     * @param includeDetail pass true for single-item responses
     */
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
        // Note: inclusions and cancellationPolicies are set by ExperienceServiceImpl
        // after a separate JOIN FETCH query — keeping this mapper free of DAO calls.
        return dto;
    }

    public static ExperienceDetailResponseDto mapDetailToDto(ExperienceDetail entity) {
        if (entity == null)
            return null;
        ExperienceDetailResponseDto dto = new ExperienceDetailResponseDto();
        dto.setId(entity.getId());
        dto.setShortDescription(entity.getShortDescription());
        dto.setDescription(entity.getDescription());
        dto.setDurationMinutes(entity.getDurationMinutes());
        dto.setMaxCapacity(entity.getMaxCapacity());
        dto.setMinAge(entity.getMinAge());
        dto.setCompletionTime(entity.getCompletionTime());
        dto.setMinHours(entity.getMinHours());
        dto.setTermsConditions(entity.getTermsConditions());
        dto.setWhatToBring(entity.getWhatToBring());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedOn(entity.getCreatedOn());
        dto.setUpdatedOn(entity.getUpdatedOn());
        return dto;
    }

    // ─── Location + TimeSlot Mapping ─────────────────────────────────────────

    /**
     * Maps a single ExperienceLocationMapper junction row →
     * ExperienceLocationResponseDto
     * with nested timeslots.
     * Pattern mirrors InclusionPolicyBeanMapper.mapInclusionToDto.
     */
    public static ExperienceLocationResponseDto mapLocationMapperToDto(ExperienceLocationMapper mapper) {
        if (mapper == null)
            return null;
        ExperienceLocationResponseDto dto = new ExperienceLocationResponseDto();
        dto.setMapperId(mapper.getId());
        dto.setPriceOverride(mapper.getPriceOverride());
        dto.setValidFrom(mapper.getValidFrom());
        dto.setValidTo(mapper.getValidTo());
        dto.setIsActive(mapper.getIsActive());

        Location loc = mapper.getLocation();
        if (loc != null) {
            dto.setLocationId(loc.getId());
            dto.setLocationName(loc.getName());
            dto.setCity(loc.getCity());
            dto.setState(loc.getState());
            dto.setCountry(loc.getCountry());
            dto.setAddress(loc.getAddress());
            dto.setLatitude(loc.getLatitude());
            dto.setLongitude(loc.getLongitude());
        }

        // Nested timeslots — delegated to TimeSlotBeanMapper
        if (mapper.getTimeSlotMappers() != null && !mapper.getTimeSlotMappers().isEmpty()) {
            List<ExperienceTimeSlotResponseDto> timeslots = TimeSlotBeanMapper.mapMapperEntitiesToDto(
                    new java.util.ArrayList<>(mapper.getTimeSlotMappers()));
            dto.setTimeslots(timeslots);
        } else {
            dto.setTimeslots(Collections.emptyList());
        }
        return dto;
    }

    public static List<ExperienceLocationResponseDto> mapLocationMappers(
            List<ExperienceLocationMapper> mappers) {
        if (mappers == null || mappers.isEmpty())
            return Collections.emptyList();
        return mappers.stream()
                .map(ExperienceBeanMapper::mapLocationMapperToDto)
                .collect(Collectors.toList());
    }
}
