package com.example.moment_forever.core.mapper;

import com.example.moment_forever.core.dto.ApplicationUserDto;
import com.example.moment_forever.data.entities.ApplicationUser;

public class ApplicationUserBeanMapper {

    // 1️⃣ Map DTO -> Entity
    public static void mapDtoToEntity(ApplicationUserDto dto, ApplicationUser entity) {
        if (dto == null || entity == null) {
            return;
        }

//        entity.setAuthUserId(dto.getAuthUserId());
        entity.setFullName(dto.getFullName());
        entity.setEmail(dto.getEmail());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setProfilePictureUrl(dto.getProfilePictureUrl());
        entity.setDateOfBirth(dto.getDateOfBirth());
        entity.setPreferredCity(dto.getPreferredCity());

        // Do not set ID, createdAt, updatedAt manually unless needed
    }

    // 2️⃣ Map Entity -> DTO
    public static ApplicationUserDto mapEntityToDto(ApplicationUser entity) {
        if (entity == null) {
            return null;
        }

        ApplicationUserDto dto = new ApplicationUserDto();
//        dto.setId(entity.getId());
//        dto.setAuthUserId(entity.getAuthUserId());
        dto.setFullName(entity.getFullName());
        dto.setEmail(entity.getEmail());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setProfilePictureUrl(entity.getProfilePictureUrl());
        dto.setDateOfBirth(entity.getDateOfBirth());
        dto.setPreferredCity(entity.getPreferredCity());
//        dto.setCreatedAt(entity.getCreatedAt());
//        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }
}