package com.forvmom.common.dto.request;

/**
 * Request body for attaching a Media record to an Experience.
 * POST /admin/experiences/{experienceId}/media/{mediaId}
 */
public class ExperienceMediaAttachRequestDto {

    private Integer displayOrder;

    /**
     * When true, this image becomes the hero/cover image of the experience.
     * Any existing primary image is automatically demoted.
     */
    private Boolean isPrimary = false;

    /** Per-experience alt text; falls back to Media.altText when null */
    private String altText;

    private Boolean isActive = true;

    // ── Getters & Setters ─────────────────────────────────────────────────────

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
}
