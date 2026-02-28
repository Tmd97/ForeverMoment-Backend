package com.forvmom.core.dto.snapshot;

import java.math.BigDecimal;

public class LocationSnapshot {
    private Long locationId;
    private String locationName;
    private BigDecimal priceOverride;

    public LocationSnapshot() {
    }

    public LocationSnapshot(Long locationId, String locationName, BigDecimal priceOverride) {
        this.locationId = locationId;
        this.locationName = locationName;
        this.priceOverride = priceOverride;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public BigDecimal getPriceOverride() {
        return priceOverride;
    }

    public void setPriceOverride(BigDecimal priceOverride) {
        this.priceOverride = priceOverride;
    }
}
