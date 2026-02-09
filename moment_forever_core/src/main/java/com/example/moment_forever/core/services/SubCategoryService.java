package com.example.moment_forever.core.services;

import com.example.moment_forever.common.dto.request.SubCategoryRequestDto;
import com.example.moment_forever.common.dto.response.SubCategoryResponseDto;

import java.util.List;

public interface SubCategoryService {

    SubCategoryResponseDto createSubCategory(SubCategoryRequestDto subCategoryDto);

    SubCategoryResponseDto updateSubCategory(Long id, SubCategoryRequestDto subCategoryDto);

    SubCategoryResponseDto getById(Long id);

    SubCategoryResponseDto getBySlug(String slug);

    List<SubCategoryResponseDto> getAll();

    List<SubCategoryResponseDto> getByCategoryId(Long categoryId);

    boolean deleteSubCategory(Long id);
}