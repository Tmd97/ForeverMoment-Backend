package com.forvmom.data.dao;

import com.forvmom.data.entities.Category;
import java.util.List;

public interface CategoryDao extends GenericDao<Category, Long> {

    List<Category> findByName(String name);

    boolean existsByName(String name);

    List<Category> findAllWithSubCategories();

    Category findByIdWithSubCategories(Long id);
}