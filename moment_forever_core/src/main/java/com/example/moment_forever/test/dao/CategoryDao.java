package com.example.moment_forever.test.dao;

import com.example.moment_forever.data.entities.Category;
import java.util.List;

public interface CategoryDao extends GenericDao<Category, Long> {
    List<Category> findByName(String name);
    List<Category> findByActiveStatus(Boolean active);
    boolean existsByName(String name);
}