package com.example.moment_forever.core.controller.admin;

import com.example.moment_forever.common.response.ApiResponse;
import com.example.moment_forever.common.response.ResponseUtil;
import com.example.moment_forever.common.utils.AppConstants;
import com.example.moment_forever.core.dto.SubCategoryDto;
import com.example.moment_forever.core.services.SubCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/subcategories")
public class SubCategoryControllerAdmin {

    @Autowired
    private SubCategoryService subCategoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createSubCategory(@RequestBody SubCategoryDto subCategoryDto) {
        SubCategoryDto subCategoryResponse = subCategoryService.createSubCategory(subCategoryDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.buildCreatedResponse(subCategoryResponse, AppConstants.MSG_CREATED));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getSubCategoryById(@PathVariable Long id) {
        SubCategoryDto subCategoryResponse = subCategoryService.getById(id);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(subCategoryResponse, AppConstants.MSG_FETCHED)
        );
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<?>> getSubCategoryBySlug(@PathVariable String slug) {
        SubCategoryDto subCategoryResponse = subCategoryService.getBySlug(slug);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(subCategoryResponse, AppConstants.MSG_FETCHED)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllSubCategories() {
        List<SubCategoryDto> subCategoryDtos = subCategoryService.getAll();
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(subCategoryDtos, AppConstants.MSG_FETCHED)
        );
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<?>> getSubCategoriesByCategory(@PathVariable Long categoryId) {
        List<SubCategoryDto> subCategoryDtoList = subCategoryService.getByCategoryId(categoryId);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(subCategoryDtoList, AppConstants.MSG_FETCHED)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateSubCategory(
            @PathVariable Long id,
            @RequestBody SubCategoryDto subCategoryDto) {
        SubCategoryDto updatedSubCategory = subCategoryService.updateSubCategory(id, subCategoryDto);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(updatedSubCategory, AppConstants.MSG_UPDATED)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteSubCategory(@PathVariable Long id) {
        subCategoryService.deleteSubCategory(id);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(null, AppConstants.MSG_DELETED)
        );
    }
}