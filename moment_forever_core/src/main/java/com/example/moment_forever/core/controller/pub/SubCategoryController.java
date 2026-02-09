package com.example.moment_forever.core.controller.pub;

import com.example.moment_forever.common.response.ApiResponse;
import com.example.moment_forever.common.response.ResponseUtil;
import com.example.moment_forever.common.utils.AppConstants;
import com.example.moment_forever.common.dto.response.SubCategoryResponseDto;
import com.example.moment_forever.core.services.SubCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/subcategories")
public class SubCategoryController {

    @Autowired
    private SubCategoryService subCategoryService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getSubCategoryById(@PathVariable Long id) {
        SubCategoryResponseDto subCategoryResponse = subCategoryService.getById(id);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(subCategoryResponse, AppConstants.MSG_FETCHED)
        );
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<?>> getSubCategoryBySlug(@PathVariable String slug) {
        SubCategoryResponseDto subCategoryResponse = subCategoryService.getBySlug(slug);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(subCategoryResponse, AppConstants.MSG_FETCHED)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllSubCategories() {
        List<SubCategoryResponseDto> subCategoryDtos = subCategoryService.getAll();
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(subCategoryDtos, AppConstants.MSG_FETCHED)
        );
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<?>> getSubCategoriesByCategory(@PathVariable Long categoryId) {
        List<SubCategoryResponseDto> subCategoryDtoList = subCategoryService.getByCategoryId(categoryId);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(subCategoryDtoList, AppConstants.MSG_FETCHED)
        );
    }
}