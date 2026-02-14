package com.forvmom.core.controller.admin;

import com.forvmom.common.dto.request.ReorderRequestDto;
import com.forvmom.common.response.ApiResponse;
import com.forvmom.common.response.ResponseUtil;
import com.forvmom.common.utils.AppConstants;
import com.forvmom.common.dto.request.CategoryRequestDto;
import com.forvmom.common.dto.response.CategoryResponseDto;
import com.forvmom.core.services.CategoryService;
import com.forvmom.core.services.ReorderingService;
import com.forvmom.data.entities.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/admin/categories")
@Tag(name = "Admin Category API", description = "Endpoints for managing categories (Admin only)")
public class CategoryControllerAdmin {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ReorderingService reorderingService;

    @PostMapping
    @Operation(summary = "Create Category", description = "Create a new category")
    public ResponseEntity<ApiResponse<?>> createCategory(@RequestBody CategoryRequestDto categoryRequestDto) {
        CategoryResponseDto categoryResponse = categoryService.createCategory(categoryRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.buildCreatedResponse(categoryResponse, AppConstants.MSG_CREATED));

    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Category by ID", description = "Fetch a category by its ID")
    public ResponseEntity<ApiResponse<?>> getCategoryById(@PathVariable Long id) {
        CategoryResponseDto categoryResponse = categoryService.getById(id);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(categoryResponse, AppConstants.MSG_FETCHED));
    }

    @GetMapping
    @Operation(summary = "Get All Categories", description = "Fetch all categories")
    public ResponseEntity<ApiResponse<?>> getAllCategories() {
        List<CategoryResponseDto> categoryDtos = categoryService.getAll();
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(categoryDtos, AppConstants.MSG_FETCHED));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Category", description = "Update an existing category")
    public ResponseEntity<ApiResponse<?>> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryRequestDto categoryRequestDto) {
        CategoryResponseDto updateCategory = categoryService.updateCategory(id, categoryRequestDto);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(updateCategory, AppConstants.MSG_UPDATED));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Category", description = "Delete a category by ID")
    public ResponseEntity<ApiResponse<?>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(null, AppConstants.MSG_DELETED));
    }

    @PatchMapping("/reorder")
    public ResponseEntity<ApiResponse<?>> reOrderTheItems(
            @RequestBody ReorderRequestDto reorderRequestDto) {
        reorderingService.reorderItems(reorderRequestDto.getId(), reorderRequestDto.getNewPosition(), Category.class);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(null, AppConstants.MSG_UPDATED));
    }

}