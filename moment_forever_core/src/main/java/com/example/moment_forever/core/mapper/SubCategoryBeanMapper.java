package com.example.moment_forever.core.mapper;

import com.example.moment_forever.common.dto.request.SubCategoryRequestDto;
import com.example.moment_forever.common.dto.response.SubCategoryResponseDto;
import com.example.moment_forever.data.entities.SubCategory;

public class SubCategoryBeanMapper {

    public static void mapDtoToEntity(SubCategoryRequestDto dto, SubCategory entity) {
        if (dto == null || entity == null) return;

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setSlug(dto.getSlug());
        entity.setDisplayOrder(dto.getDisplayOrder());
        entity.setActive(dto.getIsActive());

    }

    public static SubCategoryResponseDto mapEntityToDto(SubCategory entity) {
        if (entity == null) return null;

        SubCategoryResponseDto dto = new SubCategoryResponseDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCategoryId(entity.getCategory().getId());
        dto.setDescription(entity.getDescription());
        dto.setSlug(entity.getSlug());
        dto.setDisplayOrder(entity.getDisplayOrder());
        dto.setIsActive(entity.isActive());

        // Set category info if category exists

        if (entity.getCategory() == null) {
            return dto;
        }
        dto.setCategoryId(entity.getCategory().getId());
        dto.setCategoryName(entity.getCategory().getName());
        dto.setCategorySlug(entity.getCategory().getSlug());

        return dto;
    }
}