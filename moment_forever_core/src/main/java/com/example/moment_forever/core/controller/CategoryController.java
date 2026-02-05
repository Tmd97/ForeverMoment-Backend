package com.example.moment_forever.core.controller;

import com.example.moment_forever.common.response.ApiResponse;
import com.example.moment_forever.common.response.ResponseUtil;
import com.example.moment_forever.common.utils.AppConstants;
import com.example.moment_forever.core.dto.CategoryDto;
import com.example.moment_forever.core.mapper.CategoryBeanMapper;
import com.example.moment_forever.core.services.CategoryService;
import com.example.moment_forever.data.entities.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createCategory(@RequestBody CategoryDto categoryDto) {
        CategoryDto categoryResponse = categoryService.createCategory(categoryDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.buildCreatedResponse(categoryResponse, AppConstants.MSG_CREATED));

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getCategoryById(@PathVariable Long id) {
        CategoryDto categoryResponse = categoryService.getById(id);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(categoryResponse, AppConstants.MSG_FETCHED)
        );
    }


    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllCategories() {
        List<CategoryDto> categoryDtos = categoryService.getAll();
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(categoryDtos, AppConstants.MSG_FETCHED)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryDto categoryDto) {
        CategoryDto updateCategory = categoryService.updateCategory(id, categoryDto);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(updateCategory, AppConstants.MSG_UPDATED)
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(null, AppConstants.MSG_DELETED)
        );
    }

}