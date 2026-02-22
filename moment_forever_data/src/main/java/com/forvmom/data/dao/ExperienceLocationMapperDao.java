package com.forvmom.data.dao;

import com.forvmom.data.entities.ExperienceLocationMapper;

import java.util.List;

public interface ExperienceLocationMapperDao extends GenericDao<ExperienceLocationMapper, Long> {

    List<ExperienceLocationMapper> findByExperienceId(Long experienceId);

    List<ExperienceLocationMapper> findByLocationId(Long locationId);

    boolean existsByExperienceIdAndLocationId(Long experienceId, Long locationId);

    ExperienceLocationMapper findByExperienceIdAndLocationId(Long experienceId, Long locationId);

    /** Soft-delete all location mappers for an experience (on experience delete) */
    void deleteAllByExperienceId(Long experienceId);
}
