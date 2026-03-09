package com.forvmom.data.dao;

import com.forvmom.data.entities.CategoryLocationMapper;
import java.util.List;

public interface CategoryLocationMapperDao extends GenericDao<CategoryLocationMapper, Long> {
    CategoryLocationMapper findByCategoryIdAndLocationId(Long categoryId, Long locationId);
    List<CategoryLocationMapper> findByCategoryId(Long categoryId);
    List<CategoryLocationMapper> findByLocationId(Long locationId);
    List<CategoryLocationMapper> findActiveByLocationId(Long locationId);
    boolean existsByCategoryIdAndLocationId(Long categoryId, Long locationId);
    void softDeleteByCategoryIdAndLocationId(Long categoryId, Long locationId); // optional
}