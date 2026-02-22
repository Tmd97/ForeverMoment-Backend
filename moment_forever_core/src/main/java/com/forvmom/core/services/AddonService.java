package com.forvmom.core.services;

import com.forvmom.common.dto.request.AddonRequestDto;
import com.forvmom.common.dto.response.AddonResponseDto;

import java.math.BigDecimal;
import java.util.List;

public interface AddonService {

    /** Create a new master addon record */
    AddonResponseDto createAddon(AddonRequestDto requestDto);

    /** Get all master addon records */
    List<AddonResponseDto> getAllAddons();

    /** Update a master addon record */
    AddonResponseDto updateAddon(Long id, AddonRequestDto requestDto);

    /**
     * Soft-delete a master addon record.
     * Also soft-deletes all junction rows to remove it from all experiences.
     */
    boolean deleteAddon(Long id);

    /**
     * Attach a master addon to an experience.
     * 
     * @param priceOverride null = use Addon.basePrice
     * @param isFree        true = complimentary regardless of price
     */
    void attachToExperience(Long experienceId, Long addonId, BigDecimal priceOverride, Boolean isFree);

    /**
     * Detach an addon from an experience (junction row soft-deleted; master stays)
     */
    void detachFromExperience(Long experienceId, Long addonId);

    /** List all addons attached to a specific experience with effective pricing */
    List<AddonResponseDto> getAddonsForExperience(Long experienceId);
}
