package com.forvmom.common.dto.request;

import jakarta.validation.constraints.PositiveOrZero;

public class SubCategoryLocationAttachRequestDto {
    @PositiveOrZero
    private Integer displayOrder;
    private Boolean isActive;

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}