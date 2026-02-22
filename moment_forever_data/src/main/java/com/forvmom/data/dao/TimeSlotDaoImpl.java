package com.forvmom.data.dao;

import com.forvmom.data.entities.TimeSlot;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Repository
@Transactional
public class TimeSlotDaoImpl extends GenericDaoImpl<TimeSlot, Long> implements TimeSlotDao {

    public TimeSlotDaoImpl() {
        super(TimeSlot.class);
    }

    @Override
    public List<TimeSlot> findAll() {
        return em.createQuery(
                "SELECT t FROM TimeSlot t WHERE t.deleted = false ORDER BY t.startTime ASC",
                TimeSlot.class).getResultList();
    }

    @Override
    public List<TimeSlot> findByLabelContaining(String label) {
        return em.createQuery(
                "SELECT t FROM TimeSlot t WHERE t.deleted = false AND LOWER(t.label) LIKE LOWER(:label)",
                TimeSlot.class)
                .setParameter("label", "%" + label + "%")
                .getResultList();
    }

    @Override
    public List<TimeSlot> findByTimeRange(LocalTime startTime, LocalTime endTime) {
        return em.createQuery(
                "SELECT t FROM TimeSlot t WHERE t.deleted = false " +
                        "AND t.startTime >= :startTime AND t.endTime <= :endTime",
                TimeSlot.class)
                .setParameter("startTime", startTime)
                .setParameter("endTime", endTime)
                .getResultList();
    }

    @Override
    public TimeSlot findByLabelAndTimeRange(String label, LocalTime startTime, LocalTime endTime) {
        List<TimeSlot> results = em.createQuery(
                "SELECT t FROM TimeSlot t WHERE t.deleted = false " +
                        "AND t.label = :label AND t.startTime = :startTime AND t.endTime = :endTime",
                TimeSlot.class)
                .setParameter("label", label)
                .setParameter("startTime", startTime)
                .setParameter("endTime", endTime)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public boolean existsById(Long id) {
        Long count = em.createQuery(
                "SELECT COUNT(t) FROM TimeSlot t WHERE t.id = :id AND t.deleted = false",
                Long.class).setParameter("id", id).getSingleResult();
        return count > 0;
    }
}
