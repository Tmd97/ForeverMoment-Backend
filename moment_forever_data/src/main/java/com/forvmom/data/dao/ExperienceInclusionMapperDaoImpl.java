package com.forvmom.data.dao;

import com.forvmom.data.entities.ExperienceInclusionMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class ExperienceInclusionMapperDaoImpl extends GenericDaoImpl<ExperienceInclusionMapper, Long>
                implements ExperienceInclusionMapperDao {

        public ExperienceInclusionMapperDaoImpl() {
                super(ExperienceInclusionMapper.class);
        }

        @Override
        public List<ExperienceInclusionMapper> findByExperienceId(Long experienceId) {
                return em.createQuery(
                                "SELECT m FROM ExperienceInclusionMapper m " +
                                                "JOIN FETCH m.inclusion i " +
                                                "WHERE m.experience.id = :expId AND m.deleted = false " +
                                                "ORDER BY m.displayOrder ASC",
                                ExperienceInclusionMapper.class)
                                .setParameter("expId", experienceId)
                                .getResultList();
        }

        @Override
        public boolean existsByExperienceIdAndInclusionId(Long experienceId, Long inclusionId) {
                Long count = em.createQuery(
                                "SELECT COUNT(m) FROM ExperienceInclusionMapper m " +
                                                "WHERE m.experience.id = :expId AND m.inclusion.id = :inclId AND m.deleted = false",
                                Long.class)
                                .setParameter("expId", experienceId)
                                .setParameter("inclId", inclusionId)
                                .getSingleResult();
                return count > 0;
        }

        @Override
        public ExperienceInclusionMapper findByExperienceIdAndInclusionId(Long experienceId, Long inclusionId) {
                List<ExperienceInclusionMapper> results = em.createQuery(
                                "SELECT m FROM ExperienceInclusionMapper m " +
                                                "WHERE m.experience.id = :expId AND m.inclusion.id = :inclId AND m.deleted = false",
                                ExperienceInclusionMapper.class)
                                .setParameter("expId", experienceId)
                                .setParameter("inclId", inclusionId)
                                .getResultList();
                return results.isEmpty() ? null : results.get(0);
        }

        @Override
        public void deleteAllByExperienceId(Long experienceId) {
                em.createQuery(
                                "UPDATE ExperienceInclusionMapper m SET m.deleted = true " +
                                                "WHERE m.experience.id = :expId AND m.deleted = false")
                                .setParameter("expId", experienceId)
                                .executeUpdate();
        }

        @Override
        public void deleteAllByInclusionId(Long inclusionId) {
                em.createQuery(
                                "UPDATE ExperienceInclusionMapper m SET m.deleted = true " +
                                                "WHERE m.inclusion.id = :inclId AND m.deleted = false")
                                .setParameter("inclId", inclusionId)
                                .executeUpdate();
        }
}
