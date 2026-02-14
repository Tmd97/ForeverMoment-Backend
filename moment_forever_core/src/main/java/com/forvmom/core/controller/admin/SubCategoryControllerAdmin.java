package com.forvmom.core.controller.admin;

import com.forvmom.common.dto.request.ReorderRequestDto;
import com.forvmom.common.response.ApiResponse;
import com.forvmom.common.response.ResponseUtil;
import com.forvmom.common.utils.AppConstants;
import com.forvmom.common.dto.request.SubCategoryRequestDto;
import com.forvmom.common.dto.response.SubCategoryResponseDto;
import com.forvmom.core.services.ReorderingService;
import com.forvmom.core.services.SubCategoryService;
import com.forvmom.data.entities.Category;
import com.forvmom.data.entities.SubCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/admin/subcategories")
@Tag(name = "Admin SubCategory API", description = "Endpoints for managing sub-categories (Admin only)")
public class SubCategoryControllerAdmin {

        @Autowired
        private SubCategoryService subCategoryService;

        @Autowired
        private ReorderingService reorderingService;

        @PostMapping("/category")
        @Operation(summary = "Create SubCategory", description = "Create a new sub-category")
        public ResponseEntity<ApiResponse<?>> createSubCategory(
                        @RequestBody SubCategoryRequestDto subCategoryRequestDto) {
                SubCategoryResponseDto subCategoryResponse = subCategoryService
                                .createSubCategory(subCategoryRequestDto);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ResponseUtil.buildCreatedResponse(subCategoryResponse, AppConstants.MSG_CREATED));
        }

        @GetMapping("/{id}")
        @Operation(summary = "Get SubCategory by ID", description = "Fetch a sub-category by its ID")
        public ResponseEntity<ApiResponse<?>> getSubCategoryById(@PathVariable Long id) {
                SubCategoryResponseDto subCategoryResponse = subCategoryService.getById(id);
                return ResponseEntity.ok(
                                ResponseUtil.buildOkResponse(subCategoryResponse, AppConstants.MSG_FETCHED));
        }

        @GetMapping("/slug/{slug}")
        public ResponseEntity<ApiResponse<?>> getSubCategoryBySlug(@PathVariable String slug) {
                SubCategoryResponseDto subCategoryResponse = subCategoryService.getBySlug(slug);
                return ResponseEntity.ok(
                                ResponseUtil.buildOkResponse(subCategoryResponse, AppConstants.MSG_FETCHED));
        }

        @GetMapping
        @Operation(summary = "Get All SubCategories", description = "Fetch all sub-categories")
        public ResponseEntity<ApiResponse<?>> getAllSubCategories() {
                List<SubCategoryResponseDto> subCategoryResponse = subCategoryService.getAll();
                return ResponseEntity.ok(
                                ResponseUtil.buildOkResponse(subCategoryResponse, AppConstants.MSG_FETCHED));
        }

        @GetMapping("/category/{categoryId}")
        public ResponseEntity<ApiResponse<?>> getSubCategoriesByCategory(@PathVariable Long categoryId) {
                List<SubCategoryResponseDto> subCategoryResponseList = subCategoryService.getByCategoryId(categoryId);
                return ResponseEntity.ok(
                                ResponseUtil.buildOkResponse(subCategoryResponseList, AppConstants.MSG_FETCHED));
        }

        @PutMapping("/{id}/associate/{categoryId}")
        public ResponseEntity<ApiResponse<?>> associateSubCategoryToCategory(@PathVariable Long id,
                        @PathVariable Long categoryId) {
                subCategoryService.associateSubCategoryToCategory(id, categoryId);
                return ResponseEntity.ok(
                                ResponseUtil.buildOkResponse(null,
                                                "Sub-category associated with category successfully"));
        }

        @PutMapping("/{id}")
        @Operation(summary = "Update SubCategory", description = "Update an existing sub-category")
        public ResponseEntity<ApiResponse<?>> updateSubCategory(
                        @PathVariable Long id,
                        @RequestBody SubCategoryRequestDto subCategoryDto) {
                SubCategoryResponseDto subCategoryResponseDto = subCategoryService.updateSubCategory(id,
                                subCategoryDto);
                return ResponseEntity.ok(
                                ResponseUtil.buildOkResponse(subCategoryResponseDto, AppConstants.MSG_UPDATED));
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Delete SubCategory", description = "Delete a sub-category by ID")
        public ResponseEntity<ApiResponse<?>> deleteSubCategory(@PathVariable Long id) {
                subCategoryService.deleteSubCategory(id);
                return ResponseEntity.ok(
                                ResponseUtil.buildOkResponse(null, AppConstants.MSG_DELETED));
        }

        @PatchMapping("/reorder")
        public ResponseEntity<ApiResponse<?>> reOrderTheItems(
                        @RequestBody ReorderRequestDto reorderRequestDto) {
                reorderingService.reorderItems(reorderRequestDto.getId(), reorderRequestDto.getNewPosition(),
                                SubCategory.class);
                return ResponseEntity.ok(
                                ResponseUtil.buildOkResponse(null, AppConstants.MSG_UPDATED));
        }
}