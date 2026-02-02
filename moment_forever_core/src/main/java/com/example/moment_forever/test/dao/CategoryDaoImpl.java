package com.example.moment_forever.test.dao;

import com.example.moment_forever.data.entities.Category;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoryDaoImpl extends GenericDaoImpl<Category, Long> implements CategoryDao {

    public CategoryDaoImpl() {
        super(Category.class);
    }

    @Override
    public List<Category> findByName(String name) {
        return entityManager.createQuery(
                        "SELECT c FROM Category c WHERE c.name = :name", Category.class)
                .setParameter("name", name)
                .getResultList();
    }

    @Override
    public List<Category> findByActiveStatus(Boolean active) {
        return entityManager.createQuery(
                        "SELECT c FROM Category c WHERE c.active = :active", Category.class)
                .setParameter("active", active)
                .getResultList();
    }

    @Override
    public boolean existsByName(String name) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(c) FROM Category c WHERE c.name = :name", Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return count != null && count > 0;
    }
}