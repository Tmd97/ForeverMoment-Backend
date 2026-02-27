package com.forvmom.common.dto.response;

import java.util.List;

/**
 * Response DTO for bulk media association on an Experience.
 * attached — successfully associated items
 * skipped — items that were already attached or whose mediaId was not found
 */
public class BulkAttachMediaResultDto {

    private List<ExperienceMediaResponseDto> attached;
    private List<SkippedMediaDto> skipped;

    public BulkAttachMediaResultDto(List<ExperienceMediaResponseDto> attached,
            List<SkippedMediaDto> skipped) {
        this.attached = attached;
        this.skipped = skipped;
    }

    public List<ExperienceMediaResponseDto> getAttached() {
        return attached;
    }

    public List<SkippedMediaDto> getSkipped() {
        return skipped;
    }

    // ── Skipped item ──────────────────────────────────────────────────────────

    public static class SkippedMediaDto {

        public enum Reason {
            DUPLICATE, NOT_FOUND
        }

        private Long mediaId;
        private Reason reason;
        private String message;

        public SkippedMediaDto(Long mediaId, Reason reason, String message) {
            this.mediaId = mediaId;
            this.reason = reason;
            this.message = message;
        }

        public Long getMediaId() {
            return mediaId;
        }

        public Reason getReason() {
            return reason;
        }

        public String getMessage() {
            return message;
        }
    }
}
