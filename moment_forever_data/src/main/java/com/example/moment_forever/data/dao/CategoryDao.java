package com.example.moment_forever.data.dao;


import com.example.moment_forever.data.entities.Category;
import java.util.List;

public interface CategoryDao extends GenericDao<Category, Long> {

    List<Category> findByName(String name);
    boolean existsByName(String name);
}