package com.forvmom.data.dao;

import com.forvmom.data.entities.ExperienceCancellationPolicyMapper;

import java.util.List;

public interface ExperienceCancellationPolicyMapperDao extends GenericDao<ExperienceCancellationPolicyMapper, Long> {

    /** All junction rows for an experience (with policy eagerly fetched) */
    List<ExperienceCancellationPolicyMapper> findByExperienceId(Long experienceId);

    /** Check if this policy is already attached to the experience */
    boolean existsByExperienceIdAndPolicyId(Long experienceId, Long policyId);

    /** Find specific junction row to detach */
    ExperienceCancellationPolicyMapper findByExperienceIdAndPolicyId(Long experienceId, Long policyId);

    /**
     * Soft-delete all junction rows for an experience (called on experience delete)
     */
    void deleteAllByExperienceId(Long experienceId);

    /**
     * Soft-delete all junction rows for a master policy (called on master delete)
     */
    void deleteAllByPolicyId(Long policyId);
}
