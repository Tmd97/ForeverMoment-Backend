package com.forvmom.common.dto.request;

public class CategoryLocationAttachRequestDto {

    private Integer displayOrder; // optional

    private Boolean isActive; // optional, defaults to true

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