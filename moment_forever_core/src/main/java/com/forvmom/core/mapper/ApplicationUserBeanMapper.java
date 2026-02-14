package com.forvmom.core.mapper;

import com.forvmom.common.dto.response.AdminAppUserResponseDto;
import com.forvmom.common.dto.response.AppUserResponseDto;
import com.forvmom.common.dto.request.UserProfileRequestDto;
import com.forvmom.data.entities.ApplicationUser;

import java.util.List;

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

    public static AdminAppUserResponseDto mapEntityToAdminDto(
            ApplicationUser entity) {
        if (entity == null) {
            return null;
        }
        AdminAppUserResponseDto dto = new AdminAppUserResponseDto();
        // Map basic fields
        dto.setId(entity.getId());
        dto.setFullName(entity.getFullName());
        dto.setEmail(entity.getEmail());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setProfilePictureUrl(entity.getProfilePictureUrl());
        dto.setDateOfBirth(entity.getDateOfBirth());
        dto.setPreferredCity(entity.getPreferredCity());

        // Map Admin specific fields
        if (entity.getAuthUser() != null) {
            dto.setAuthUserId(entity.getAuthUser().getId());
            dto.setUsername(entity.getAuthUser().getUsername());
            dto.setEnabled(entity.getAuthUser().isEnabled());
            dto.setAccountNonLocked(entity.getAuthUser().isAccountNonLocked());
            dto.setAccountNonExpired(entity.getAuthUser().isAccountNonExpired());
            dto.setCredentialsNonExpired(entity.getAuthUser().isCredentialsNonExpired());
            dto.setExternalUserId(entity.getAuthUser().getExternalUserId());
            dto.setAuthCreatedAt(entity.getAuthUser().getCreatedAt());
            dto.setAuthLastLogin(entity.getAuthUser().getLastLogin());

            if (entity.getAuthUser().getUserRoles() != null) {
                List<String> roles = entity.getAuthUser().getUserRoles().stream()
                        .map(authUserRole -> authUserRole.getRole().getName())
                        .collect(java.util.stream.Collectors.toList());
                dto.setRoles(roles);
            }
        }
        return dto;
    }
}