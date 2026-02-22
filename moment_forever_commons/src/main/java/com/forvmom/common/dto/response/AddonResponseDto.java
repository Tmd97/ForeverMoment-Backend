package com.forvmom.common.dto.response;

import java.math.BigDecimal;

/**
 * Response DTO for a master Addon record.
 * When returned as part of an experience, effectivePrice reflects the
 * per-experience price_override or isFree flag.
 */
public class AddonResponseDto {

    private Long id;
    private String name;
    private String description;
    private String icon;
    private BigDecimal basePrice;

    /**
     * Effective price for a specific experience attachment (null when listing
     * master records)
     */
    private BigDecimal effectivePrice;

    /** Whether this addon is free for the attached experience */
    private Boolean isFree;

    private Boolean isActive;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getEffectivePrice() {
        return effectivePrice;
    }

    public void setEffectivePrice(BigDecimal effectivePrice) {
        this.effectivePrice = effectivePrice;
    }

    public Boolean getIsFree() {
        return isFree;
    }

    public void setIsFree(Boolean isFree) {
        this.isFree = isFree;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
