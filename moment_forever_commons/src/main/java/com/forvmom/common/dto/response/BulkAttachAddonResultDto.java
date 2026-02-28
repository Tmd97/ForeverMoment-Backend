package com.forvmom.common.dto.response;

import java.util.List;

/**
 * Response DTO for the bulk-attach addons endpoint.
 *
 * <p>
 * POST /api/admin/experiences/{experienceId}/addons/bulk-attach
 *
 * <p>
 * Contains two lists:
 * <ul>
 * <li>{@code attached} — successfully created {@code ExperienceAddonMapper}
 * records</li>
 * <li>{@code skipped} — addon IDs that were already attached (DUPLICATE) or
 * not found (NOT_FOUND)</li>
 * </ul>
 */
public class BulkAttachAddonResultDto {

    private List<ExperienceAddonResponseDto> attached;
    private List<SkippedAddonDto> skipped;

    public BulkAttachAddonResultDto(List<ExperienceAddonResponseDto> attached,
            List<SkippedAddonDto> skipped) {
        this.attached = attached;
        this.skipped = skipped;
    }

    public List<ExperienceAddonResponseDto> getAttached() {
        return attached;
    }

    public void setAttached(List<ExperienceAddonResponseDto> attached) {
        this.attached = attached;
    }

    public List<SkippedAddonDto> getSkipped() {
        return skipped;
    }

    public void setSkipped(List<SkippedAddonDto> skipped) {
        this.skipped = skipped;
    }

    // ── Inner class ───────────────────────────────────────────────────────────

    public static class SkippedAddonDto {

        public enum Reason {
            DUPLICATE, NOT_FOUND
        }

        private Long addonId;
        private Reason reason;
        private String message;

        public SkippedAddonDto(Long addonId, Reason reason, String message) {
            this.addonId = addonId;
            this.reason = reason;
            this.message = message;
        }

        public Long getAddonId() {
            return addonId;
        }

        public void setAddonId(Long addonId) {
            this.addonId = addonId;
        }

        public Reason getReason() {
            return reason;
        }

        public void setReason(Reason reason) {
            this.reason = reason;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
