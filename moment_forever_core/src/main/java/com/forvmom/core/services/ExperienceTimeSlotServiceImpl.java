package com.forvmom.core.services;

import com.forvmom.common.dto.request.BulkAttachTimeSlotsRequestDto;
import com.forvmom.common.dto.request.BulkAttachTimeSlotsRequestDto.TimeSlotAttachItem;
import com.forvmom.common.dto.request.ExperienceTimeSlotAttachRequestDto;
import com.forvmom.common.dto.request.TimeSlotRequestDto;
import com.forvmom.common.dto.response.BulkAttachTimeSlotsResultDto;
import com.forvmom.common.dto.response.BulkAttachTimeSlotsResultDto.SkippedTimeSlotDto;
import com.forvmom.common.dto.response.BulkAttachTimeSlotsResultDto.SkippedTimeSlotDto.Reason;
import com.forvmom.common.dto.response.ExperienceTimeSlotResponseDto;
import com.forvmom.common.dto.response.TimeSlotResponseDto;
import com.forvmom.common.errorhandler.ResourceNotFoundException;
import com.forvmom.core.mapper.TimeSlotBeanMapper;
import com.forvmom.data.dao.ExperienceLocationMapperDao;
import com.forvmom.data.dao.ExperienceTimeSlotMapperDao;
import com.forvmom.data.dao.TimeSlotDao;
import com.forvmom.data.entities.ExperienceLocationMapper;
import com.forvmom.data.entities.ExperienceTimeSlotMapper;
import com.forvmom.data.entities.TimeSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExperienceTimeSlotServiceImpl implements ExperienceTimeSlotService {

    @Autowired
    private TimeSlotDao timeSlotDao;

    @Autowired
    private ExperienceTimeSlotMapperDao timeSlotMapperDao;

    @Autowired
    private ExperienceLocationMapperDao expLocationMapperDao;

    // ── Master TimeSlot CRUD ──────────────────────────────────────────────────

    @Override
    @Transactional
    public TimeSlotResponseDto createTimeSlot(TimeSlotRequestDto requestDto) {
        TimeSlot existing = timeSlotDao.findByLabelAndTimeRange(
                requestDto.getLabel(),
                TimeSlotBeanMapper.parseTime(requestDto.getStartTime()),
                TimeSlotBeanMapper.parseTime(requestDto.getEndTime()));
        if (existing != null) {
            throw new IllegalArgumentException("Time slot with same label and time range already exists");
        }
        TimeSlot entity = TimeSlotBeanMapper.mapDtoToEntity(requestDto);
        return TimeSlotBeanMapper.mapEntityToDto(timeSlotDao.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimeSlotResponseDto> getAllTimeSlots() {
        return timeSlotDao.findAll().stream()
                .map(TimeSlotBeanMapper::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TimeSlotResponseDto getTimeSlotById(Long id) {
        return TimeSlotBeanMapper.mapEntityToDto(findTimeSlotOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimeSlotResponseDto> getTimeSlotsByLabel(String label) {
        return timeSlotDao.findByLabelContaining(label).stream()
                .map(TimeSlotBeanMapper::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimeSlotResponseDto> getTimeSlotsByTimeRange(String startTime, String endTime) {
        return timeSlotDao.findByTimeRange(
                TimeSlotBeanMapper.parseTime(startTime),
                TimeSlotBeanMapper.parseTime(endTime)).stream().map(TimeSlotBeanMapper::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TimeSlotResponseDto updateTimeSlot(Long id, TimeSlotRequestDto requestDto) {
        TimeSlot entity = findTimeSlotOrThrow(id);

        TimeSlot dupe = timeSlotDao.findByLabelAndTimeRange(
                requestDto.getLabel(),
                TimeSlotBeanMapper.parseTime(requestDto.getStartTime()),
                TimeSlotBeanMapper.parseTime(requestDto.getEndTime()));
        if (dupe != null && !dupe.getId().equals(id)) {
            throw new IllegalArgumentException("Another time slot with same label and time range already exists");
        }

        TimeSlotBeanMapper.updateEntityFromDto(entity, requestDto);
        return TimeSlotBeanMapper.mapEntityToDto(timeSlotDao.update(entity));
    }

    @Override
    @Transactional
    public void deleteTimeSlot(Long id) {
        TimeSlot entity = findTimeSlotOrThrow(id);
        timeSlotDao.delete(entity);
    }

    @Override
    @Transactional
    public void toggleTimeSlotActive(Long id) {
        TimeSlot entity = findTimeSlotOrThrow(id);
        entity.setIsActive(!Boolean.TRUE.equals(entity.getIsActive()));
        timeSlotDao.update(entity);
    }

    // ── Experience-Location Attachment ────────────────────────────────────────

    @Override
    @Transactional
    public ExperienceTimeSlotResponseDto attachTimeSlot(Long experienceId, Long locationId, Long timeSlotId,
            ExperienceTimeSlotAttachRequestDto requestDto) {

        ExperienceLocationMapper expLocation = resolveExpLocation(experienceId, locationId);

        if (timeSlotMapperDao.existsByExperienceLocationIdAndTimeSlotId(expLocation.getId(), timeSlotId)) {
            throw new IllegalStateException(
                    "TimeSlot " + timeSlotId + " is already attached to this experience-location.");
        }

        TimeSlot timeSlot = findTimeSlotOrThrow(timeSlotId);

        ExperienceTimeSlotMapper mapper = TimeSlotBeanMapper.mapDtoToMapperEntity(requestDto);
        mapper.setTimeSlot(timeSlot);
        expLocation.addTimeSlotMapper(mapper); // bidirectional helper

        return TimeSlotBeanMapper.mapMapperEntityToDto(timeSlotMapperDao.save(mapper));
    }

    @Override
    @Transactional
    public BulkAttachTimeSlotsResultDto attachTimeSlots(Long experienceId, Long locationId,
            BulkAttachTimeSlotsRequestDto requestDto) {

        List<ExperienceTimeSlotResponseDto> attached = new ArrayList<>();
        List<SkippedTimeSlotDto> skipped = new ArrayList<>();

        for (TimeSlotAttachItem item : requestDto.getItems()) {
            Long tsId = item.getTimeSlotId();
            try {
                attached.add(attachTimeSlot(experienceId, locationId, tsId, item));
            } catch (IllegalStateException e) {
                // already attached
                skipped.add(new SkippedTimeSlotDto(tsId, Reason.DUPLICATE, e.getMessage()));
            } catch (ResourceNotFoundException e) {
                // master TimeSlot or experience-location not found
                skipped.add(new SkippedTimeSlotDto(tsId, Reason.NOT_FOUND, e.getMessage()));
            }
        }

        return new BulkAttachTimeSlotsResultDto(attached, skipped);
    }

    @Override
    @Transactional
    public void detachTimeSlot(Long experienceId, Long locationId, Long timeSlotId) {
        ExperienceLocationMapper expLocation = resolveExpLocation(experienceId, locationId);

        ExperienceTimeSlotMapper mapper = timeSlotMapperDao.findByExperienceLocationIdAndTimeSlotId(expLocation.getId(),
                timeSlotId);
        if (mapper == null) {
            throw new ResourceNotFoundException(
                    "TimeSlot " + timeSlotId + " is not attached to this experience-location.");
        }
        timeSlotMapperDao.delete(mapper);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExperienceTimeSlotResponseDto> getTimeSlotsForExperienceLocation(
            Long experienceId, Long locationId) {
        ExperienceLocationMapper expLocation = resolveExpLocation(experienceId, locationId);
        return TimeSlotBeanMapper.mapMapperEntitiesToDto(
                timeSlotMapperDao.findByExperienceLocationId(expLocation.getId()));
    }

    @Override
    @Transactional
    public ExperienceTimeSlotResponseDto updateAttachment(Long experienceId, Long locationId, Long timeSlotId,
            ExperienceTimeSlotAttachRequestDto requestDto) {
        ExperienceLocationMapper expLocation = resolveExpLocation(experienceId, locationId);

        ExperienceTimeSlotMapper mapper = timeSlotMapperDao.findByExperienceLocationIdAndTimeSlotId(expLocation.getId(),
                timeSlotId);
        if (mapper == null) {
            throw new ResourceNotFoundException(
                    "TimeSlot " + timeSlotId + " is not attached to this experience-location.");
        }
        TimeSlotBeanMapper.updateMapperEntityFromDto(mapper, requestDto);
        return TimeSlotBeanMapper.mapMapperEntityToDto(timeSlotMapperDao.update(mapper));
    }

    @Override
    @Transactional
    public void toggleAttachmentActive(Long mapperId) {
        ExperienceTimeSlotMapper mapper = timeSlotMapperDao.findById(mapperId);
        if (mapper == null)
            throw new ResourceNotFoundException("TimeSlot mapping not found: " + mapperId);
        mapper.setIsActive(!Boolean.TRUE.equals(mapper.getIsActive()));
        timeSlotMapperDao.update(mapper);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private TimeSlot findTimeSlotOrThrow(Long id) {
        TimeSlot entity = timeSlotDao.findById(id);
        if (entity == null)
            throw new ResourceNotFoundException("TimeSlot not found: " + id);
        return entity;
    }

    private ExperienceLocationMapper resolveExpLocation(Long experienceId, Long locationId) {
        ExperienceLocationMapper expLocation = expLocationMapperDao.findByExperienceIdAndLocationId(experienceId,
                locationId);
        if (expLocation == null) {
            throw new ResourceNotFoundException(
                    "Location " + locationId + " is not attached to experience " + experienceId
                            + ". Attach the location first.");
        }
        return expLocation;
    }
}
