package com.example.moment_forever.core.admin.services;

import api.response.ApiResponse;
import api.response.ResponseUtil;
import com.example.moment_forever.core.admin.mapper.CategoryBeanMapper;
import com.example.moment_forever.core.admin.dto.CategoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.moment_forever.data.dao.CategoryDao;
import com.example.moment_forever.data.entities.Category;

@Service
public class CategoryService {

    @Autowired
    public CategoryDao categoryDao;


    public ApiResponse<?> createCategory(CategoryDto categoryDto) {
        try {
            // Validate category name uniqueness
//            if (categoryDao.existsByName(categoryDto.getName())) {
//                return ApiResponse.<CategoryDto>builder()
//                        .setCode(409)
//                        .setStatus("ERROR")
//                        .setMsg("Category with this name already exists")
//                        .setResponse(null)
//                        .build();
//            }
            // Convert DTO to Entity
            Category category = new Category();
            CategoryBeanMapper.mapDtoToEntity(categoryDto, category);
            Category categoryResponse = categoryDao.save(category);
            return ResponseUtil.buildOkResponse(categoryResponse, "Created the category");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
