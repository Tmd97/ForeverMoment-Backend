package com.forvmom.core.mapper;

import com.forvmom.common.dto.request.ExperienceMediaAttachRequestDto;
import com.forvmom.common.dto.response.ExperienceMediaResponseDto;
import com.forvmom.core.config.ImageUrlConfig;
import com.forvmom.data.entities.ExperienceMediaMapper;
import com.forvmom.data.entities.Media;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Stateless mapper for ExperienceMediaMapper entity ↔ DTOs.
 * Receives ImageUrlConfig as a parameter (not Spring-managed) so URLs are
 * populated without making this class a bean.
 */
public class ExperienceMediaBeanMapper {

    private ExperienceMediaBeanMapper() {
    }

    public static ExperienceMediaResponseDto mapEntityToDto(ExperienceMediaMapper mapper,
            ImageUrlConfig urlConfig) {
        if (mapper == null)
            return null;
        ExperienceMediaResponseDto dto = new ExperienceMediaResponseDto();
        dto.setMapperId(mapper.getId());
        dto.setDisplayOrder(mapper.getDisplayOrder());
        dto.setIsPrimary(mapper.getIsPrimary());
        dto.setIsActive(mapper.getIsActive());

        Media media = mapper.getMedia();
        if (media != null) {
            dto.setMediaId(media.getId());
            dto.setFileName(media.getFileName());
            dto.setStorageFileName(media.getStorageFileName());
            dto.setMimeType(media.getMimeType());
            dto.setFileSizeBytes(media.getFileSizeBytes());

            // Effective alt text: junction override wins, falls back to master
            dto.setAltText(mapper.getAltText() != null ? mapper.getAltText() : media.getAltText());

            if (media.getStorageFileName() != null) {
                dto.setUrl(urlConfig.buildPublicUrl(media.getStorageFileName()));
                dto.setThumbnailUrl(urlConfig.buildThumbnailUrl(media.getStorageFileName()));
            }
        }
        return dto;
    }

    public static List<ExperienceMediaResponseDto> mapEntitiesToDto(List<ExperienceMediaMapper> mappers,
            ImageUrlConfig urlConfig) {
        if (mappers == null || mappers.isEmpty())
            return Collections.emptyList();
        return mappers.stream()
                .map(m -> mapEntityToDto(m, urlConfig))
                .collect(Collectors.toList());
    }

    public static ExperienceMediaMapper mapDtoToEntity(ExperienceMediaAttachRequestDto dto) {
        ExperienceMediaMapper mapper = new ExperienceMediaMapper();
        return updateEntityFromDto(mapper, dto);
    }

    public static ExperienceMediaMapper updateEntityFromDto(ExperienceMediaMapper mapper,
            ExperienceMediaAttachRequestDto dto) {
        if (dto.getDisplayOrder() != null)
            mapper.setDisplayOrder(dto.getDisplayOrder());
        if (dto.getIsPrimary() != null)
            mapper.setIsPrimary(dto.getIsPrimary());
        if (dto.getAltText() != null)
            mapper.setAltText(dto.getAltText());
        if (dto.getIsActive() != null)
            mapper.setIsActive(dto.getIsActive());
        return mapper;
    }
}
