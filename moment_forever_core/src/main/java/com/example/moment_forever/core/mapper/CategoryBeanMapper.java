package com.example.moment_forever.core.mapper;

import com.example.moment_forever.common.dto.request.CategoryRequestDto;
import com.example.moment_forever.common.dto.request.SubCategoryRequestDto;
import com.example.moment_forever.common.dto.response.CategoryResponseDto;
import com.example.moment_forever.common.dto.response.SubCategoryResponseDto;
import com.example.moment_forever.data.entities.Category;
import com.example.moment_forever.data.entities.SubCategory;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryBeanMapper {

    public static void mapDtoToEntity(CategoryRequestDto dto, Category entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setSlug(dto.getSlug());
        entity.setDisplayOrder(dto.getDisplayOrder());
        entity.setActive(dto.getIsActive());
    }
    // public static void mapDtoToEntity(CategoryRequestDto dto, Category entity) {
    // entity.setName(dto.getName());
    // entity.setDescription(dto.getDescription());
    // entity.setSlug(dto.getSlug());
    // entity.setDisplayOrder(dto.getDisplayOrder());
    //
    // // Handle SubCategories mapping
    // if (dto.getSubCategories() != null && !dto.getSubCategories().isEmpty()) {
    // for (SubCategoryRequestDto subCatDto : dto.getSubCategories()) {
    // SubCategory subCategory = new SubCategory();
    // SubCategoryBeanMapper.mapDtoToEntity(subCatDto, subCategory);
    // entity.setSubCategory(subCategory);
    // }
    // }
    // }

    public static CategoryResponseDto mapEntityToDto(Category entity) {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setSlug(entity.getSlug());
        dto.setDisplayOrder(entity.getDisplayOrder());
        dto.setIsActive(entity.isActive());

        // Map SubCategories
        if (entity.getSubCategories() == null || entity.getSubCategories().isEmpty()) {
            return dto;
        }
        List<SubCategoryResponseDto> subCatDtos = entity.getSubCategories().stream()
                .map(SubCategoryBeanMapper::mapEntityToDto)
                .collect(Collectors.toList());
        dto.setSubCategories(subCatDtos);

        return dto;
    }
}
