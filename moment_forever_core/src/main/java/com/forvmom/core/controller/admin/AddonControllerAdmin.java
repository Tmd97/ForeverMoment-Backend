package com.forvmom.core.controller.admin;

import com.forvmom.common.dto.request.AddonRequestDto;
import com.forvmom.common.dto.response.AddonResponseDto;
import com.forvmom.core.services.AddonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Admin controller for managing master Addon records
 * and attaching/detaching them to specific experiences.
 *
 * Master CRUD → /api/admin/addons
 * Attachment → /api/admin/experiences/{experienceId}/addons
 */
//TODO Image upload, Every Addon has its own image to display with its details
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin Addon API", description = "Master CRUD for addons and per-experience attachment with price override (Admin only)")
public class AddonControllerAdmin {

    @Autowired
    private AddonService addonService;

    @PostMapping("/addons")
    @Operation(summary = "Create Addon", description = "Creates a reusable master addon record")
    public ResponseEntity<AddonResponseDto> createAddon(@Valid @RequestBody AddonRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addonService.createAddon(requestDto));
    }

    @GetMapping("/addons")
    @Operation(summary = "Get All Addons", description = "Returns all master addon records")
    public ResponseEntity<List<AddonResponseDto>> getAllAddons() {
        return ResponseEntity.ok(addonService.getAllAddons());
    }

    @PutMapping("/addons/{id}")
    @Operation(summary = "Update Addon")
    public ResponseEntity<AddonResponseDto> updateAddon(
            @PathVariable Long id,
            @Valid @RequestBody AddonRequestDto requestDto) {
        return ResponseEntity.ok(addonService.updateAddon(id, requestDto));
    }

    @DeleteMapping("/addons/{id}")
    @Operation(summary = "Delete Addon (soft)", description = "Soft-deletes the master addon and all its junction rows")
    public ResponseEntity<Void> deleteAddon(@PathVariable Long id) {
        addonService.deleteAddon(id);
        return ResponseEntity.noContent().build();
    }

    // ── Experience Attachment ─────────────────────────────────────────────────

    @GetMapping("/experiences/{experienceId}/addons")
    @Operation(summary = "Get Addons for Experience", description = "Lists all addons attached to a given experience with effective pricing")
    public ResponseEntity<List<AddonResponseDto>> getAddonsForExperience(@PathVariable Long experienceId) {
        return ResponseEntity.ok(addonService.getAddonsForExperience(experienceId));
    }

    /**
     * Attach a master addon to an experience.
     * Optional query params:
     * ?priceOverride=199.00 → per-experience price (null = use addon base price)
     * ?isFree=true → marks addon as complimentary
     */
    @PostMapping("/experiences/{experienceId}/addons/{addonId}")
    @Operation(summary = "Attach Addon to Experience", description = "Links a master addon to an experience. Optional priceOverride and isFree params")
    public ResponseEntity<Void> attachAddonToExperience(
            @PathVariable Long experienceId,
            @PathVariable Long addonId,
            @RequestParam(required = false) BigDecimal priceOverride,
            @RequestParam(required = false, defaultValue = "false") Boolean isFree) {
        addonService.attachToExperience(experienceId, addonId, priceOverride, isFree);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/experiences/{experienceId}/addons/{addonId}")
    @Operation(summary = "Detach Addon from Experience", description = "Removes the junction row only; master addon record is NOT deleted")
    public ResponseEntity<Void> detachAddonFromExperience(
            @PathVariable Long experienceId,
            @PathVariable Long addonId) {
        addonService.detachFromExperience(experienceId, addonId);
        return ResponseEntity.noContent().build();
    }
}
