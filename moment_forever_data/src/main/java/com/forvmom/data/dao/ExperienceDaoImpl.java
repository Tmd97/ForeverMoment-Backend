package com.forvmom.data.dao;

import com.forvmom.data.entities.Experience;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class ExperienceDaoImpl extends GenericDaoImpl<Experience, Long> implements ExperienceDao {

    public ExperienceDaoImpl() {
        super(Experience.class);
    }

    @Override
    public boolean existsBySlug(String slug) {
        Long count = em.createQuery(
                "SELECT COUNT(e) FROM Experience e WHERE e.slug = :slug", Long.class)
                .setParameter("slug", slug)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public Experience findBySlug(String slug) {
        try {
            return em.createQuery(
                    "FROM Experience e WHERE e.slug = :slug", Experience.class)
                    .setParameter("slug", slug)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Experience findByIdWithDetail(Long id) {
        try {
            return em.createQuery(
                    "SELECT DISTINCT e FROM Experience e " +
                            "LEFT JOIN FETCH e.detail " +
                            "LEFT JOIN FETCH e.subCategory " +
                            "WHERE e.id = :id",
                    Experience.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Experience findBySlugWithDetail(String slug) {
        try {
            return em.createQuery(
                    "SELECT DISTINCT e FROM Experience e " +
                            "LEFT JOIN FETCH e.detail " +
                            "LEFT JOIN FETCH e.subCategory " +
                            "WHERE e.slug = :slug",
                    Experience.class)
                    .setParameter("slug", slug)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Experience> findAllWithDetail() {
        return em.createQuery(
                "SELECT DISTINCT e FROM Experience e " +
                        "LEFT JOIN FETCH e.detail " +
                        "LEFT JOIN FETCH e.subCategory " +
                        "ORDER BY e.displayOrder ASC",
                Experience.class)
                .getResultList();
    }

    @Override
    public List<Experience> findBySubCategoryId(Long subCategoryId) {
        return em.createQuery(
                "SELECT DISTINCT e FROM Experience e " +
                        "LEFT JOIN FETCH e.detail " +
                        "WHERE e.subCategory.id = :subCategoryId " +
                        "ORDER BY e.displayOrder ASC",
                Experience.class)
                .setParameter("subCategoryId", subCategoryId)
                .getResultList();
    }

    @Override
    public List<Experience> findFeatured() {
        return em.createQuery(
                "SELECT DISTINCT e FROM Experience e " +
                        "LEFT JOIN FETCH e.detail " +
                        "WHERE e.isFeatured = true AND e.isActive = true " +
                        "ORDER BY e.displayOrder ASC",
                Experience.class)
                .getResultList();
    }

    @Override
    public List<Experience> findAllActive() {
        return em.createQuery(
                "SELECT DISTINCT e FROM Experience e " +
                        "LEFT JOIN FETCH e.detail " +
                        "LEFT JOIN FETCH e.subCategory " +
                        "WHERE e.isActive = true " +
                        "ORDER BY e.displayOrder ASC",
                Experience.class)
                .getResultList();
    }
}
