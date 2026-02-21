package com.forvmom.data.dao;

import com.forvmom.data.entities.ExperienceCancellationPolicyMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class ExperienceCancellationPolicyMapperDaoImpl extends GenericDaoImpl<ExperienceCancellationPolicyMapper, Long>
                implements ExperienceCancellationPolicyMapperDao {

        public ExperienceCancellationPolicyMapperDaoImpl() {
                super(ExperienceCancellationPolicyMapper.class);
        }

        @Override
        public List<ExperienceCancellationPolicyMapper> findByExperienceId(Long experienceId) {
                return em.createQuery(
                                "SELECT m FROM ExperienceCancellationPolicyMapper m " +
                                                "JOIN FETCH m.policy p " +
                                                "WHERE m.experience.id = :expId AND m.deleted = false " +
                                                "ORDER BY m.displayOrder ASC",
                                ExperienceCancellationPolicyMapper.class)
                                .setParameter("expId", experienceId)
                                .getResultList();
        }

        @Override
        public boolean existsByExperienceIdAndPolicyId(Long experienceId, Long policyId) {
                Long count = em.createQuery(
                                "SELECT COUNT(m) FROM ExperienceCancellationPolicyMapper m " +
                                                "WHERE m.experience.id = :expId AND m.policy.id = :polId AND m.deleted = false",
                                Long.class)
                                .setParameter("expId", experienceId)
                                .setParameter("polId", policyId)
                                .getSingleResult();
                return count > 0;
        }

        @Override
        public ExperienceCancellationPolicyMapper findByExperienceIdAndPolicyId(Long experienceId, Long policyId) {
                List<ExperienceCancellationPolicyMapper> results = em.createQuery(
                                "SELECT m FROM ExperienceCancellationPolicyMapper m " +
                                                "WHERE m.experience.id = :expId AND m.policy.id = :polId AND m.deleted = false",
                                ExperienceCancellationPolicyMapper.class)
                                .setParameter("expId", experienceId)
                                .setParameter("polId", policyId)
                                .getResultList();
                return results.isEmpty() ? null : results.get(0);
        }

        @Override
        public void deleteAllByExperienceId(Long experienceId) {
                em.createQuery(
                                "UPDATE ExperienceCancellationPolicyMapper m SET m.deleted = true " +
                                                "WHERE m.experience.id = :expId AND m.deleted = false")
                                .setParameter("expId", experienceId)
                                .executeUpdate();
        }

        @Override
        public void deleteAllByPolicyId(Long policyId) {
                em.createQuery(
                                "UPDATE ExperienceCancellationPolicyMapper m SET m.deleted = true " +
                                                "WHERE m.policy.id = :polId AND m.deleted = false")
                                .setParameter("polId", policyId)
                                .executeUpdate();
        }
}
