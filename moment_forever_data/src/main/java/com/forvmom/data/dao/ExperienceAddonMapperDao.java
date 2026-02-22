package com.forvmom.data.dao;

import com.forvmom.data.entities.ExperienceAddonMapper;

import java.util.List;

public interface ExperienceAddonMapperDao extends GenericDao<ExperienceAddonMapper, Long> {

    List<ExperienceAddonMapper> findByExperienceId(Long experienceId);

    boolean existsByExperienceIdAndAddonId(Long experienceId, Long addonId);

    ExperienceAddonMapper findByExperienceIdAndAddonId(Long experienceId, Long addonId);

    /**
     * Soft-delete all addon mappers for an experience (called on experience delete)
     */
    void deleteAllByExperienceId(Long experienceId);

    /**
     * Soft-delete all addon mappers for an addon (called on master addon delete)
     */
    void deleteAllByAddonId(Long addonId);
}
