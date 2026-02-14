package com.forvmom.data.dao;

import com.forvmom.data.entities.SubCategory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class SubCategoryDaoImpl extends GenericDaoImpl<SubCategory, Long>
        implements SubCategoryDao {

    public SubCategoryDaoImpl() {
        super(SubCategory.class);
    }

    @Override
    public List<SubCategory> findByName(String name) {
        return em.createQuery(
                "from SubCategory where name = :name",
                SubCategory.class).setParameter("name", name)
                .getResultList();
    }

    @Override
    public boolean existsByName(String name) {
        Long count = em.createQuery(
                "SELECT COUNT(s) FROM SubCategory s WHERE s.name = :name",
                Long.class).setParameter("name", name)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public List<SubCategory> findByCategoryId(Long categoryId) {
        return em.createQuery(
                "from SubCategory s where s.category.id = :categoryId",
                SubCategory.class).setParameter("categoryId", categoryId)
                .getResultList();
    }

    @Override
    public List<SubCategory> findBySlug(String slug) {
        return em.createQuery(
                "from SubCategory where slug = :slug",
                SubCategory.class).setParameter("slug", slug)
                .getResultList();
    }

    @Override
    public boolean existsBySlug(String slug) {
        Long count = em.createQuery(
                "SELECT COUNT(s) FROM SubCategory s WHERE s.slug = :slug",
                Long.class).setParameter("slug", slug)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public List<SubCategory> findAllWithCategory() {
        return em.createQuery(
                "SELECT DISTINCT s FROM SubCategory s LEFT JOIN FETCH s.category ORDER BY s.displayOrder ASC",
                SubCategory.class).getResultList();
    }

    @Override
    public SubCategory findByIdWithCategory(Long id) {
        try {
            return em.createQuery(
                    "SELECT DISTINCT s FROM SubCategory s LEFT JOIN FETCH s.category WHERE s.id = :id",
                    SubCategory.class).setParameter("id", id)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<SubCategory> findByCategoryIdWithCategory(Long categoryId) {
        return em.createQuery(
                "SELECT DISTINCT s FROM SubCategory s LEFT JOIN FETCH s.category WHERE s.category.id = :categoryId ORDER BY s.displayOrder ASC",
                SubCategory.class).setParameter("categoryId", categoryId)
                .getResultList();
    }

    @Override
    public List<SubCategory> findBySlugWithCategory(String slug) {
        return em.createQuery(
                "SELECT DISTINCT s FROM SubCategory s LEFT JOIN FETCH s.category WHERE s.slug = :slug",
                SubCategory.class).setParameter("slug", slug)
                .getResultList();
    }
}