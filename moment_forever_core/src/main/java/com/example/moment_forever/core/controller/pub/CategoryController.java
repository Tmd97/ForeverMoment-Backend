package com.example.moment_forever.core.controller.pub;

import com.example.moment_forever.common.response.ApiResponse;
import com.example.moment_forever.common.response.ResponseUtil;
import com.example.moment_forever.common.utils.AppConstants;
import com.example.moment_forever.common.dto.response.CategoryResponseDto;
import com.example.moment_forever.core.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getCategoryById(@PathVariable Long id) {
        CategoryResponseDto categoryResponse = categoryService.getById(id);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(categoryResponse, AppConstants.MSG_FETCHED)
        );
    }


    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllCategories() {
        List<CategoryResponseDto> categoryDtos = categoryService.getAll();
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(categoryDtos, AppConstants.MSG_FETCHED)
        );
    }
}