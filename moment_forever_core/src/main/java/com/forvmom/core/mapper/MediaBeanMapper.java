package com.forvmom.core.mapper;

import com.forvmom.core.config.ImageUrlConfig;
import com.forvmom.data.entities.Media;
import com.forvmom.store.dto.ImageResponse;

/**
 * Stateless mapper for Media entity ↔ ImageResponse DTO.
 *
 * Two variants:
 * mapEntityToDto(entity) — no URL population (e.g. internal use)
 * mapEntityToDto(entity, urlConfig) — populates url + thumbnailUrl via
 * ImageUrlConfig.buildPublicUrl(storageFileName).
 * The storageFileName already contains the
 * upload timestamp, so the URL is inherently
 * cache-busted on every re-upload.
 */
public class MediaBeanMapper {

    private MediaBeanMapper() {
    }

    // ── Base mapping (no URL) ─────────────────────────────────────────────────

    public static ImageResponse mapEntityToDto(Media entity) {
        if (entity == null)
            return null;
        ImageResponse dto = new ImageResponse();
        dto.setId(entity.getId());
        dto.setFileName(entity.getFileName());
        dto.setStorageFileName(entity.getStorageFileName());
        dto.setFilePath(entity.getFilePath());
        dto.setMimeType(entity.getMimeType());
        dto.setMediaType(entity.getMediaType());
        dto.setFileSizeBytes(entity.getFileSizeBytes());
        dto.setAltText(entity.getAltText());
        dto.setActive(entity.getIsActive());
        return dto;
    }

    // ── URL-aware mapping (used after upload / on all response APIs) ──────────

    /**
     * Maps entity to DTO and populates the public-facing URL fields using
     * ImageUrlConfig. storageFileName = "originalName_<timestamp>", so the
     * resulting URL changes on every re-upload → automatic cache busting.
     */
    public static ImageResponse mapEntityToDto(Media entity, ImageUrlConfig urlConfig) {
        ImageResponse dto = mapEntityToDto(entity);
        if (dto == null || entity.getStorageFileName() == null)
            return dto;
        dto.setMediaUrl(urlConfig.buildPublicUrl(entity.getStorageFileName()));
        dto.setThumbnailUrl(urlConfig.buildThumbnailUrl(entity.getStorageFileName()));
        return dto;
    }
}
