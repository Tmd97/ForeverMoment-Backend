package com.forvmom.data.dao;

import com.forvmom.data.entities.ExperienceCancellationPolicy;

import java.util.List;

public interface ExperienceCancellationPolicyDao extends GenericDao<ExperienceCancellationPolicy, Long> {

    /** All active master policies (for admin library listing) */
    List<ExperienceCancellationPolicy> findAll();

    boolean existsById(Long id);
}
