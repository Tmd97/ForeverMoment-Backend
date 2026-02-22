package com.forvmom.core.mapper;

import com.forvmom.common.dto.request.AddonRequestDto;
import com.forvmom.common.dto.response.AddonResponseDto;
import com.forvmom.data.entities.Addon;
import com.forvmom.data.entities.ExperienceAddonMapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AddonBeanMapper {

    private AddonBeanMapper() {
    }

    // ── Master Addon mapping ──────────────────────────────────────────────────

    /** Map master Addon entity → response DTO (no experience context) */
    public static AddonResponseDto mapAddonToDto(Addon addon) {
        if (addon == null)
            return null;
        AddonResponseDto dto = new AddonResponseDto();
        dto.setId(addon.getId());
        dto.setName(addon.getName());
        dto.setDescription(addon.getDescription());
        dto.setIcon(addon.getIcon());
        dto.setBasePrice(addon.getBasePrice());
        dto.setEffectivePrice(addon.getBasePrice()); // no override — effective = base
        dto.setIsFree(false);
        dto.setIsActive(addon.getIsActive());
        return dto;
    }

    /** Map junction mapper → DTO with per-experience pricing applied */
    public static AddonResponseDto mapAddonMapperToDto(ExperienceAddonMapper mapper) {
        if (mapper == null || mapper.getAddon() == null)
            return null;
        Addon addon = mapper.getAddon();
        AddonResponseDto dto = new AddonResponseDto();
        dto.setId(addon.getId());
        dto.setName(addon.getName());
        dto.setDescription(addon.getDescription());
        dto.setIcon(addon.getIcon());
        dto.setBasePrice(addon.getBasePrice());
        dto.setEffectivePrice(mapper.effectivePrice());
        dto.setIsFree(mapper.getIsFree());
        dto.setIsActive(addon.getIsActive());
        return dto;
    }

    /** Map a list of junction mappers → list of DTOs */
    public static List<AddonResponseDto> mapAddonMappers(List<ExperienceAddonMapper> mappers) {
        if (mappers == null || mappers.isEmpty())
            return Collections.emptyList();
        return mappers.stream()
                .map(AddonBeanMapper::mapAddonMapperToDto)
                .collect(Collectors.toList());
    }

    /** Map request DTO → new Addon entity */
    public static Addon mapRequestToAddon(AddonRequestDto dto) {
        Addon addon = new Addon();
        return applyRequest(dto, addon);
    }

    /** Apply request DTO fields onto existing Addon entity */
    public static Addon updateAddonFromRequest(Addon addon, AddonRequestDto dto) {
        return applyRequest(dto, addon);
    }

    private static Addon applyRequest(AddonRequestDto dto, Addon addon) {
        addon.setName(dto.getName());
        addon.setDescription(dto.getDescription());
        addon.setIcon(dto.getIcon());
        addon.setBasePrice(dto.getBasePrice());
        if (dto.getIsActive() != null)
            addon.setIsActive(dto.getIsActive());
        return addon;
    }
}
