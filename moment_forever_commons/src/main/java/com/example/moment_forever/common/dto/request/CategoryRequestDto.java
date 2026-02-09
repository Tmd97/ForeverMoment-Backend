package com.example.moment_forever.common.dto.request;

import java.util.ArrayList;
import java.util.List;

public class CategoryRequestDto {

    private String name;
    private String description;
    private String slug;
    private String icon;
    private Integer displayOrder;
    private Boolean isActive;
    private List<SubCategoryRequestDto> subCategories = new ArrayList<>();

    public CategoryRequestDto() {}

    public CategoryRequestDto(Long id, String name, String slug) {
        this.name = name;
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public List<SubCategoryRequestDto> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<SubCategoryRequestDto> subCategories) {
        this.subCategories = subCategories;
    }
}
