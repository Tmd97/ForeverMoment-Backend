package com.forvmom.common.dto.request;

import jakarta.validation.constraints.Size;

public class MediaRequestDto {

    @Size(max = 300, message = "Alt text cannot exceed 300 characters")
    private String altText;

    @Size(max = 20, message = "Media type cannot exceed 20 characters")
    private String mediaType;

    private Boolean isActive;

    private Integer displayOrder;

    private Boolean isPrimary;

    private Boolean isCover;

    private Boolean isThumbnail;

    // ── Getters & Setters ──────────────────────────────────────────────────────

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    public Boolean getIsCover() {
        return isCover;
    }

    public void setIsCover(Boolean isCover) {
        this.isCover = isCover;
    }

    public Boolean getIsThumbnail() {
        return isThumbnail;
    }

    public void setIsThumbnail(Boolean isThumbnail) {
        this.isThumbnail = isThumbnail;
    }
}
