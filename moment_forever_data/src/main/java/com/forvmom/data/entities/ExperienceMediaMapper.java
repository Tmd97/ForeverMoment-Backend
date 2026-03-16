package com.forvmom.data.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.Date;

/**
 * Junction table: links a reusable Media record to an Experience.
 *
 * One Experience → many ExperienceMediaMapper rows
 * One Media → many ExperienceMediaMapper rows (same image on multiple
 * experiences)
 *
 * Extra columns on the junction row:
 * displayOrder — sort order within the gallery of that experience
 * isPrimary — cover/hero image of the experience
 * altText — per-experience alt text (overrides Media.altText if set)
 * isActive — can hide without deleting
 */
@Entity
@Table(name = "experience_media_mappers")
@SQLDelete(sql = "UPDATE experience_media_mappers SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class ExperienceMediaMapper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experience_id", nullable = false)
    private Experience experience;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_id", nullable = false)
    private Media media;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    /** true = this is the hero/cover image of the experience */
    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;

    /**
     * Per-experience override for alt text (falls back to Media.altText when null)
     */
    @Column(name = "alt_text", length = 300)
    private String altText;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "deleted", nullable = false, columnDefinition = "boolean default false")
    private boolean deleted = false;

    @CreationTimestamp
    @Column(name = "created_on", updatable = false)
    private Date createdOn;

    // ── Getters & Setters ─────────────────────────────────────────────────────

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

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
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
}
