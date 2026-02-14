package com.forvmom.core.services;

import com.forvmom.common.errorhandler.ResourceNotFoundException;
import com.forvmom.common.dto.request.SubCategoryRequestDto;
import com.forvmom.common.dto.response.SubCategoryResponseDto;
import com.forvmom.core.mapper.SubCategoryBeanMapper;
import com.forvmom.data.dao.CategoryDao;
import com.forvmom.data.dao.SubCategoryDao;
import com.forvmom.data.entities.Category;
import com.forvmom.data.entities.SubCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubCategoryServiceImpl implements SubCategoryService {

    @Autowired
    private SubCategoryDao subCategoryDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private ReorderingService reorderingService;

    @Override
    @Transactional
    public SubCategoryResponseDto createSubCategory(SubCategoryRequestDto requestDto) {
        // Check if SubCategory with same name already exists
        if (subCategoryDao.existsByName(requestDto.getName())) {
            throw new IllegalArgumentException("SubCategory with name '" + requestDto.getName() + "' already exists");
        }

        if (subCategoryDao.existsBySlug(requestDto.getSlug())) {
            throw new IllegalArgumentException("SubCategory with slug '" + requestDto.getSlug() + "' already exists");
        }

        Category category = categoryDao.findById(requestDto.getCategoryId());
        if (category == null) {
            throw new ResourceNotFoundException("No Category exist with id " + requestDto.getCategoryId());
        }

        // Link SubCategory to Category and save
        SubCategory subCategory = new SubCategory();
        SubCategoryBeanMapper.mapDtoToEntity(requestDto, subCategory);
        subCategory.setCategory(category);
        Long max = reorderingService.getMaxOrder(SubCategory.class);
        subCategory.setDisplayOrder(max + 1);
        SubCategory saved = subCategoryDao.save(subCategory);

        SubCategoryResponseDto responseDto = SubCategoryBeanMapper.mapEntityToDto(saved);
        return responseDto;
    }

    @Override
    @Transactional
    public SubCategoryResponseDto updateSubCategory(Long id, SubCategoryRequestDto requestDto) {
        SubCategory existing = subCategoryDao.findById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("SubCategory not found with id " + id);
        }

        // If name is being changed, check for duplicates
        if (!existing.getName().equals(requestDto.getName()) &&
                subCategoryDao.existsByName(requestDto.getName())) {
            throw new IllegalArgumentException("SubCategory with name '" + requestDto.getName() + "' already exists");
        }

        // If slug is being changed, check for duplicates
        if (existing.getSlug() != null && (!existing.getSlug().equals(requestDto.getSlug()) &&
                subCategoryDao.existsBySlug(requestDto.getSlug()))) {
            throw new IllegalArgumentException("SubCategory with slug '" + requestDto.getSlug() + "' already exists");
        }

        SubCategoryBeanMapper.mapDtoToEntity(requestDto, existing);
        SubCategory updated = subCategoryDao.update(existing);
        return SubCategoryBeanMapper.mapEntityToDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public SubCategoryResponseDto getById(Long id) {
        SubCategory subCategory = subCategoryDao.findByIdWithCategory(id);
        if (subCategory == null) {
            throw new ResourceNotFoundException("SubCategory with id " + id + " does not exist");
        }
        return SubCategoryBeanMapper.mapEntityToDto(subCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public SubCategoryResponseDto getBySlug(String slug) {
        List<SubCategory> subCategories = subCategoryDao.findBySlugWithCategory(slug);
        if (subCategories.isEmpty()) {
            throw new ResourceNotFoundException("SubCategory with slug '" + slug + "' does not exist");
        }
        return SubCategoryBeanMapper.mapEntityToDto(subCategories.get(0));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubCategoryResponseDto> getAll() {
        // Use optimized query to fetch SubCategories + Category
        List<SubCategory> subCategories = subCategoryDao.findAllWithCategory();
        if (subCategories == null || subCategories.isEmpty()) {
            return new ArrayList<>();
        } else {
            return subCategories.stream()
                    .map(SubCategoryBeanMapper::mapEntityToDto)
                    .toList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubCategoryResponseDto> getByCategoryId(Long categoryId) {
        // Use optimized query to fetch SubCategories + Category
        List<SubCategory> subCategories = subCategoryDao.findByCategoryIdWithCategory(categoryId);
        if (subCategories == null || subCategories.isEmpty()) {
            throw new ResourceNotFoundException("No SubCategories found for category id " + categoryId);
        }
        return subCategories.stream()
                .map(SubCategoryBeanMapper::mapEntityToDto)
                .toList();
    }

    @Override
    @Transactional
    public boolean deleteSubCategory(Long id) {
        SubCategory existing = subCategoryDao.findById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("No SubCategory exists with id " + id);
        }
        subCategoryDao.delete(existing);
        return true;
    }

    @Override
    @Transactional
    public void associateSubCategoryToCategory(Long id, Long categoryId) {
        SubCategory subCategory = subCategoryDao.findById(id);
        if (subCategory == null) {
            throw new ResourceNotFoundException("No SubCategory exists with id " + id);
        }

        Category category = categoryDao.findById(categoryId);
        if (category == null) {
            throw new ResourceNotFoundException("No Category exists with id " + categoryId);
        }

        subCategory.setCategory(category);
        subCategoryDao.update(subCategory);

    }
}