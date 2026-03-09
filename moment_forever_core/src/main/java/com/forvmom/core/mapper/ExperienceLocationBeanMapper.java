package com.forvmom.core.mapper;

import com.forvmom.common.dto.response.ExperienceLocationMapperDto;
import com.forvmom.common.dto.response.ExperienceLocationResponseDto;
import com.forvmom.common.dto.response.ExperienceMediaResponseDto;
import com.forvmom.core.config.ImageUrlConfig;
import com.forvmom.data.entities.ExperienceLocationMapper;
import com.forvmom.data.entities.ExperienceMediaMapper;

public class ExperienceLocationBeanMapper {
    public static ExperienceLocationMapperDto mapEntityToDto(ExperienceLocationMapper entity) {
        if (entity == null) {
            return null;
        }
        ExperienceLocationMapperDto dto = new ExperienceLocationMapperDto();
        dto.setId(entity.getId());
        dto.setExperienceId(entity.getExperience().getId());
        dto.setLocationId(entity.getLocation().getId());
        dto.setPriceOverride(entity.getPriceOverride());
        dto.setIsActive(entity.getIsActive());
        dto.setValidFrom(entity.getValidFrom());
        dto.setValidTo(entity.getValidTo());
        return dto;
    }
}
