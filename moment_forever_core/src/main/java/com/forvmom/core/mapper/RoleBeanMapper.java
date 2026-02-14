package com.forvmom.core.mapper;

import com.forvmom.common.dto.request.RoleRequestDto;
import com.forvmom.common.dto.response.RoleResponseDto;
import com.forvmom.data.entities.auth.Role;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RoleBeanMapper {

    // For CREATE operation - DTO to Entity
    public static Role mapDtoToEntity(RoleRequestDto dto) {
        if (dto == null) {
            return null;
        }

        Role role = new Role();
        role.setName(dto.getRoleName());
        role.setDescription(dto.getDescription());

        // Set permission level with default value 10 if not provided
        role.setPermissionLevel(dto.getPermissionLevel() != null ? dto.getPermissionLevel() : 10);

        // Set active status with default true if not provided
        role.setActive(dto.getActive() != null ? dto.getActive() : true);

        // Set system role flag with default false if not provided
        role.setSystemRole(dto.getSystemRole() != null ? dto.getSystemRole() : false);

        return role;
    }

    // For UPDATE operation - Update existing entity from DTO
    public static void updateEntity(Role entity, RoleRequestDto dto) {
        if (dto == null || entity == null) {
            return;
        }

        // Update allowed fields
        entity.setName(dto.getRoleName());
        entity.setDescription(dto.getDescription());

        // Update permission level if provided, otherwise keep existing
        if (dto.getPermissionLevel() != null) {
            entity.setPermissionLevel(dto.getPermissionLevel());
        }

        // Update active status if provided, otherwise keep existing
        if (dto.getActive() != null) {
            entity.setActive(dto.getActive());
        }

        // NOTE: isSystemRole is NOT updated here intentionally
        // System role flag should only be set at creation time
    }

    // For READ operation - Entity to DTO (single)
    public static RoleResponseDto mapEntityToDto(Role entity) {
        if (entity == null) {
            return null;
        }

        RoleResponseDto dto = new RoleResponseDto();
        dto.setId(entity.getId());
        dto.setRoleName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPermissionLevel(entity.getPermissionLevel());
        dto.setActive(entity.isActive());
        dto.setSystemRole(entity.isSystemRole());
        dto.setCreatedAt(entity.getCreatedAt());

        // User count is not set here - no relationship in Role entity
        // Set to null or 0 as per your response DTO
        dto.setUserCount(null); // Or dto.setUserCount(0);

        return dto;
    }

    // For READ operation - List of Entities to List of DTOs
    public static List<RoleResponseDto> toDtoList(List<Role> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }

        return entities.stream()
                .map(RoleBeanMapper::mapEntityToDto)
                .collect(Collectors.toList());
    }

    // Optional: For CREATE operation with specific permission level
    public static Role mapDtoToEntityWithPermission(RoleRequestDto dto, Integer permissionLevel) {
        Role role = mapDtoToEntity(dto);
        if (role != null && permissionLevel != null) {
            role.setPermissionLevel(permissionLevel);
        }
        return role;
    }

    // Optional: For copying properties from one entity to another
    public static void copyProperties(Role source, Role target) {
        if (source == null || target == null) {
            return;
        }

        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setPermissionLevel(source.getPermissionLevel());
        target.setActive(source.isActive());
        target.setSystemRole(source.isSystemRole());
        // Note: id, createdAt are not copied
    }
}