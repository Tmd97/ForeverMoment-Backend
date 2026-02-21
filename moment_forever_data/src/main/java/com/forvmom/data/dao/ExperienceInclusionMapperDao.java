package com.forvmom.data.dao;

import com.forvmom.data.entities.ExperienceInclusionMapper;

import java.util.List;

public interface ExperienceInclusionMapperDao extends GenericDao<ExperienceInclusionMapper, Long> {

    /** All junction rows for an experience (with inclusion eagerly fetched) */
    List<ExperienceInclusionMapper> findByExperienceId(Long experienceId);

    /** Check if this inclusion is already attached to the experience */
    boolean existsByExperienceIdAndInclusionId(Long experienceId, Long inclusionId);

    /** Find specific junction row to detach */
    ExperienceInclusionMapper findByExperienceIdAndInclusionId(Long experienceId, Long inclusionId);

    /**
     * Soft-delete all junction rows for an experience (called on experience delete)
     */
    void deleteAllByExperienceId(Long experienceId);

    /**
     * Soft-delete all junction rows for a master inclusion (called on master
     * delete)
     */
    void deleteAllByInclusionId(Long inclusionId);
}
