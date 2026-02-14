package com.example.moment_forever.data.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.moment_forever.data.entities.Category;
import java.util.List;

@Repository
@Transactional
public class CategoryDaoImpl extends GenericDaoImpl<Category, Long>
        implements CategoryDao {

    public CategoryDaoImpl() {
        super(Category.class);
    }

    @Override
    public List<Category> findByName(String name) {
        return em.createQuery(
                "from Category where name = :name",
                Category.class).setParameter("name", name)
                .getResultList();
    }

    @Override
    public boolean existsByName(String name) {
        Long count = em.createQuery(
                "SELECT COUNT(c) FROM Category c WHERE c.name = :name",
                Long.class).setParameter("name", name)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public List<Category> findAllWithSubCategories() {
        return em.createQuery(
                "SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.subCategories ORDER BY c.displayOrder ASC",
                Category.class).getResultList();
    }

    @Override
    public Category findByIdWithSubCategories(Long id) {
        try {
            return em.createQuery(
                    "SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.subCategories WHERE c.id = :id",
                    Category.class).setParameter("id", id)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}