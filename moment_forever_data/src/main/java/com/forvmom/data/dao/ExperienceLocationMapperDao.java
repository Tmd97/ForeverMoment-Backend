package com.forvmom.data.dao;

import com.forvmom.data.entities.Experience;
import com.forvmom.data.entities.ExperienceLocationMapper;
import com.forvmom.data.entities.Location;

import java.util.List;

public interface ExperienceLocationMapperDao extends GenericDao<ExperienceLocationMapper, Long> {

    List<ExperienceLocationMapper> findByExperienceId(Long experienceId);

    List<ExperienceLocationMapper> findByLocationId(Long locationId);

    boolean existsByExperienceIdAndLocationId(Long experienceId, Long locationId);

    ExperienceLocationMapper findByExperienceIdAndLocationId(Long experienceId, Long locationId);

    /** Soft-delete all location mappers for an experience (on experience delete) */
    void deleteAllByExperienceId(Long experienceId);

    /**
     * Returns the existing {@code ExperienceLocationMapper} for the given
     * (experience, location) pair, or creates and persists a new one if it
     * doesn't exist yet.
     *
     * <p>
     * The new row is flushed immediately so its database-generated ID is
     * available for subsequent FK references in the same transaction.
     *
     * @param experience the experience entity (must be persistent)
     * @param location   the location entity (must be persistent)
     * @return the existing or newly created mapper
     */
    ExperienceLocationMapper findOrCreate(Experience experience,
                                          Location location);
}
