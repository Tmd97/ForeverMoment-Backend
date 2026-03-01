package com.forvmom.common.dto.snapshot;

import java.math.BigDecimal;

public class AddonSnapshot {
    private Long addonMapperId;
    private String addonName;
    private BigDecimal effectivePrice;
    private boolean free;

    public AddonSnapshot() {
    }

    public AddonSnapshot(Long addonMapperId, String addonName,
            BigDecimal effectivePrice, boolean free) {
        this.addonMapperId = addonMapperId;
        this.addonName = addonName;
        this.effectivePrice = effectivePrice;
        this.free = free;
    }

    public Long getAddonMapperId() {
        return addonMapperId;
    }

    public void setAddonMapperId(Long addonMapperId) {
        this.addonMapperId = addonMapperId;
    }

    public String getAddonName() {
        return addonName;
    }

    public void setAddonName(String addonName) {
        this.addonName = addonName;
    }

    public BigDecimal getEffectivePrice() {
        return effectivePrice;
    }

    public void setEffectivePrice(BigDecimal effectivePrice) {
        this.effectivePrice = effectivePrice;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }
}
