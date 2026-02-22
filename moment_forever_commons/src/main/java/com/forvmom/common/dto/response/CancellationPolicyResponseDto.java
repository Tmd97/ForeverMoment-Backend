package com.forvmom.common.dto.response;

import java.util.Date;

public class CancellationPolicyResponseDto extends NamedEntityDto {

    private String description;
    /** true = favourable ✓, false = restrictive ✗ */
    private Boolean isIncluded;
    private Integer displayOrder;
    private Boolean isActive;
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsIncluded() {
        return isIncluded;
    }

    public void setIsIncluded(Boolean isIncluded) {
        this.isIncluded = isIncluded;
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
}
