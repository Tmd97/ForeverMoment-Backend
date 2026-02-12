package com.example.moment_forever.core.controller.admin;

import com.example.moment_forever.common.dto.request.ReorderRequestDto;
import com.example.moment_forever.common.response.ApiResponse;
import com.example.moment_forever.common.response.ResponseUtil;
import com.example.moment_forever.common.utils.AppConstants;
import com.example.moment_forever.common.dto.request.SubCategoryRequestDto;
import com.example.moment_forever.common.dto.response.SubCategoryResponseDto;
import com.example.moment_forever.core.services.ReorderingService;
import com.example.moment_forever.core.services.SubCategoryService;
import com.example.moment_forever.data.entities.Category;
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

    @Autowired
    private ReorderingService reorderingService;

    @PostMapping("/category")
    public ResponseEntity<ApiResponse<?>> createSubCategory(@RequestBody SubCategoryRequestDto subCategoryRequestDto) {
        SubCategoryResponseDto subCategoryResponse = subCategoryService.createSubCategory(subCategoryRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.buildCreatedResponse(subCategoryResponse, AppConstants.MSG_CREATED));
    }

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
        List<SubCategoryResponseDto> subCategoryResponse = subCategoryService.getAll();
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(subCategoryResponse, AppConstants.MSG_FETCHED)
        );
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<?>> getSubCategoriesByCategory(@PathVariable Long categoryId) {
        List<SubCategoryResponseDto> subCategoryResponseList = subCategoryService.getByCategoryId(categoryId);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(subCategoryResponseList, AppConstants.MSG_FETCHED)
        );
    }

    @PutMapping("/{id}/associate/{categoryId}")
    public ResponseEntity<ApiResponse<?>> associateSubCategoryToCategory(@PathVariable Long id,
                                                                         @PathVariable Long categoryId) {
        subCategoryService.associateSubCategoryToCategory(id, categoryId);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(null, "Sub-category associated with category successfully")
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateSubCategory(
            @PathVariable Long id,
            @RequestBody SubCategoryRequestDto subCategoryDto) {
        SubCategoryResponseDto subCategoryResponseDto = subCategoryService.updateSubCategory(id, subCategoryDto);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(subCategoryResponseDto, AppConstants.MSG_UPDATED)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteSubCategory(@PathVariable Long id) {
        subCategoryService.deleteSubCategory(id);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(null, AppConstants.MSG_DELETED)
        );
    }

    @PatchMapping("/reorder")
    public ResponseEntity<ApiResponse<?>> reOrderTheItems(
            @RequestBody ReorderRequestDto reorderRequestDto) {
        reorderingService.reorderItems(reorderRequestDto.getId(), reorderRequestDto.getNewPosition(), Category.class);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(null, AppConstants.MSG_UPDATED)
        );
    }
}