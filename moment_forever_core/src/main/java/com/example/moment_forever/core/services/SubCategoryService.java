package com.example.moment_forever.core.services;

import com.example.moment_forever.core.dto.SubCategoryDto;
import com.example.moment_forever.data.entities.SubCategory;
import java.util.List;

public interface SubCategoryService {

    SubCategoryDto createSubCategory(SubCategoryDto subCategoryDto);

    SubCategoryDto updateSubCategory(Long id, SubCategoryDto subCategoryDto);

    SubCategoryDto getById(Long id);

    SubCategoryDto getBySlug(String slug);

    List<SubCategoryDto> getAll();

    List<SubCategoryDto> getByCategoryId(Long categoryId);

    boolean deleteSubCategory(Long id);
}