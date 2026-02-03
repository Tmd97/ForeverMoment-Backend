package com.example.moment_forever.core.mapper;

import com.example.moment_forever.core.dto.CategoryDto;
import com.example.moment_forever.core.dto.SubCategoryDto;
import com.example.moment_forever.data.entities.Category;
import com.example.moment_forever.data.entities.SubCategory;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryBeanMapper {

    public static void mapDtoToEntity(CategoryDto dto, Category entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setSlug(dto.getSlug());
        entity.setDisplayOrder(dto.getDisplayOrder());

        // Handle SubCategories mapping
        if (dto.getSubCategories() != null && !dto.getSubCategories().isEmpty()) {
            for (SubCategoryDto subCatDto : dto.getSubCategories()) {
                SubCategory subCategory = new SubCategory();
                SubCategoryBeanMapper.mapDtoToEntity(subCatDto, subCategory);
                entity.setSubCategory(subCategory);
            }
        }
    }

    public static CategoryDto mapEntityToDto(Category entity) {
        CategoryDto dto = new CategoryDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setSlug(entity.getSlug());
        dto.setDisplayOrder(entity.getDisplayOrder());

        // Map SubCategories
        if (entity.getSubCategories() == null || entity.getSubCategories().isEmpty()) {
            return dto;
        }
        List<SubCategoryDto> subCatDtos = entity.getSubCategories().stream()
                .map(SubCategoryBeanMapper::mapEntityToDto)
                .collect(Collectors.toList());
        dto.setSubCategories(subCatDtos);

        return dto;
    }
}
