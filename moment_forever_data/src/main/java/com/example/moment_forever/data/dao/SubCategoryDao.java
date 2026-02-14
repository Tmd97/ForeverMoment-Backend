package com.example.moment_forever.data.dao;

import com.example.moment_forever.data.entities.SubCategory;
import java.util.List;

public interface SubCategoryDao extends GenericDao<SubCategory, Long> {

    List<SubCategory> findByName(String name);

    boolean existsByName(String name);

    List<SubCategory> findByCategoryId(Long categoryId);

    List<SubCategory> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<SubCategory> findAllWithCategory();

    SubCategory findByIdWithCategory(Long id);

    List<SubCategory> findByCategoryIdWithCategory(Long categoryId);

    List<SubCategory> findBySlugWithCategory(String slug);
}