package com.forvmom.common.dto.snapshot;

import java.math.BigDecimal;

public class ExperienceSnapshot {
    private Long id;
    private String name;
    private String slug;
    private BigDecimal basePrice;

    public ExperienceSnapshot() {
    }

    public ExperienceSnapshot(Long id, String name, String slug, BigDecimal basePrice) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.basePrice = basePrice;
    }

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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }
}
