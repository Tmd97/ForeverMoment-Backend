package com.forvmom.core.dto.snapshot;

import java.math.BigDecimal;

public class SlotSnapshot {
    private Long slotMapperId;
    private Long experienceId;
    private Long locationId;
    private Long timeSlotId;
    private String label;
    private String startTime;
    private String endTime;
    private BigDecimal priceOverride;
    private Integer maxCapacity;

    public SlotSnapshot() {
    }

    public SlotSnapshot(Long slotMapperId, Long experienceId, Long locationId, Long timeSlotId, String label,
            String startTime, String endTime,
            BigDecimal priceOverride, Integer maxCapacity) {
        this.slotMapperId = slotMapperId;
        this.experienceId = experienceId;
        this.locationId = locationId;
        this.timeSlotId = timeSlotId;
        this.label = label;
        this.startTime = startTime;
        this.endTime = endTime;
        this.priceOverride = priceOverride;
        this.maxCapacity = maxCapacity;
    }

    public Long getSlotMapperId() {
        return slotMapperId;
    }

    public void setSlotMapperId(Long slotMapperId) {
        this.slotMapperId = slotMapperId;
    }

    public Long getExperienceId() {
        return experienceId;
    }

    public void setExperienceId(Long experienceId) {
        this.experienceId = experienceId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Long getTimeSlotId() {
        return timeSlotId;
    }

    public void setTimeSlotId(Long timeSlotId) {
        this.timeSlotId = timeSlotId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

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
}
