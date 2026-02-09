package com.example.moment_forever.core.mapper;

import com.example.moment_forever.common.dto.response.AppUserResponseDto;
import com.example.moment_forever.common.dto.request.UserProfileRequestDto;
import com.example.moment_forever.data.entities.ApplicationUser;

public class ApplicationUserBeanMapper {

    public static void mapDtoToEntity(UserProfileRequestDto dto, ApplicationUser entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setFullName(dto.getFullName());
        entity.setEmail(dto.getEmail());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setProfilePictureUrl(dto.getProfilePictureUrl());
        entity.setDateOfBirth(dto.getDateOfBirth());
        entity.setPreferredCity(dto.getPreferredCity());
    }

    public static AppUserResponseDto mapEntityToDto(ApplicationUser entity) {
        if (entity == null) {
            return null;
        }
        AppUserResponseDto dto = new AppUserResponseDto();
        dto.setId(entity.getId());
        dto.setFullName(entity.getFullName());
        dto.setEmail(entity.getEmail());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setProfilePictureUrl(entity.getProfilePictureUrl());
        dto.setDateOfBirth(entity.getDateOfBirth());
        dto.setPreferredCity(entity.getPreferredCity());
        return dto;
    }
}