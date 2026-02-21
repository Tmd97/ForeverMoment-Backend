package com.forvmom.core.controller.admin;

import com.forvmom.common.dto.request.CancellationPolicyRequestDto;
import com.forvmom.common.dto.response.CancellationPolicyResponseDto;
import com.forvmom.core.services.ExperienceCancellationPolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin controller for managing master ExperienceCancellationPolicy records
 * and attaching/detaching them to specific experiences.
 *
 * Master CRUD → /api/admin/cancellation-policies
 * Experience attachment →
 * /api/admin/experiences/{experienceId}/cancellation-policies
 */
@RestController
@RequestMapping("/admin")
@Tag(name = "Admin Cancellation Policy API", description = "Master CRUD for cancellation policies and per-experience attachment (Admin only)")
public class ExperienceCancellationPolicyControllerAdmin {

    @Autowired
    private ExperienceCancellationPolicyService policyService;

    /** Create a new reusable master cancellation policy point */
    @PostMapping("/cancellation-policies")
    @Operation(summary = "Create Cancellation Policy", description = "Creates a reusable master cancellation policy record")
    public ResponseEntity<CancellationPolicyResponseDto> createPolicy(
            @Valid @RequestBody CancellationPolicyRequestDto requestDto) {
        return ResponseEntity.ok(policyService.createPolicy(requestDto));
    }

    /** List all master cancellation policy points (admin library) */
    @GetMapping("/cancellation-policies")
    @Operation(summary = "Get All Cancellation Policies", description = "Returns all master cancellation policy records")
    public ResponseEntity<List<CancellationPolicyResponseDto>> getAllPolicies() {
        return ResponseEntity.ok(policyService.getAllPolicies());
    }

    /** Update a master cancellation policy point */
    @PutMapping("/cancellation-policies/{id}")
    @Operation(summary = "Update Cancellation Policy")
    public ResponseEntity<CancellationPolicyResponseDto> updatePolicy(
            @PathVariable Long id,
            @Valid @RequestBody CancellationPolicyRequestDto requestDto) {
        return ResponseEntity.ok(policyService.updatePolicy(id, requestDto));
    }

    /**
     * Soft-delete a master cancellation policy point.
     * Also soft-deletes all junction rows — removes it from ALL experiences.
     */
    @DeleteMapping("/cancellation-policies/{id}")
    @Operation(summary = "Delete Cancellation Policy (soft)", description = "Soft-deletes the master policy and all its junction rows")
    public ResponseEntity<Void> deletePolicy(@PathVariable Long id) {
        policyService.deletePolicy(id);
        return ResponseEntity.noContent().build();
    }

    // ── Experience Attachment ─────────────────────────────────────────────────

    /** List cancellation policies attached to a specific experience */
    @GetMapping("/experiences/{experienceId}/cancellation-policies")
    @Operation(summary = "Get Policies for Experience", description = "Lists all cancellation policies attached to a given experience")
    public ResponseEntity<List<CancellationPolicyResponseDto>> getPoliciesForExperience(
            @PathVariable Long experienceId) {
        return ResponseEntity.ok(policyService.getPoliciesForExperience(experienceId));
    }

    /**
     * Attach a master policy to an experience.
     * Optional query param ?displayOrder=X to set per-experience ordering.
     */
    @PostMapping("/experiences/{experienceId}/cancellation-policies/{policyId}")
    @Operation(summary = "Attach Policy to Experience", description = "Links a master policy to an experience. Optional displayOrder param for ordering")
    public ResponseEntity<Void> attachPolicyToExperience(
            @PathVariable Long experienceId,
            @PathVariable Long policyId,
            @RequestParam(required = false) Integer displayOrder) {
        policyService.attachToExperience(experienceId, policyId, displayOrder);
        return ResponseEntity.ok().build();
    }

    /**
     * Detach a policy from one experience (junction row soft-deleted).
     * Master policy record is NOT affected.
     */
    @DeleteMapping("/experiences/{experienceId}/cancellation-policies/{policyId}")
    @Operation(summary = "Detach Policy from Experience", description = "Removes the junction row only; master policy record is NOT deleted")
    public ResponseEntity<Void> detachPolicyFromExperience(
            @PathVariable Long experienceId,
            @PathVariable Long policyId) {
        policyService.detachFromExperience(experienceId, policyId);
        return ResponseEntity.noContent().build();
    }
}
