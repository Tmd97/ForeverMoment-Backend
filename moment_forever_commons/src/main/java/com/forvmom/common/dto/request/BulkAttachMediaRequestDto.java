package com.forvmom.common.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Request body for bulk-attaching multiple images to an experience in one call.
 * POST /admin/experiences/{experienceId}/media/bulk-attach
 *
 * Already-attached or not-found mediaIds are reported in the response's
 * "skipped" list — they do NOT fail the whole operation.
 */
public class BulkAttachMediaRequestDto {

    @NotEmpty(message = "At least one media item is required")
    @Valid
    private List<MediaAttachItem> items;

    public List<MediaAttachItem> getItems() { return items; }
    public void setItems(List<MediaAttachItem> items) { this.items = items; }

    // ── Inner item ────────────────────────────────────────────────────────────

    /** Each item = one mediaId + optional junction-row fields */
    public static class MediaAttachItem extends ExperienceMediaAttachRequestDto {

        private Long mediaId;

        public Long getMediaId() { return mediaId; }
        public void setMediaId(Long mediaId) { this.mediaId = mediaId; }
    }
}
