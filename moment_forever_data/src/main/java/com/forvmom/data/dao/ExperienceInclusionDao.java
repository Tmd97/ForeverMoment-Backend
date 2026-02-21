package com.forvmom.data.dao;

import com.forvmom.data.entities.ExperienceInclusion;

import java.util.List;

public interface ExperienceInclusionDao extends GenericDao<ExperienceInclusion, Long> {

    /** All active master inclusions (for admin library listing) */
    List<ExperienceInclusion> findAll();

    boolean existsById(Long id);
}
