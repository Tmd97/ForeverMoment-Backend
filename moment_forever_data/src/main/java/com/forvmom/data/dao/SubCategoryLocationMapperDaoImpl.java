package com.forvmom.data.dao;

import com.forvmom.data.entities.SubCategoryLocationMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
@Transactional
public class SubCategoryLocationMapperDaoImpl extends GenericDaoImpl<SubCategoryLocationMapper, Long>
        implements SubCategoryLocationMapperDao {

    public SubCategoryLocationMapperDaoImpl() {
        super(SubCategoryLocationMapper.class);
    }

    @Override
    public SubCategoryLocationMapper findBySubCategoryIdAndLocationId(Long subCategoryId, Long locationId) {
        List<SubCategoryLocationMapper> results = em.createQuery(
                        "SELECT sclm FROM SubCategoryLocationMapper sclm " +
                                "WHERE sclm.subCategory.id = :subCategoryId AND sclm.location.id = :locationId AND sclm.deleted = false",
                        SubCategoryLocationMapper.class)
                .setParameter("subCategoryId", subCategoryId)
                .setParameter("locationId", locationId)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<SubCategoryLocationMapper> findBySubCategoryId(Long subCategoryId) {
        return em.createQuery(
                        "SELECT sclm FROM SubCategoryLocationMapper sclm " +
                                "JOIN FETCH sclm.location l " +
                                "WHERE sclm.subCategory.id = :subCategoryId AND sclm.deleted = false",
                        SubCategoryLocationMapper.class)
                .setParameter("subCategoryId", subCategoryId)
                .getResultList();
    }

    @Override
    public List<SubCategoryLocationMapper> findByLocationId(Long locationId) {
        return em.createQuery(
                        "SELECT sclm FROM SubCategoryLocationMapper sclm " +
                                "JOIN FETCH sclm.subCategory sc " +
                                "JOIN FETCH sc.category " +  // eager load category for response
                                "WHERE sclm.location.id = :locationId AND sclm.deleted = false",
                        SubCategoryLocationMapper.class)
                .setParameter("locationId", locationId)
                .getResultList();
    }

    @Override
    public List<SubCategoryLocationMapper> findActiveByLocationId(Long locationId) {
        return em.createQuery(
                        "SELECT sclm FROM SubCategoryLocationMapper sclm " +
                                "JOIN FETCH sclm.subCategory sc " +
                                "JOIN FETCH sc.category " +
                                "WHERE sclm.location.id = :locationId " +
                                "AND sclm.deleted = false AND sclm.isActive = true " +
                                "AND sc.deleted = false AND sc.isActive = true " +
                                "ORDER BY sclm.displayOrder ASC",
                        SubCategoryLocationMapper.class)
                .setParameter("locationId", locationId)
                .getResultList();
    }

    @Override
    public List<SubCategoryLocationMapper> findActiveByLocationIdAndCategoryId(Long locationId, Long categoryId) {
        return em.createQuery(
                        "SELECT sclm FROM SubCategoryLocationMapper sclm " +
                                "JOIN FETCH sclm.subCategory sc " +
                                "WHERE sclm.location.id = :locationId " +
                                "AND sc.category.id = :categoryId " +
                                "AND sclm.deleted = false AND sclm.isActive = true " +
                                "AND sc.deleted = false AND sc.isActive = true " +
                                "ORDER BY sclm.displayOrder ASC",
                        SubCategoryLocationMapper.class)
                .setParameter("locationId", locationId)
                .setParameter("categoryId", categoryId)
                .getResultList();
    }

    @Override
    public boolean existsBySubCategoryIdAndLocationId(Long subCategoryId, Long locationId) {
        Long count = em.createQuery(
                        "SELECT COUNT(sclm) FROM SubCategoryLocationMapper sclm " +
                                "WHERE sclm.subCategory.id = :subCategoryId AND sclm.location.id = :locationId AND sclm.deleted = false",
                        Long.class)
                .setParameter("subCategoryId", subCategoryId)
                .setParameter("locationId", locationId)
                .getSingleResult();
        return count > 0;
    }
}
