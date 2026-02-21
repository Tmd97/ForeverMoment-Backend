package com.forvmom.data.dao;

import com.forvmom.data.entities.ExperienceCancellationPolicy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class ExperienceCancellationPolicyDaoImpl extends GenericDaoImpl<ExperienceCancellationPolicy, Long>
        implements ExperienceCancellationPolicyDao {

    public ExperienceCancellationPolicyDaoImpl() {
        super(ExperienceCancellationPolicy.class);
    }

    @Override
    public List<ExperienceCancellationPolicy> findAll() {
        return em.createQuery(
                "SELECT p FROM ExperienceCancellationPolicy p WHERE p.deleted = false ORDER BY p.displayOrder ASC",
                ExperienceCancellationPolicy.class).getResultList();
    }

    @Override
    public boolean existsById(Long id) {
        Long count = em.createQuery(
                "SELECT COUNT(p) FROM ExperienceCancellationPolicy p WHERE p.id = :id AND p.deleted = false",
                Long.class).setParameter("id", id).getSingleResult();
        return count > 0;
    }
}
