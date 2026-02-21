package com.forvmom.core.controller.pub;

import com.forvmom.common.dto.response.ExperienceHighlightResponseDto;
import com.forvmom.common.dto.response.ExperienceResponseDto;
import com.forvmom.common.response.ApiResponse;
import com.forvmom.common.response.ResponseUtil;
import com.forvmom.common.utils.AppConstants;
import com.forvmom.core.services.ExperienceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/experiences")
@Tag(name = "Public Experience API", description = "Endpoints for browsing experiences")
public class ExperienceController {

    @Autowired
    private ExperienceService experienceService;

    @GetMapping
    @Operation(summary = "Get All Active Experiences")
    public ResponseEntity<ApiResponse<?>> getAll() {
        List<ExperienceHighlightResponseDto> response = experienceService.getAllActive();
        return ResponseEntity.ok(ResponseUtil.buildOkResponse(response, AppConstants.MSG_FETCHED));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Experience by ID (with full detail)")
    public ResponseEntity<ApiResponse<?>> getById(@PathVariable Long id) {
        ExperienceResponseDto response = experienceService.getById(id);
        return ResponseEntity.ok(ResponseUtil.buildOkResponse(response, AppConstants.MSG_FETCHED));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get Experience by Slug (with full detail)")
    public ResponseEntity<ApiResponse<?>> getBySlug(@PathVariable String slug) {
        ExperienceResponseDto response = experienceService.getBySlug(slug);
        return ResponseEntity.ok(ResponseUtil.buildOkResponse(response, AppConstants.MSG_FETCHED));
    }

    @GetMapping("/subcategory/{subCategoryId}")
    @Operation(summary = "Get Experiences by SubCategory")
    public ResponseEntity<ApiResponse<?>> getBySubCategory(@PathVariable Long subCategoryId) {
        List<ExperienceHighlightResponseDto> response = experienceService.getBySubCategory(subCategoryId);
        return ResponseEntity.ok(ResponseUtil.buildOkResponse(response, AppConstants.MSG_FETCHED));
    }

    @GetMapping("/featured")
    @Operation(summary = "Get Featured Experiences", description = "Returns featured active experiences for homepage banners")
    public ResponseEntity<ApiResponse<?>> getFeatured() {
        List<ExperienceHighlightResponseDto> response = experienceService.getFeatured();
        return ResponseEntity.ok(ResponseUtil.buildOkResponse(response, AppConstants.MSG_FETCHED));
    }
}
