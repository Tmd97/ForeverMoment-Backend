package com.forvmom.core.services;

import com.forvmom.common.dto.request.ReorderRequestDto;
import com.forvmom.common.errorhandler.ResourceNotFoundException;
import com.forvmom.common.dto.request.CategoryRequestDto;
import com.forvmom.common.dto.response.CategoryResponseDto;
import com.forvmom.common.response.ApiResponse;
import com.forvmom.core.mapper.CategoryBeanMapper;
import com.forvmom.data.dao.CategoryDao;
import com.forvmom.data.entities.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CategoryService extends ReorderingService {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private ReorderingService reorderingService;

    // TODO remove from request the display Order, as backend have done set and
    // return
    public CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto) {
        if (categoryDao.existsByName(categoryRequestDto.getName())) {
            throw new IllegalArgumentException(
                    "Category with name '" + categoryRequestDto.getName() + "' already exists");
        }
        Category category = new Category();
        CategoryBeanMapper.mapDtoToEntity(categoryRequestDto, category);
        Long max = reorderingService.getMaxOrder(Category.class);
        category.setDisplayOrder(max + 1);
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
        // Use optimized query to fetch Category + SubCategories
        Category category = categoryDao.findByIdWithSubCategories(id);
        if (category == null) {
            throw new ResourceNotFoundException("Category with given Id " + id + " is not exist");
        }
        return CategoryBeanMapper.mapEntityToDto(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAll() {
        // Use optimized query to fetch Categories + SubCategories
        List<Category> categories = categoryDao.findAllWithSubCategories();
        if (categories == null || categories.isEmpty()) {
            return new ArrayList<>();
        } else {
            return categories.stream()
                    .map(CategoryBeanMapper::mapEntityToDto)
                    .toList();
        }
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

    public void reorderCategories(Long id, Long newPosition) {
        reorderingService.reorderItems(id, newPosition, Category.class);

    }
}