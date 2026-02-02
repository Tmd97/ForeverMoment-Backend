package com.example.moment_forever.test.mapper;

import com.example.moment_forever.data.entities.Category;
import com.example.moment_forever.test.dto.CategoryDto;

public class CategoryBeanMapper {
    public static void mapEntityToDto(Category category, CategoryDto categoryDto) {
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        category.setDesc(category.getDesc());

    }

    public static void mapDtoToEntity(CategoryDto categoryDto, Category category) {
        category.setName(categoryDto.getName());
        category.setDesc(categoryDto.getDesc());
        category.setEnabled(true);

    }
}
