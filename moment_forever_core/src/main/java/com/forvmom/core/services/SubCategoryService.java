package com.forvmom.core.services;

import com.forvmom.common.dto.request.SubCategoryRequestDto;
import com.forvmom.common.dto.response.SubCategoryResponseDto;

import java.util.List;

public interface SubCategoryService {

    SubCategoryResponseDto createSubCategory(SubCategoryRequestDto subCategoryDto);

    SubCategoryResponseDto updateSubCategory(Long id, SubCategoryRequestDto subCategoryDto);

    SubCategoryResponseDto getById(Long id);

    SubCategoryResponseDto getBySlug(String slug);

    List<SubCategoryResponseDto> getAll();

    List<SubCategoryResponseDto> getByCategoryId(Long categoryId);

    boolean deleteSubCategory(Long id);

   void associateSubCategoryToCategory(Long id, Long categoryId);
}