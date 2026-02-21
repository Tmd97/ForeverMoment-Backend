package com.forvmom.core.services;

import com.forvmom.common.dto.request.CancellationPolicyRequestDto;
import com.forvmom.common.dto.response.CancellationPolicyResponseDto;
import com.forvmom.common.errorhandler.ResourceNotFoundException;
import com.forvmom.core.mapper.InclusionPolicyBeanMapper;
import com.forvmom.data.dao.ExperienceCancellationPolicyDao;
import com.forvmom.data.dao.ExperienceCancellationPolicyMapperDao;
import com.forvmom.data.dao.ExperienceDao;
import com.forvmom.data.entities.Experience;
import com.forvmom.data.entities.ExperienceCancellationPolicy;
import com.forvmom.data.entities.ExperienceCancellationPolicyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExperienceCancellationPolicyServiceImpl implements ExperienceCancellationPolicyService {

    @Autowired
    private ExperienceCancellationPolicyDao policyDao;

    @Autowired
    private ExperienceCancellationPolicyMapperDao policyMapperDao;

    @Autowired
    private ExperienceDao experienceDao;

    @Override
    @Transactional
    public CancellationPolicyResponseDto createPolicy(CancellationPolicyRequestDto requestDto) {
        ExperienceCancellationPolicy entity = InclusionPolicyBeanMapper.mapRequestToPolicy(requestDto);
        ExperienceCancellationPolicy saved = policyDao.save(entity);
        return InclusionPolicyBeanMapper.mapPolicyToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CancellationPolicyResponseDto> getAllPolicies() {
        return policyDao.findAll().stream()
                .map(InclusionPolicyBeanMapper::mapPolicyToDto)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional
    public CancellationPolicyResponseDto updatePolicy(Long id, CancellationPolicyRequestDto requestDto) {
        ExperienceCancellationPolicy existing = policyDao.findById(id);
        if (existing == null)
            throw new ResourceNotFoundException("Policy not found: " + id);
        InclusionPolicyBeanMapper.updatePolicyFromRequest(existing, requestDto);
        return InclusionPolicyBeanMapper.mapPolicyToDto(policyDao.update(existing));
    }

    @Override
    @Transactional
    public boolean deletePolicy(Long id) {
        ExperienceCancellationPolicy existing = policyDao.findById(id);
        if (existing == null)
            throw new ResourceNotFoundException("Policy not found: " + id);
        // Soft-delete all junction rows first, then soft-delete master
        policyMapperDao.deleteAllByPolicyId(id);
        policyDao.delete(existing);
        return true;
    }

    @Override
    @Transactional
    public void attachToExperience(Long experienceId, Long policyId, Integer displayOrder) {
        if (policyMapperDao.existsByExperienceIdAndPolicyId(experienceId, policyId)) {
            throw new IllegalStateException(
                    "Policy " + policyId + " is already attached to experience " + experienceId);
        }
        Experience experience = experienceDao.findById(experienceId);
        if (experience == null)
            throw new ResourceNotFoundException("Experience not found: " + experienceId);

        ExperienceCancellationPolicy policy = policyDao.findById(policyId);
        if (policy == null)
            throw new ResourceNotFoundException("Policy not found: " + policyId);

        ExperienceCancellationPolicyMapper mapper = new ExperienceCancellationPolicyMapper();
        mapper.setPolicy(policy);
        mapper.setExperience(experience);
        mapper.setDisplayOrder(displayOrder != null ? displayOrder : 0);
        experience.addPolicyMapper(mapper);
        policyMapperDao.save(mapper);
    }

    @Override
    @Transactional
    public void detachFromExperience(Long experienceId, Long policyId) {
        ExperienceCancellationPolicyMapper mapper = policyMapperDao.findByExperienceIdAndPolicyId(experienceId,
                policyId);
        if (mapper == null) {
            throw new ResourceNotFoundException(
                    "Policy " + policyId + " is not attached to experience " + experienceId);
        }
        policyMapperDao.delete(mapper);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CancellationPolicyResponseDto> getPoliciesForExperience(Long experienceId) {
        List<ExperienceCancellationPolicyMapper> mappers = policyMapperDao.findByExperienceId(experienceId);
        return InclusionPolicyBeanMapper.mapPolicyMappers(mappers);
    }
}
