package com.forvmom.data.dao;

import com.forvmom.data.entities.ExperienceInclusion;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class ExperienceInclusionDaoImpl extends GenericDaoImpl<ExperienceInclusion, Long>
        implements ExperienceInclusionDao {

    public ExperienceInclusionDaoImpl() {
        super(ExperienceInclusion.class);
    }

    @Override
    public List<ExperienceInclusion> findAll() {
        return em.createQuery(
                "SELECT i FROM ExperienceInclusion i WHERE i.deleted = false ORDER BY i.displayOrder ASC",
                ExperienceInclusion.class).getResultList();
    }

    @Override
    public boolean existsById(Long id) {
        Long count = em.createQuery(
                "SELECT COUNT(i) FROM ExperienceInclusion i WHERE i.id = :id AND i.deleted = false",
                Long.class).setParameter("id", id).getSingleResult();
        return count > 0;
    }
}
