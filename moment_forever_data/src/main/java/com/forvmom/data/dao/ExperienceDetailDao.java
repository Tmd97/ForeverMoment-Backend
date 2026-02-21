package com.forvmom.data.dao;

import com.forvmom.data.entities.ExperienceDetail;

public interface ExperienceDetailDao extends GenericDao<ExperienceDetail, Long> {

    ExperienceDetail findByExperienceId(Long experienceId);

    boolean existsByExperienceId(Long experienceId);
}
