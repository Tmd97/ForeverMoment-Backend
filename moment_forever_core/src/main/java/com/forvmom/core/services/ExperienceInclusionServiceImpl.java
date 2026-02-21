package com.forvmom.core.services;

import com.forvmom.common.dto.request.ExperienceInclusionRequestDto;
import com.forvmom.common.dto.response.ExperienceInclusionResponseDto;
import com.forvmom.common.errorhandler.ResourceNotFoundException;
import com.forvmom.core.mapper.InclusionPolicyBeanMapper;
import com.forvmom.data.dao.ExperienceDao;
import com.forvmom.data.dao.ExperienceInclusionDao;
import com.forvmom.data.dao.ExperienceInclusionMapperDao;
import com.forvmom.data.entities.Experience;
import com.forvmom.data.entities.ExperienceInclusion;
import com.forvmom.data.entities.ExperienceInclusionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExperienceInclusionServiceImpl implements ExperienceInclusionService {

    @Autowired
    private ExperienceInclusionDao inclusionDao;

    @Autowired
    private ExperienceInclusionMapperDao inclusionMapperDao;

    @Autowired
    private ExperienceDao experienceDao;

    @Override
    @Transactional
    public ExperienceInclusionResponseDto createInclusion(ExperienceInclusionRequestDto requestDto) {
        ExperienceInclusion entity = InclusionPolicyBeanMapper.mapRequestToInclusion(requestDto);
        ExperienceInclusion saved = inclusionDao.save(entity);
        return InclusionPolicyBeanMapper.mapInclusionToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExperienceInclusionResponseDto> getAllInclusions() {
        return inclusionDao.findAll().stream()
                .map(InclusionPolicyBeanMapper::mapInclusionToDto)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional
    public ExperienceInclusionResponseDto updateInclusion(Long id, ExperienceInclusionRequestDto requestDto) {
        ExperienceInclusion existing = inclusionDao.findById(id);
        if (existing == null)
            throw new ResourceNotFoundException("Inclusion not found: " + id);
        InclusionPolicyBeanMapper.updateInclusionFromRequest(existing, requestDto);
        return InclusionPolicyBeanMapper.mapInclusionToDto(inclusionDao.update(existing));
    }

    @Override
    @Transactional
    public boolean deleteInclusion(Long id) {
        ExperienceInclusion existing = inclusionDao.findById(id);
        if (existing == null)
            throw new ResourceNotFoundException("Inclusion not found: " + id);
        // Soft-delete all junction rows (removes it from all experiences) then
        // soft-delete master
        inclusionMapperDao.deleteAllByInclusionId(id);
        inclusionDao.delete(existing);
        return true;
    }

    @Override
    @Transactional
    public void attachToExperience(Long experienceId, Long inclusionId, Integer displayOrder) {
        if (inclusionMapperDao.existsByExperienceIdAndInclusionId(experienceId, inclusionId)) {
            throw new IllegalStateException(
                    "Inclusion " + inclusionId + " is already attached to experience " + experienceId);
        }
        Experience experience = experienceDao.findById(experienceId);
        if (experience == null)
            throw new ResourceNotFoundException("Experience not found: " + experienceId);

        ExperienceInclusion inclusion = inclusionDao.findById(inclusionId);
        if (inclusion == null)
            throw new ResourceNotFoundException("Inclusion not found: " + inclusionId);

        ExperienceInclusionMapper mapper = new ExperienceInclusionMapper();
        mapper.setInclusion(inclusion);
        mapper.setDisplayOrder(displayOrder != null ? displayOrder : 0);
        // Helper wires both sides: mapper.setExperience(experience) +
        // experience.getInclusionMappers().add(mapper)
        experience.addInclusionMapper(mapper);
        inclusionMapperDao.save(mapper);
    }

    @Override
    @Transactional
    public void detachFromExperience(Long experienceId, Long inclusionId) {
        ExperienceInclusionMapper mapper = inclusionMapperDao.findByExperienceIdAndInclusionId(experienceId,
                inclusionId);
        if (mapper == null) {
            throw new ResourceNotFoundException(
                    "Inclusion " + inclusionId + " is not attached to experience " + experienceId);
        }
        inclusionMapperDao.delete(mapper);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExperienceInclusionResponseDto> getInclusionsForExperience(Long experienceId) {
        List<ExperienceInclusionMapper> mappers = inclusionMapperDao.findByExperienceId(experienceId);
        return InclusionPolicyBeanMapper.mapInclusionMappers(mappers);
    }
}
