package com.forvmom.data.dao;

import com.forvmom.data.entities.ExperienceTimeSlotMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class ExperienceTimeSlotMapperDaoImpl extends GenericDaoImpl<ExperienceTimeSlotMapper, Long>
                implements ExperienceTimeSlotMapperDao {

        public ExperienceTimeSlotMapperDaoImpl() {
                super(ExperienceTimeSlotMapper.class);
        }

        @Override
        public List<ExperienceTimeSlotMapper> findByExperienceLocationId(Long expLocationId) {
                return em.createQuery(
                                "SELECT m FROM ExperienceTimeSlotMapper m " +
                                                "JOIN FETCH m.timeSlot t " +
                                                "WHERE m.experienceLocation.id = :expLocId AND m.deleted = false " +
                                                "ORDER BY t.startTime ASC",
                                ExperienceTimeSlotMapper.class)
                                .setParameter("expLocId", expLocationId)
                                .getResultList();
        }

        @Override
        public boolean existsByExperienceLocationIdAndTimeSlotId(Long expLocationId, Long timeSlotId) {
                Long count = em.createQuery(
                                "SELECT COUNT(m) FROM ExperienceTimeSlotMapper m " +
                                                "WHERE m.experienceLocation.id = :expLocId AND m.timeSlot.id = :tsId AND m.deleted = false",
                                Long.class)
                                .setParameter("expLocId", expLocationId)
                                .setParameter("tsId", timeSlotId)
                                .getSingleResult();
                return count > 0;
        }

        @Override
        public ExperienceTimeSlotMapper findByExperienceLocationIdAndTimeSlotId(Long expLocationId, Long timeSlotId) {
                List<ExperienceTimeSlotMapper> results = em.createQuery(
                                "SELECT m FROM ExperienceTimeSlotMapper m " +
                                                "WHERE m.experienceLocation.id = :expLocId AND m.timeSlot.id = :tsId AND m.deleted = false",
                                ExperienceTimeSlotMapper.class)
                                .setParameter("expLocId", expLocationId)
                                .setParameter("tsId", timeSlotId)
                                .getResultList();
                return results.isEmpty() ? null : results.get(0);
        }

        @Override
        public void deleteAllByExperienceLocationId(Long expLocationId) {
                em.createQuery(
                                "UPDATE ExperienceTimeSlotMapper m SET m.deleted = true " +
                                                "WHERE m.experienceLocation.id = :expLocId AND m.deleted = false")
                                .setParameter("expLocId", expLocationId)
                                .executeUpdate();
        }

        @Override
        public int atomicIncrementCapacity(Long slotMapperId, int guestCount) {
                // Uses m.maxCapacity directly from the table. IS NULL means unlimited.
                return em.createQuery(
                                "UPDATE ExperienceTimeSlotMapper m " +
                                                "SET m.currentBookings = m.currentBookings + :guests " +
                                                "WHERE m.id = :id " +
                                                "  AND m.deleted = false " +
                                                "  AND (m.maxCapacity IS NULL OR (m.currentBookings + :guests) <= m.maxCapacity)")
                                .setParameter("guests", guestCount)
                                .setParameter("id", slotMapperId)
                                .executeUpdate();
        }

        @Override
        public int atomicDecrementCapacity(Long slotMapperId, int guestCount) {
                // GREATEST(0, current_bookings - guestCount) — prevents going negative
                return em.createQuery(
                                "UPDATE ExperienceTimeSlotMapper m " +
                                                "SET m.currentBookings = CASE " +
                                                "  WHEN m.currentBookings >= :guests THEN m.currentBookings - :guests "
                                                +
                                                "  ELSE 0 END " +
                                                "WHERE m.id = :id AND m.deleted = false")
                                .setParameter("guests", guestCount)
                                .setParameter("id", slotMapperId)
                                .executeUpdate();
        }
}
