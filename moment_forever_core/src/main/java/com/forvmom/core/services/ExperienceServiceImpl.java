package com.forvmom.core.services;

import com.forvmom.common.dto.request.ExperienceCreateRequestDto;
import com.forvmom.common.dto.request.ExperienceDetailRequestDto;
import com.forvmom.common.dto.response.ExperienceDetailResponseDto;
import com.forvmom.common.dto.response.ExperienceHighlightResponseDto;
import com.forvmom.common.dto.response.ExperienceResponseDto;
import com.forvmom.common.errorhandler.ResourceNotFoundException;
import com.forvmom.core.mapper.ExperienceBeanMapper;
import com.forvmom.data.dao.ExperienceDao;
import com.forvmom.data.dao.ExperienceDetailDao;
import com.forvmom.data.dao.SubCategoryDao;
import com.forvmom.data.entities.Experience;
import com.forvmom.data.entities.ExperienceDetail;
import com.forvmom.data.entities.SubCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExperienceServiceImpl implements ExperienceService {

    @Autowired
    private ExperienceDao experienceDao;

    @Autowired
    private ExperienceDetailDao experienceDetailDao;

    @Autowired
    private SubCategoryDao subCategoryDao;

    @Override
    @Transactional
    public ExperienceResponseDto createExperience(ExperienceCreateRequestDto requestDto) {
        if (experienceDao.existsBySlug(requestDto.getSlug())) {
            throw new IllegalArgumentException("Experience with slug '" + requestDto.getSlug() + "' already exists");
        }

        SubCategory subCategory = subCategoryDao.findById(requestDto.getSubCategoryId());
        if (subCategory == null) {
            throw new ResourceNotFoundException("SubCategory not found with id " + requestDto.getSubCategoryId());
        }

        // Save basic experience row
        Experience experience = new Experience();
        ExperienceBeanMapper.mapCreateDtoToEntity(requestDto, experience);
        experience.setSubCategory(subCategory);
        Experience saved = experienceDao.save(experience);

        // Save detail row (always created with the experience)
        ExperienceDetail detail = new ExperienceDetail();
        ExperienceBeanMapper.mapCreateDtoToDetail(requestDto, detail);
        detail.setExperience(saved);
        ExperienceDetail savedDetail = experienceDetailDao.save(detail);
        saved.setDetail(savedDetail);

        return ExperienceBeanMapper.mapEntityToDto(saved, true);
    }

    @Override
    @Transactional
    public ExperienceResponseDto updateExperience(Long id, ExperienceCreateRequestDto requestDto) {
        Experience existing = experienceDao.findByIdWithDetail(id);
        if (existing == null) {
            throw new ResourceNotFoundException("Experience not found with id " + id);
        }

        if (!existing.getSlug().equals(requestDto.getSlug()) &&
                experienceDao.existsBySlug(requestDto.getSlug())) {
            throw new IllegalArgumentException("Experience with slug '" + requestDto.getSlug() + "' already exists");
        }

        if (!existing.getSubCategory().getId().equals(requestDto.getSubCategoryId())) {
            SubCategory subCategory = subCategoryDao.findById(requestDto.getSubCategoryId());
            if (subCategory == null) {
                throw new ResourceNotFoundException("SubCategory not found with id " + requestDto.getSubCategoryId());
            }
            existing.setSubCategory(subCategory);
        }

        // Update basic fields
        ExperienceBeanMapper.mapCreateDtoToEntity(requestDto, existing);
        Experience updated = experienceDao.update(existing);

        // Upsert detail
        ExperienceDetail detail = updated.getDetail();
        if (detail == null) {
            detail = new ExperienceDetail();
            detail.setExperience(updated);
        }
        ExperienceBeanMapper.mapCreateDtoToDetail(requestDto, detail);
        ExperienceDetail savedDetail = (detail.getId() == null)
                ? experienceDetailDao.save(detail)
                : experienceDetailDao.update(detail);
        updated.setDetail(savedDetail);

        return ExperienceBeanMapper.mapEntityToDto(updated, true);
    }

    @Override
    @Transactional(readOnly = true)
    public ExperienceResponseDto getById(Long id) {
        Experience experience = experienceDao.findByIdWithDetail(id);
        if (experience == null) {
            throw new ResourceNotFoundException("Experience not found with id " + id);
        }
        return ExperienceBeanMapper.mapEntityToDto(experience, true);
    }

    @Override
    @Transactional(readOnly = true)
    public ExperienceResponseDto getBySlug(String slug) {
        Experience experience = experienceDao.findBySlugWithDetail(slug);
        if (experience == null) {
            throw new ResourceNotFoundException("Experience not found with slug '" + slug + "'");
        }
        return ExperienceBeanMapper.mapEntityToDto(experience, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExperienceHighlightResponseDto> getAll() {
        List<Experience> experiences = experienceDao.findAllWithDetail();
        if (experiences == null || experiences.isEmpty())
            return new ArrayList<>();
        return experiences.stream()
                .map(ExperienceBeanMapper::mapEntityToHighlightDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExperienceHighlightResponseDto> getAllActive() {
        List<Experience> experiences = experienceDao.findAllActive();
        if (experiences == null || experiences.isEmpty())
            return new ArrayList<>();
        return experiences.stream()
                .map(ExperienceBeanMapper::mapEntityToHighlightDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExperienceHighlightResponseDto> getBySubCategory(Long subCategoryId) {
        List<Experience> experiences = experienceDao.findBySubCategoryId(subCategoryId);
        if (experiences == null || experiences.isEmpty()) {
            throw new ResourceNotFoundException("No experiences found for sub-category id " + subCategoryId);
        }
        return experiences.stream()
                .map(ExperienceBeanMapper::mapEntityToHighlightDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExperienceHighlightResponseDto> getFeatured() {
        List<Experience> experiences = experienceDao.findFeatured();
        if (experiences == null || experiences.isEmpty())
            return new ArrayList<>();
        return experiences.stream()
                .map(ExperienceBeanMapper::mapEntityToHighlightDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean deleteExperience(Long id) {
        Experience existing = experienceDao.findById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("Experience not found with id " + id);
        }
        experienceDao.delete(existing);
        return true;
    }

    @Override
    @Transactional
    public void toggleActive(Long id) {
        Experience existing = experienceDao.findById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("Experience not found with id " + id);
        }
        existing.setActive(!existing.isActive());
        experienceDao.update(existing);
    }

    @Override
    @Transactional
    public void toggleFeatured(Long id) {
        Experience existing = experienceDao.findById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("Experience not found with id " + id);
        }
        existing.setIsFeatured(!existing.getIsFeatured());
        experienceDao.update(existing);
    }

    @Override
    @Transactional
    public ExperienceDetailResponseDto upsertDetail(Long experienceId, ExperienceDetailRequestDto requestDto) {
        Experience experience = experienceDao.findById(experienceId);
        if (experience == null) {
            throw new ResourceNotFoundException("Experience not found with id " + experienceId);
        }

        ExperienceDetail detail = experienceDetailDao.findByExperienceId(experienceId);
        if (detail == null) {
            detail = new ExperienceDetail();
            detail.setExperience(experience);
        }

        ExperienceBeanMapper.mapDetailDtoToEntity(requestDto, detail);
        ExperienceDetail saved = (detail.getId() == null)
                ? experienceDetailDao.save(detail)
                : experienceDetailDao.update(detail);

        return ExperienceBeanMapper.mapDetailToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ExperienceDetailResponseDto getDetail(Long experienceId) {
        ExperienceDetail detail = experienceDetailDao.findByExperienceId(experienceId);
        if (detail == null) {
            throw new ResourceNotFoundException("No detail found for experience id " + experienceId);
        }
        return ExperienceBeanMapper.mapDetailToDto(detail);
    }
}
