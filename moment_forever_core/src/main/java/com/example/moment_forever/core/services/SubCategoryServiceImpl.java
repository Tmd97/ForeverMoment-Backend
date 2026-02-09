package com.example.moment_forever.core.services;

import com.example.moment_forever.common.errorhandler.ResourceNotFoundException;
import com.example.moment_forever.common.dto.request.SubCategoryRequestDto;
import com.example.moment_forever.common.dto.response.SubCategoryResponseDto;
import com.example.moment_forever.core.mapper.SubCategoryBeanMapper;
import com.example.moment_forever.data.dao.CategoryDao;
import com.example.moment_forever.data.dao.SubCategoryDao;
import com.example.moment_forever.data.entities.Category;
import com.example.moment_forever.data.entities.SubCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubCategoryServiceImpl implements SubCategoryService {

    @Autowired
    private SubCategoryDao subCategoryDao;

    @Autowired
    private CategoryDao categoryDao;

    @Override
    @Transactional
    public SubCategoryResponseDto createSubCategory(SubCategoryRequestDto requestDto) {
        // Check if SubCategory with same name already exists
        if (subCategoryDao.existsByName(requestDto.getName())) {
            throw new IllegalArgumentException("SubCategory with name '" + requestDto.getName() + "' already exists");
        }

        // Check if SubCategory with same slug already exists
        if (subCategoryDao.existsBySlug(requestDto.getSlug())) {
            throw new IllegalArgumentException("SubCategory with slug '" + requestDto.getSlug() + "' already exists");
        }

        // Get the Category
        if(requestDto.getCategoryId() == null) {
            throw new IllegalArgumentException("Category id must be provided for SubCategory creation");
        }
        Category category = categoryDao.findById(requestDto.getCategoryId());
        if (category == null) {
            throw new IllegalArgumentException("Category with id " + requestDto.getCategoryId() + " not found");
        }

        // Create SubCategory
        SubCategory subCategory = new SubCategory();
        SubCategoryBeanMapper.mapDtoToEntity(requestDto, subCategory);

        // Set the Category relationship
        subCategory.setCategory(category);

        // Save
        SubCategory saved = subCategoryDao.save(subCategory);

        return SubCategoryBeanMapper.mapEntityToDto(saved);
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
        if (!existing.getSlug().equals(requestDto.getSlug()) &&
                subCategoryDao.existsBySlug(requestDto.getSlug())) {
            throw new IllegalArgumentException("SubCategory with slug '" + requestDto.getSlug() + "' already exists");
        }

        // Map updatable fields
        SubCategoryBeanMapper.mapDtoToEntity(requestDto, existing);
        // If category is being changed, update relationship
        if (requestDto.getCategoryId() != null &&
                !existing.getCategory().getId().equals(requestDto.getCategoryId())) {
            Category newCategory = categoryDao.findById(requestDto.getCategoryId());
            if (newCategory == null) {
                throw new IllegalArgumentException("Category with id " + requestDto.getCategoryId() + " not found");
            }
            existing.setCategory(newCategory);
        }

        SubCategory updated = subCategoryDao.update(existing);

        return SubCategoryBeanMapper.mapEntityToDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public SubCategoryResponseDto getById(Long id) {
        SubCategory subCategory = subCategoryDao.findById(id);
        if (subCategory == null) {
            throw new ResourceNotFoundException("SubCategory with id " + id + " does not exist");
        }
        return SubCategoryBeanMapper.mapEntityToDto(subCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public SubCategoryResponseDto getBySlug(String slug) {
        List<SubCategory> subCategories = subCategoryDao.findBySlug(slug);
        if (subCategories.isEmpty()) {
            throw new ResourceNotFoundException("SubCategory with slug '" + slug + "' does not exist");
        }
        return SubCategoryBeanMapper.mapEntityToDto(subCategories.get(0));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubCategoryResponseDto> getAll() {
        List<SubCategory> subCategories = subCategoryDao.findAll();
        if (subCategories == null || subCategories.isEmpty()) {
            throw new ResourceNotFoundException("No SubCategories exist");
        }
        return subCategories.stream()
                .map(SubCategoryBeanMapper::mapEntityToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubCategoryResponseDto> getByCategoryId(Long categoryId) {
        List<SubCategory> subCategories = subCategoryDao.findByCategoryId(categoryId);
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
}