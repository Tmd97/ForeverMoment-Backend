package com.forvmom.common.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for attaching a TimeSlot to an ExperienceLocationMapper.
 * price_override — null = fall back to ExperienceLocationMapper.price_override
 * or Experience.base_price
 */
public class ExperienceTimeSlotAttachRequestDto {

    /**
     * null = use ExperienceLocationMapper price; non-null = timeslot-level override
     * (Level 3 pricing)
     */
    private BigDecimal priceOverride;

    private Integer maxCapacity;

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

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
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
