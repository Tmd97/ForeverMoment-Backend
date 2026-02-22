package com.forvmom.data.dao;

import com.forvmom.data.entities.ExperienceLocationMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class ExperienceLocationMapperDaoImpl extends GenericDaoImpl<ExperienceLocationMapper, Long>
                implements ExperienceLocationMapperDao {

        public ExperienceLocationMapperDaoImpl() {
                super(ExperienceLocationMapper.class);
        }

        @Override
        public List<ExperienceLocationMapper> findByExperienceId(Long experienceId) {
                return em.createQuery(
                                "SELECT m FROM ExperienceLocationMapper m " +
                                                "JOIN FETCH m.location l " +
                                                "WHERE m.experience.id = :expId AND m.deleted = false",
                                ExperienceLocationMapper.class)
                                .setParameter("expId", experienceId)
                                .getResultList();
        }

        @Override
        public List<ExperienceLocationMapper> findByLocationId(Long locationId) {
                return em.createQuery(
                                "SELECT m FROM ExperienceLocationMapper m " +
                                                "JOIN FETCH m.experience e " +
                                                "WHERE m.location.id = :locId AND m.deleted = false",
                                ExperienceLocationMapper.class)
                                .setParameter("locId", locationId)
                                .getResultList();
        }

        @Override
        public boolean existsByExperienceIdAndLocationId(Long experienceId, Long locationId) {
                Long count = em.createQuery(
                                "SELECT COUNT(m) FROM ExperienceLocationMapper m " +
                                                "WHERE m.experience.id = :expId AND m.location.id = :locId AND m.deleted = false",
                                Long.class)
                                .setParameter("expId", experienceId)
                                .setParameter("locId", locationId)
                                .getSingleResult();
                return count > 0;
        }

        @Override
        public ExperienceLocationMapper findByExperienceIdAndLocationId(Long experienceId, Long locationId) {
                List<ExperienceLocationMapper> results = em.createQuery(
                                "SELECT m FROM ExperienceLocationMapper m " +
                                                "WHERE m.experience.id = :expId AND m.location.id = :locId AND m.deleted = false",
                                ExperienceLocationMapper.class)
                                .setParameter("expId", experienceId)
                                .setParameter("locId", locationId)
                                .getResultList();
                return results.isEmpty() ? null : results.get(0);
        }

        @Override
        public void deleteAllByExperienceId(Long experienceId) {
                em.createQuery(
                                "UPDATE ExperienceLocationMapper m SET m.deleted = true " +
                                                "WHERE m.experience.id = :expId AND m.deleted = false")
                                .setParameter("expId", experienceId)
                                .executeUpdate();
        }
}
