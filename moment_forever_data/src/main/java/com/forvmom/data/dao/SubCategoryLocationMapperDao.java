package com.forvmom.data.dao;

import com.forvmom.data.entities.SubCategoryLocationMapper;
import java.util.List;

public interface SubCategoryLocationMapperDao extends GenericDao<SubCategoryLocationMapper, Long> {
    SubCategoryLocationMapper findBySubCategoryIdAndLocationId(Long subCategoryId, Long locationId);
    List<SubCategoryLocationMapper> findBySubCategoryId(Long subCategoryId);
    List<SubCategoryLocationMapper> findByLocationId(Long locationId);
    List<SubCategoryLocationMapper> findActiveByLocationId(Long locationId);
    List<SubCategoryLocationMapper> findActiveByLocationIdAndCategoryId(Long locationId, Long categoryId);
    boolean existsBySubCategoryIdAndLocationId(Long subCategoryId, Long locationId);
}