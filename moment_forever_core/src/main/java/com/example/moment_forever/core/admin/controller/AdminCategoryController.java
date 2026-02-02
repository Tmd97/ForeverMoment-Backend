package com.example.moment_forever.core.admin.controller;

import api.response.ApiResponse;
import com.example.moment_forever.core.admin.dto.CategoryDto;
import com.example.moment_forever.core.admin.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    @Autowired
    public CategoryService categoryService;

    @PostMapping
    public ApiResponse<?> createCategory(
            @Valid @RequestBody CategoryDto categoryDto) {
        ApiResponse<?> category = categoryService.createCategory(categoryDto);
        ApiResponse<?> response = ApiResponse.builder()
                .setCode(201)
                .setStatus("SUCCESS")
                .setMsg("Category Created successfully")
                .setResponse(category)
                .build();

        return response;
    }

}