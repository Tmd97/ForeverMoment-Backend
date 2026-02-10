package com.example.moment_forever.core.services;

import com.example.moment_forever.common.errorhandler.ResourceNotFoundException;
import com.example.moment_forever.common.dto.request.CategoryRequestDto;
import com.example.moment_forever.common.dto.request.SubCategoryRequestDto;
import com.example.moment_forever.common.dto.response.CategoryResponseDto;
import com.example.moment_forever.core.mapper.CategoryBeanMapper;
import com.example.moment_forever.core.mapper.SubCategoryBeanMapper;
import com.example.moment_forever.data.dao.CategoryDao;
import com.example.moment_forever.data.entities.Category;
import com.example.moment_forever.data.entities.SubCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryDao categoryDao;

    public CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto) {
        if (categoryDao.existsByName(categoryRequestDto.getName())) {
            throw new IllegalArgumentException("Category with name '" + categoryRequestDto.getName() + "' already exists");
        }
        Category category = new Category();
        CategoryBeanMapper.mapDtoToEntity(categoryRequestDto, category);

        // to handle SubCategories
//        if (categoryRequestDto.getSubCategories() != null && !categoryRequestDto.getSubCategories().isEmpty()) {
//            for (SubCategoryRequestDto subCatDto : categoryRequestDto.getSubCategories()) {
//                SubCategory subCategory = new SubCategory();
//                SubCategoryBeanMapper.mapDtoToEntity(subCatDto, subCategory);
//                category.setSubCategory(subCategory);
//            }
//        }
        Category res = categoryDao.save(category);
        return CategoryBeanMapper.mapEntityToDto(res);
    }

    @Transactional
    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto categoryRequestDto) {

        Category existing = categoryDao.findById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("Category not found with given Id " + id);
        }
        // map only updatable fields
        CategoryBeanMapper.mapDtoToEntity(categoryRequestDto, existing);
        Category res = categoryDao.update(existing);
        return CategoryBeanMapper.mapEntityToDto(res);

    }

    @Transactional(readOnly = true)
    public CategoryResponseDto getById(Long id) {
        Category category = categoryDao.findById(id);
        if (category == null) {
            throw new ResourceNotFoundException("Category with given Id " + id + " is not exist");
        }
        return CategoryBeanMapper.mapEntityToDto(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAll() {
        List<Category> categories = categoryDao.findAll();
        if (categories == null || categories.size() == 0) {
            throw new ResourceNotFoundException("Categories doesn't exist");
        }
        return categories.stream()
                .map(CategoryBeanMapper::mapEntityToDto)
                .toList();
    }

    @Transactional
    public boolean deleteCategory(Long id) {
        Category existing = categoryDao.findById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("No such category for given Id exist " + id);
        }
        categoryDao.delete(existing);
        return true;
    }
}