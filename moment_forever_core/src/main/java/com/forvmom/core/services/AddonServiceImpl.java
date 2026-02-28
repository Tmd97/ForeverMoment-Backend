package com.forvmom.core.services;

import com.forvmom.common.dto.request.AddonRequestDto;
import com.forvmom.common.dto.request.BulkAttachAddonRequestDto;
import com.forvmom.common.dto.request.BulkAttachAddonRequestDto.AddonAttachItem;
import com.forvmom.common.dto.response.AddonResponseDto;
import com.forvmom.common.dto.response.BulkAttachAddonResultDto;
import com.forvmom.common.dto.response.BulkAttachAddonResultDto.SkippedAddonDto;
import com.forvmom.common.dto.response.BulkAttachAddonResultDto.SkippedAddonDto.Reason;
import com.forvmom.common.dto.response.ExperienceAddonResponseDto;
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
import java.util.ArrayList;
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

    // ── Master Addon CRUD ─────────────────────────────────────────────────────

    @Override
    @Transactional
    public AddonResponseDto createAddon(AddonRequestDto requestDto) {
        Addon addon = AddonBeanMapper.mapRequestToAddon(requestDto);
        return AddonBeanMapper.mapAddonToDto(addonDao.save(addon));
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
        addonMapperDao.deleteAllByAddonId(id);
        addonDao.delete(existing);
        return true;
    }

    // ── Experience Attachment ─────────────────────────────────────────────────

    @Override
    @Transactional
    public ExperienceAddonResponseDto attachToExperience(Long experienceId, Long addonId,
            BigDecimal priceOverride, Boolean isFree) {
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
        // Bidirectional helper — sets experience on mapper and adds to experience's set
        experience.addAddonMapper(mapper);

        return AddonBeanMapper.mapAddonMapperToDto(addonMapperDao.save(mapper));
    }

    /**
     * Bulk-attach addons to an experience. Skips (does not fail for) items that are
     * already attached or whose addon ID does not exist.
     */
    @Override
    @Transactional
    public BulkAttachAddonResultDto attachAddons(Long experienceId,
            BulkAttachAddonRequestDto requestDto) {
        List<ExperienceAddonResponseDto> attached = new ArrayList<>();
        List<SkippedAddonDto> skipped = new ArrayList<>();

        for (AddonAttachItem item : requestDto.getItems()) {
            Long addonId = item.getAddonId();
            try {
                ExperienceAddonResponseDto result = attachToExperience(
                        experienceId,
                        addonId,
                        item.getPriceOverride(),
                        item.getIsFree());
                attached.add(result);
            } catch (IllegalStateException e) {
                // Already attached
                skipped.add(new SkippedAddonDto(addonId, Reason.DUPLICATE, e.getMessage()));
            } catch (ResourceNotFoundException e) {
                // Addon or experience not found
                skipped.add(new SkippedAddonDto(addonId, Reason.NOT_FOUND, e.getMessage()));
            }
        }

        return new BulkAttachAddonResultDto(attached, skipped);
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
    public List<ExperienceAddonResponseDto> getAddonsForExperience(Long experienceId) {
        List<ExperienceAddonMapper> mappers = addonMapperDao.findByExperienceId(experienceId);
        return AddonBeanMapper.mapAddonMappers(mappers);
    }
}
