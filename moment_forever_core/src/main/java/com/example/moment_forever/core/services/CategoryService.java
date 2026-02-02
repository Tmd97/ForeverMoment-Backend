package com.example.moment_forever.core.services;

import com.example.moment_forever.common.errorhandler.ResourceNotFoundException;
import com.example.moment_forever.common.utils.AppConstants;
import com.example.moment_forever.core.dto.CategoryDto;
import com.example.moment_forever.core.mapper.CategoryBeanMapper;
import com.example.moment_forever.data.dao.CategoryDao;
import com.example.moment_forever.data.entities.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryDao categoryDao;

    public Category createCategory(CategoryDto categoryDto) {
        if (categoryDao.existsByName(categoryDto.getName())) {
            throw new IllegalArgumentException("Category with name '" + categoryDto.getName() + "' already exists");
        }
        Category category = new Category();
        CategoryBeanMapper.mapDtoToEntity(categoryDto, category);
        return categoryDao.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, CategoryDto categoryDto) {

        Category existing = categoryDao.findById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("Category not found with given Id " + id);
        }
        // map only updatable fields
        CategoryBeanMapper.mapDtoToEntity(categoryDto, existing);
        return categoryDao.update(existing);
    }

    @Transactional(readOnly = true)
    public Category getById(Long id) {
        Category category = categoryDao.findById(id);
        if (category == null) {
            throw new ResourceNotFoundException("Category with given Id " + id + " is not exist");
        }
        return category;
    }

    @Transactional(readOnly = true)
    public List<Category> getAll() {
        List<Category> categories = categoryDao.findAll();
        if (categories == null || categories.size() == 0) {
            throw new ResourceNotFoundException("Categories doesn't exist");
        }
        return categories;
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