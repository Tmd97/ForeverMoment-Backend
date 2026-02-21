package com.forvmom.data.dao;

import com.forvmom.data.entities.ExperienceDetail;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ExperienceDetailDaoImpl extends GenericDaoImpl<ExperienceDetail, Long>
        implements ExperienceDetailDao {

    public ExperienceDetailDaoImpl() {
        super(ExperienceDetail.class);
    }

    @Override
    public ExperienceDetail findByExperienceId(Long experienceId) {
        try {
            return em.createQuery(
                    "FROM ExperienceDetail d WHERE d.experience.id = :expId",
                    ExperienceDetail.class)
                    .setParameter("expId", experienceId)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean existsByExperienceId(Long experienceId) {
        Long count = em.createQuery(
                "SELECT COUNT(d) FROM ExperienceDetail d WHERE d.experience.id = :expId",
                Long.class)
                .setParameter("expId", experienceId)
                .getSingleResult();
        return count > 0;
    }
}
