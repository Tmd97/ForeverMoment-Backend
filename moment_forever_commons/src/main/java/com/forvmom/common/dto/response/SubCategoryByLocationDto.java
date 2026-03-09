package com.forvmom.common.dto.response;

public class SubCategoryByLocationDto {
    private Long id;
    private String name;
    private String slug;
    private Long categoryId;
    private String categoryName;
    private Integer displayOrder;

    public SubCategoryByLocationDto(Long id, String name, String slug, Long categoryId, String categoryName, Integer displayOrder) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.displayOrder = displayOrder;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }
}