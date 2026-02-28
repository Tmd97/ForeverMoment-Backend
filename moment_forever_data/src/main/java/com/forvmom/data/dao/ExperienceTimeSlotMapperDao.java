package com.forvmom.data.dao;

import com.forvmom.data.entities.ExperienceTimeSlotMapper;

import java.util.List;

public interface ExperienceTimeSlotMapperDao extends GenericDao<ExperienceTimeSlotMapper, Long> {

    /** All timeslot mappers for a specific experience-location pair */
    List<ExperienceTimeSlotMapper> findByExperienceLocationId(Long expLocationId);

    boolean existsByExperienceLocationIdAndTimeSlotId(Long expLocationId, Long timeSlotId);

    ExperienceTimeSlotMapper findByExperienceLocationIdAndTimeSlotId(Long expLocationId, Long timeSlotId);

    /**
     * Soft-delete all timeslot mappers for an experience-location (on location
     * detach or experience delete)
     */
    void deleteAllByExperienceLocationId(Long expLocationId);

    /**
     * Atomically increments {@code current_bookings} by {@code guestCount} only if
     * the result would not exceed {@code maxCapacity}.
     * Pass {@code null} for maxCapacity to skip the cap check (unlimited slots).
     *
     * @return number of rows updated (1 = success, 0 = capacity exceeded)
     */
    int atomicIncrementCapacity(Long slotMapperId, int guestCount);

    /**
     * Atomically decrements {@code current_bookings} by {@code guestCount},
     * floored at zero. Used by Core when a booking fails downstream.
     *
     * @return number of rows updated
     */
    int atomicDecrementCapacity(Long slotMapperId, int guestCount);
}
