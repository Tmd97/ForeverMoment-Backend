package com.forvmom.data.dao;

import com.forvmom.data.entities.TimeSlot;

import java.time.LocalTime;
import java.util.List;

public interface TimeSlotDao extends GenericDao<TimeSlot, Long> {

    List<TimeSlot> findAll();

    List<TimeSlot> findByLabelContaining(String label);

    List<TimeSlot> findByTimeRange(LocalTime startTime, LocalTime endTime);

    TimeSlot findByLabelAndTimeRange(String label, LocalTime startTime, LocalTime endTime);

    boolean existsById(Long id);
}