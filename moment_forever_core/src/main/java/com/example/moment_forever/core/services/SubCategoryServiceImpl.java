package com.example.moment_forever.core.services;

import com.example.moment_forever.core.dto.SubCategoryDto;
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
    public SubCategoryDto createSubCategory(SubCategoryDto subCategoryDto) {
        // Check if SubCategory with same name already exists
        if (subCategoryDao.existsByName(subCategoryDto.getName())) {
            throw new IllegalArgumentException("SubCategory with name '" + subCategoryDto.getName() + "' already exists");
        }

        // Check if SubCategory with same slug already exists
        if (subCategoryDao.existsBySlug(subCategoryDto.getSlug())) {
            throw new IllegalArgumentException("SubCategory with slug '" + subCategoryDto.getSlug() + "' already exists");
        }

        // Get the Category
        Category category = categoryDao.findById(subCategoryDto.getCategoryId());
        if (category == null) {
            throw new IllegalArgumentException("Category with id " + subCategoryDto.getCategoryId() + " not found");
        }

        // Create SubCategory
        SubCategory subCategory = new SubCategory();
        SubCategoryBeanMapper.mapDtoToEntity(subCategoryDto, subCategory);

        // Set the Category relationship
        subCategory.setCategory(category);

        // Save
        SubCategory res=subCategoryDao.save(subCategory);
        return SubCategoryBeanMapper.mapEntityToDto(res);
    }

    @Override
    @Transactional
    public SubCategoryDto updateSubCategory(Long id, SubCategoryDto subCategoryDto) {
        SubCategory existing = subCategoryDao.findById(id);
        if (existing == null) {
            throw new RuntimeException("SubCategory not found with given Id " + id);
        }

        // If name is being changed, check for duplicates
        if (!existing.getName().equals(subCategoryDto.getName()) &&
                subCategoryDao.existsByName(subCategoryDto.getName())) {
            throw new IllegalArgumentException("SubCategory with name '" + subCategoryDto.getName() + "' already exists");
        }

        // If slug is being changed, check for duplicates
        if (!existing.getSlug().equals(subCategoryDto.getSlug()) &&
                subCategoryDao.existsBySlug(subCategoryDto.getSlug())) {
            throw new IllegalArgumentException("SubCategory with slug '" + subCategoryDto.getSlug() + "' already exists");
        }

        // Map updatable fields
        SubCategoryBeanMapper.mapDtoToEntity(subCategoryDto, existing);

        // If category is being changed, update relationship
        if (subCategoryDto.getCategoryId() != null &&
                !existing.getCategory().getId().equals(subCategoryDto.getCategoryId())) {
            Category newCategory = categoryDao.findById(subCategoryDto.getCategoryId());
            if (newCategory == null) {
                throw new IllegalArgumentException("Category with id " + subCategoryDto.getCategoryId() + " not found");
            }
            existing.setCategory(newCategory);
        }

        SubCategory res=subCategoryDao.update(existing);
        return SubCategoryBeanMapper.mapEntityToDto(res);
    }

    @Override
    @Transactional(readOnly = true)
    public SubCategoryDto getById(Long id) {
        SubCategory subCategory = subCategoryDao.findById(id);
        if (subCategory == null) {
            throw new RuntimeException("SubCategory with given Id " + id + " does not exist");
        }
        return SubCategoryBeanMapper.mapEntityToDto(subCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public SubCategoryDto getBySlug(String slug) {
        List<SubCategory> subCategories = subCategoryDao.findBySlug(slug);
        if (subCategories.isEmpty()) {
            throw new RuntimeException("SubCategory with slug '" + slug + "' does not exist");
        }
        return SubCategoryBeanMapper.mapEntityToDto(subCategories.get(0));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubCategoryDto> getAll() {
        List<SubCategory> subCategories = subCategoryDao.findAll();
        if (subCategories == null || subCategories.isEmpty()) {
            throw new RuntimeException("No SubCategories exist");
        }
        return subCategories.stream()
                .map(SubCategoryBeanMapper::mapEntityToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubCategoryDto> getByCategoryId(Long categoryId) {
        List<SubCategory> subCategories = subCategoryDao.findByCategoryId(categoryId);
        if (subCategories == null || subCategories.isEmpty()) {
            throw new RuntimeException("No SubCategories found for category id " + categoryId);
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
            throw new RuntimeException("No such SubCategory for given Id exist " + id);
        }
        subCategoryDao.delete(existing);
        return true;
    }
}