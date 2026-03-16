package com.forvmom.data.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Junction table: links a master Addon to an Experience.
 * price_override — null means use Addon.basePrice.
 * is_free — overrides both basePrice and price_override to make it free.
 */
@Entity
@Table(name = "experience_addon_mappers")
@SQLDelete(sql = "UPDATE experience_addon_mappers SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class ExperienceAddonMapper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experience_id", nullable = false)
    private Experience experience;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "addon_id", nullable = false)
    private Addon addon;

    /** null = use Addon.basePrice; non-null = per-experience price */
    @Column(name = "price_override", precision = 10, scale = 2)
    private BigDecimal priceOverride;

    /** When true, addon is complimentary regardless of price fields */
    @Column(name = "is_free", nullable = false)
    private Boolean isFree = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "deleted", nullable = false, columnDefinition = "boolean default false")
    private boolean deleted = false;

    @CreationTimestamp
    @Column(name = "created_on", updatable = false)
    private Date createdOn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Experience getExperience() {
        return experience;
    }

    public void setExperience(Experience experience) {
        this.experience = experience;
    }

    public Addon getAddon() {
        return addon;
    }

    public void setAddon(Addon addon) {
        this.addon = addon;
    }

    public BigDecimal getPriceOverride() {
        return priceOverride;
    }

    public void setPriceOverride(BigDecimal priceOverride) {
        this.priceOverride = priceOverride;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    /** Effective price: free → 0, override set → override, else addon base */
    public BigDecimal effectivePrice() {
        if (Boolean.TRUE.equals(isFree))
            return BigDecimal.ZERO;
        return priceOverride != null ? priceOverride : (addon != null ? addon.getBasePrice() : BigDecimal.ZERO);
    }
}
