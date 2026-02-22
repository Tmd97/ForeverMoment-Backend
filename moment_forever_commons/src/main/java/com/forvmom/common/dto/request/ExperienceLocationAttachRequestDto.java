package com.forvmom.common.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for attaching a Location to an Experience.
 * All fields are optional — null priceOverride means "use
 * Experience.basePrice".
 */
public class ExperienceLocationAttachRequestDto {

    /**
     * Optional price override at the location level. null = fall back to
     * Experience.basePrice
     */
    private BigDecimal priceOverride;

    private LocalDate validFrom;
    private LocalDate validTo;

    private Boolean isActive = true;

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public BigDecimal getPriceOverride() {
        return priceOverride;
    }

    public void setPriceOverride(BigDecimal priceOverride) {
        this.priceOverride = priceOverride;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
