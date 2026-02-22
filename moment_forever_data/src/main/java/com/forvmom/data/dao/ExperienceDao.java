package com.forvmom.data.dao;

import com.forvmom.data.entities.Experience;

import java.util.List;

public interface ExperienceDao extends GenericDao<Experience, Long> {

    boolean existsBySlug(String slug);

    Experience findBySlug(String slug);

    Experience findByIdWithDetail(Long id);

    Experience findByIdWithPolicies(Long id);

    Experience findBySlugWithDetail(String slug);

    Experience findBySlugWithPolicies(String slug);

    /**
     * Query 3 of 3: loads locationMappers + their timeslot mappers (separate to
     * avoid Cartesian product)
     */
    Experience findByIdWithLocations(Long id);

    Experience findBySlugWithLocations(String slug);

    List<Experience> findAllWithDetail();

    List<Experience> findBySubCategoryId(Long subCategoryId);

    List<Experience> findFeatured();

    List<Experience> findAllActive();
}
