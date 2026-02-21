package com.forvmom.core.mapper;

import com.forvmom.common.dto.request.CancellationPolicyRequestDto;
import com.forvmom.common.dto.request.ExperienceInclusionRequestDto;
import com.forvmom.common.dto.response.CancellationPolicyResponseDto;
import com.forvmom.common.dto.response.ExperienceInclusionResponseDto;
import com.forvmom.data.entities.ExperienceCancellationPolicy;
import com.forvmom.data.entities.ExperienceCancellationPolicyMapper;
import com.forvmom.data.entities.ExperienceInclusion;
import com.forvmom.data.entities.ExperienceInclusionMapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Stateless mapper for ExperienceInclusion and ExperienceCancellationPolicy
 * entities/DTOs.
 */
public class InclusionPolicyBeanMapper {

    private InclusionPolicyBeanMapper() {
    }

    // ── ExperienceInclusion ──────────────────────────────────────────────────

    public static ExperienceInclusion mapRequestToInclusion(ExperienceInclusionRequestDto dto) {
        ExperienceInclusion entity = new ExperienceInclusion();
        return updateInclusionFromRequest(entity, dto);
    }

    public static ExperienceInclusion updateInclusionFromRequest(ExperienceInclusion entity,
            ExperienceInclusionRequestDto dto) {
        entity.setDescription(dto.getDescription());
        entity.setIsIncluded(dto.getIsIncluded() != null ? dto.getIsIncluded() : true);
        entity.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);
        entity.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return entity;
    }

    public static ExperienceInclusionResponseDto mapInclusionToDto(ExperienceInclusion entity) {
        if (entity == null)
            return null;
        ExperienceInclusionResponseDto dto = new ExperienceInclusionResponseDto();
        dto.setId(entity.getId());
        dto.setDescription(entity.getDescription());
        dto.setIsIncluded(entity.getIsIncluded());
        dto.setDisplayOrder(entity.getDisplayOrder());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedOn(entity.getCreatedOn());
        return dto;
    }

    /**
     * Maps junction rows to flat inclusion DTOs — uses the master inclusion's data
     * but overrides displayOrder with the per-experience display_order.
     */
    public static List<ExperienceInclusionResponseDto> mapInclusionMappers(
            List<ExperienceInclusionMapper> mappers) {
        if (mappers == null || mappers.isEmpty())
            return Collections.emptyList();
        return mappers.stream().map(m -> {
            ExperienceInclusionResponseDto dto = mapInclusionToDto(m.getInclusion());
            if (dto != null)
                dto.setDisplayOrder(m.getDisplayOrder()); // per-experience order
            return dto;
        }).collect(Collectors.toList());
    }

    // ── ExperienceCancellationPolicy ─────────────────────────────────────────

    public static ExperienceCancellationPolicy mapRequestToPolicy(CancellationPolicyRequestDto dto) {
        ExperienceCancellationPolicy entity = new ExperienceCancellationPolicy();
        return updatePolicyFromRequest(entity, dto);
    }

    public static ExperienceCancellationPolicy updatePolicyFromRequest(ExperienceCancellationPolicy entity,
            CancellationPolicyRequestDto dto) {
        entity.setDescription(dto.getDescription());
        entity.setIsIncluded(dto.getIsIncluded() != null ? dto.getIsIncluded() : true);
        entity.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);
        entity.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return entity;
    }

    public static CancellationPolicyResponseDto mapPolicyToDto(ExperienceCancellationPolicy entity) {
        if (entity == null)
            return null;
        CancellationPolicyResponseDto dto = new CancellationPolicyResponseDto();
        dto.setId(entity.getId());
        dto.setDescription(entity.getDescription());
        dto.setIsIncluded(entity.getIsIncluded());
        dto.setDisplayOrder(entity.getDisplayOrder());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedOn(entity.getCreatedOn());
        return dto;
    }

    /**
     * Maps junction rows to flat policy DTOs — overrides displayOrder with
     * per-experience value.
     */
    public static List<CancellationPolicyResponseDto> mapPolicyMappers(
            List<ExperienceCancellationPolicyMapper> mappers) {
        if (mappers == null || mappers.isEmpty())
            return Collections.emptyList();
        return mappers.stream().map(m -> {
            CancellationPolicyResponseDto dto = mapPolicyToDto(m.getPolicy());
            if (dto != null)
                dto.setDisplayOrder(m.getDisplayOrder()); // per-experience order
            return dto;
        }).collect(Collectors.toList());
    }
}
