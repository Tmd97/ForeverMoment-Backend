package com.forvmom.data.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;

@Entity
@Table(name = "experience")
@SQLDelete(sql = "UPDATE experience SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Experience extends NamedEntity {

    @Column(name = "slug", nullable = false, unique = true, length = 220)
    private String slug;

    @Column(name = "tag_name", length = 100)
    private String tagName;

    @Column(name = "base_price", precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "is_featured")
    private Boolean isFeatured = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_category_id", nullable = false)
    private SubCategory subCategory;

    @OneToOne(mappedBy = "experience", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private ExperienceDetail detail;

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public SubCategory getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(SubCategory subCategory) {
        this.subCategory = subCategory;
    }

    public ExperienceDetail getDetail() {
        return detail;
    }

    public void setDetail(ExperienceDetail detail) {
        this.detail = detail;
        if (detail != null)
            detail.setExperience(this);
    }
}