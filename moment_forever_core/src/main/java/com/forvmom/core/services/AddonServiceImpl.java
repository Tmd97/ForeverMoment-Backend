package com.forvmom.core.services;

import com.forvmom.common.dto.request.AddonRequestDto;
import com.forvmom.common.dto.response.AddonResponseDto;
import com.forvmom.common.errorhandler.ResourceNotFoundException;
import com.forvmom.core.mapper.AddonBeanMapper;
import com.forvmom.data.dao.AddonDao;
import com.forvmom.data.dao.ExperienceAddonMapperDao;
import com.forvmom.data.dao.ExperienceDao;
import com.forvmom.data.entities.Addon;
import com.forvmom.data.entities.Experience;
import com.forvmom.data.entities.ExperienceAddonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddonServiceImpl implements AddonService {

    @Autowired
    private AddonDao addonDao;

    @Autowired
    private ExperienceAddonMapperDao addonMapperDao;

    @Autowired
    private ExperienceDao experienceDao;

    @Override
    @Transactional
    public AddonResponseDto createAddon(AddonRequestDto requestDto) {
        Addon addon = AddonBeanMapper.mapRequestToAddon(requestDto);
        Addon saved = addonDao.save(addon);
        return AddonBeanMapper.mapAddonToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddonResponseDto> getAllAddons() {
        return addonDao.findAll().stream()
                .map(AddonBeanMapper::mapAddonToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddonResponseDto updateAddon(Long id, AddonRequestDto requestDto) {
        Addon existing = addonDao.findById(id);
        if (existing == null)
            throw new ResourceNotFoundException("Addon not found: " + id);
        AddonBeanMapper.updateAddonFromRequest(existing, requestDto);
        return AddonBeanMapper.mapAddonToDto(addonDao.update(existing));
    }

    @Override
    @Transactional
    public boolean deleteAddon(Long id) {
        Addon existing = addonDao.findById(id);
        if (existing == null)
            throw new ResourceNotFoundException("Addon not found: " + id);
        // Soft-delete all junction rows first (removes it from all experiences)
        addonMapperDao.deleteAllByAddonId(id);
        addonDao.delete(existing);
        return true;
    }

    // ── Experience Attachment ─────────────────────────────────────────────────

    @Override
    @Transactional
    public void attachToExperience(Long experienceId, Long addonId, BigDecimal priceOverride, Boolean isFree) {
        if (addonMapperDao.existsByExperienceIdAndAddonId(experienceId, addonId)) {
            throw new IllegalStateException(
                    "Addon " + addonId + " is already attached to experience " + experienceId);
        }
        Experience experience = experienceDao.findById(experienceId);
        if (experience == null)
            throw new ResourceNotFoundException("Experience not found: " + experienceId);

        Addon addon = addonDao.findById(addonId);
        if (addon == null)
            throw new ResourceNotFoundException("Addon not found: " + addonId);

        ExperienceAddonMapper mapper = new ExperienceAddonMapper();
        mapper.setAddon(addon);
        mapper.setPriceOverride(priceOverride);
        mapper.setIsFree(Boolean.TRUE.equals(isFree));
        // Bidirectional helper — sets experience on mapper and adds to experience Set
        experience.addAddonMapper(mapper);
        addonMapperDao.save(mapper);
    }

    @Override
    @Transactional
    public void detachFromExperience(Long experienceId, Long addonId) {
        ExperienceAddonMapper mapper = addonMapperDao.findByExperienceIdAndAddonId(experienceId, addonId);
        if (mapper == null) {
            throw new ResourceNotFoundException(
                    "Addon " + addonId + " is not attached to experience " + experienceId);
        }
        addonMapperDao.delete(mapper);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddonResponseDto> getAddonsForExperience(Long experienceId) {
        List<ExperienceAddonMapper> mappers = addonMapperDao.findByExperienceId(experienceId);
        return AddonBeanMapper.mapAddonMappers(mappers);
    }
}
