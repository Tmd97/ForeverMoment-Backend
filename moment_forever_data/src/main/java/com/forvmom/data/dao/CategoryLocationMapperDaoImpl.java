package com.forvmom.data.dao;

import com.forvmom.data.entities.CategoryLocationMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class CategoryLocationMapperDaoImpl extends GenericDaoImpl<CategoryLocationMapper, Long>
        implements CategoryLocationMapperDao {

    public CategoryLocationMapperDaoImpl() {
        super(CategoryLocationMapper.class);
    }

    @Override
    public CategoryLocationMapper findByCategoryIdAndLocationId(Long categoryId, Long locationId) {
        List<CategoryLocationMapper> results = em.createQuery(
                        "SELECT clm FROM CategoryLocationMapper clm " +
                                "WHERE clm.category.id = :categoryId AND clm.location.id = :locationId AND clm.deleted = false",
                        CategoryLocationMapper.class)
                .setParameter("categoryId", categoryId)
                .setParameter("locationId", locationId)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<CategoryLocationMapper> findByCategoryId(Long categoryId) {
        return em.createQuery(
                        "SELECT clm FROM CategoryLocationMapper clm " +
                                "JOIN FETCH clm.location l " +
                                "WHERE clm.category.id = :categoryId AND clm.deleted = false",
                        CategoryLocationMapper.class)
                .setParameter("categoryId", categoryId)
                .getResultList();
    }

    @Override
    public List<CategoryLocationMapper> findByLocationId(Long locationId) {
        return em.createQuery(
                        "SELECT clm FROM CategoryLocationMapper clm " +
                                "JOIN FETCH clm.category c " +
                                "WHERE clm.location.id = :locationId AND clm.deleted = false",
                        CategoryLocationMapper.class)
                .setParameter("locationId", locationId)
                .getResultList();
    }

    @Override
    public List<CategoryLocationMapper> findActiveByLocationId(Long locationId) {
        return em.createQuery(
                        "SELECT clm FROM CategoryLocationMapper clm " +
                                "JOIN FETCH clm.category c " +
                                "WHERE clm.location.id = :locationId " +
                                "AND clm.deleted = false AND clm.isActive = true " +
                                "AND c.deleted = false AND c.isActive = true " +
                                "ORDER BY clm.displayOrder ASC",
                        CategoryLocationMapper.class)
                .setParameter("locationId", locationId)
                .getResultList();
    }

    @Override
    public boolean existsByCategoryIdAndLocationId(Long categoryId, Long locationId) {
        Long count = em.createQuery(
                        "SELECT COUNT(clm) FROM CategoryLocationMapper clm " +
                                "WHERE clm.category.id = :categoryId AND clm.location.id = :locationId AND clm.deleted = false",
                        Long.class)
                .setParameter("categoryId", categoryId)
                .setParameter("locationId", locationId)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public void softDeleteByCategoryIdAndLocationId(Long categoryId, Long locationId) {
        em.createQuery(
                        "UPDATE CategoryLocationMapper clm SET clm.deleted = true " +
                                "WHERE clm.category.id = :categoryId AND clm.location.id = :locationId AND clm.deleted = false")
                .setParameter("categoryId", categoryId)
                .setParameter("locationId", locationId)
                .executeUpdate();
    }
}