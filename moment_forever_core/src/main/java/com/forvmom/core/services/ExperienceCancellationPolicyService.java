package com.forvmom.core.services;

import com.forvmom.common.dto.request.CancellationPolicyRequestDto;
import com.forvmom.common.dto.response.CancellationPolicyResponseDto;

import java.util.List;

public interface ExperienceCancellationPolicyService {

    /** Create a new reusable master policy */
    CancellationPolicyResponseDto createPolicy(CancellationPolicyRequestDto requestDto);

    /** List all master policies (admin library) */
    List<CancellationPolicyResponseDto> getAllPolicies();

    /** Update a master policy record */
    CancellationPolicyResponseDto updatePolicy(Long id, CancellationPolicyRequestDto requestDto);

    /**
     * Soft-delete master + all its junction rows (policy removed from all
     * experiences)
     */
    boolean deletePolicy(Long id);

    /** Attach a master policy to an experience (creates junction row) */
    void attachToExperience(Long experienceId, Long policyId, Integer displayOrder);

    /**
     * Detach a policy from one experience only (removes junction row, master stays)
     */
    void detachFromExperience(Long experienceId, Long policyId);

    /** Get all policies attached to an experience (for detail response) */
    List<CancellationPolicyResponseDto> getPoliciesForExperience(Long experienceId);
}
