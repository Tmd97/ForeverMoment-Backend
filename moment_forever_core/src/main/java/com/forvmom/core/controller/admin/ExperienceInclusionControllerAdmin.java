package com.forvmom.core.controller.admin;

import com.forvmom.common.dto.request.ExperienceInclusionRequestDto;
import com.forvmom.common.dto.response.ExperienceInclusionResponseDto;
import com.forvmom.core.services.ExperienceInclusionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin controller for managing master ExperienceInclusion records
 * and attaching/detaching them to specific experiences.
 *
 * Master CRUD → /api/admin/inclusions
 * Experience attachment → /api/admin/experiences/{experienceId}/inclusions
 */
@RestController
@RequestMapping("/admin")
@Tag(name = "Admin Inclusion API", description = "Master CRUD for experience inclusions and per-experience attachment (Admin only)")
public class ExperienceInclusionControllerAdmin {

    @Autowired
    private ExperienceInclusionService inclusionService;

    // ── Master CRUD ──────────────────────────────────────────────────────────

    /** Create a new reusable master inclusion item */
    @PostMapping("/inclusions")
    @Operation(summary = "Create Inclusion", description = "Creates a reusable master inclusion record")
    public ResponseEntity<ExperienceInclusionResponseDto> createInclusion(
            @Valid @RequestBody ExperienceInclusionRequestDto requestDto) {
        return ResponseEntity.ok(inclusionService.createInclusion(requestDto));
    }

    /** List all master inclusion items (admin library) */
    @GetMapping("/inclusions")
    @Operation(summary = "Get All Inclusions", description = "Returns all master inclusion records")
    public ResponseEntity<List<ExperienceInclusionResponseDto>> getAllInclusions() {
        return ResponseEntity.ok(inclusionService.getAllInclusions());
    }

    /** Update a master inclusion item */
    @PutMapping("/inclusions/{id}")
    @Operation(summary = "Update Inclusion")
    public ResponseEntity<ExperienceInclusionResponseDto> updateInclusion(
            @PathVariable Long id,
            @Valid @RequestBody ExperienceInclusionRequestDto requestDto) {
        return ResponseEntity.ok(inclusionService.updateInclusion(id, requestDto));
    }

    /**
     * Soft-delete a master inclusion item.
     * Also soft-deletes all junction rows — removes it from ALL experiences.
     */
    @DeleteMapping("/inclusions/{id}")
    @Operation(summary = "Delete Inclusion (soft)", description = "Soft-deletes the master inclusion and all its junction rows")
    public ResponseEntity<Void> deleteInclusion(@PathVariable Long id) {
        inclusionService.deleteInclusion(id);
        return ResponseEntity.noContent().build();
    }

    // ── Experience Attachment ─────────────────────────────────────────────────

    /** List inclusions attached to a specific experience */
    @GetMapping("/experiences/{experienceId}/inclusions")
    @Operation(summary = "Get Inclusions for Experience", description = "Lists all inclusions attached to a given experience")
    public ResponseEntity<List<ExperienceInclusionResponseDto>> getInclusionsForExperience(
            @PathVariable Long experienceId) {
        return ResponseEntity.ok(inclusionService.getInclusionsForExperience(experienceId));
    }

    /**
     * Attach a master inclusion to an experience.
     * Optional query param ?displayOrder=X to set per-experience ordering.
     */
    @PostMapping("/experiences/{experienceId}/inclusions/{inclusionId}")
    @Operation(summary = "Attach Inclusion to Experience", description = "Links a master inclusion to an experience. Optional displayOrder param for ordering")
    public ResponseEntity<Void> attachInclusionToExperience(
            @PathVariable Long experienceId,
            @PathVariable Long inclusionId,
            @RequestParam(required = false) Integer displayOrder) {
        inclusionService.attachToExperience(experienceId, inclusionId, displayOrder);
        return ResponseEntity.ok().build();
    }

    /**
     * Detach an inclusion from one experience (junction row soft-deleted).
     * Master inclusion record is NOT affected.
     */
    @DeleteMapping("/experiences/{experienceId}/inclusions/{inclusionId}")
    @Operation(summary = "Detach Inclusion from Experience", description = "Removes the junction row only; master inclusion record is NOT deleted")
    public ResponseEntity<Void> detachInclusionFromExperience(
            @PathVariable Long experienceId,
            @PathVariable Long inclusionId) {
        inclusionService.detachFromExperience(experienceId, inclusionId);
        return ResponseEntity.noContent().build();
    }
}
