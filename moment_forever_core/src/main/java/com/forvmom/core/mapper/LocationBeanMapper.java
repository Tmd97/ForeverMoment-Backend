package com.forvmom.core.mapper;

import com.forvmom.common.dto.request.LocationRequestDto;
import com.forvmom.common.dto.response.LocationResponseDto;
import com.forvmom.common.dto.response.PincodeResponseDto;
import com.forvmom.data.entities.Location;
import com.forvmom.data.entities.Pincode;

import java.util.List;
import java.util.stream.Collectors;

public class LocationBeanMapper {

    public static void mapDtoToEntity(LocationRequestDto dto, Location entity) {
        if (dto == null || entity == null)
            return;

        entity.setName(dto.getName());
        entity.setCity(dto.getCity());
        entity.setState(dto.getState());
        entity.setCountry(dto.getCountry() != null ? dto.getCountry() : "India");
        entity.setAddress(dto.getAddress());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        if (dto.getIsActive() != null) {
            entity.setActive(dto.getIsActive());
        }
    }

    public static LocationResponseDto mapEntityToDto(Location entity) {
        if (entity == null)
            return null;

        LocationResponseDto dto = new LocationResponseDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        dto.setCountry(entity.getCountry());
        dto.setAddress(entity.getAddress());
        dto.setLatitude(entity.getLatitude());
        dto.setLongitude(entity.getLongitude());
        dto.setIsActive(entity.isActive());
        dto.setCreatedOn(entity.getCreatedOn());
        dto.setUpdatedOn(entity.getUpdatedOn());

        if (entity.getPincodes() != null && !entity.getPincodes().isEmpty()) {
            List<PincodeResponseDto> pincodeDtos = entity.getPincodes().stream()
                    .map(p -> mapPincodeToDto(p, false))
                    .collect(Collectors.toList());
            dto.setPincodes(pincodeDtos);
        }

        return dto;
    }

    // Shallow mapping — without embedding location info (used when Location is
    // already context)
    public static PincodeResponseDto mapPincodeToDto(Pincode pincode, boolean includeLocation) {
        if (pincode == null)
            return null;

        PincodeResponseDto dto = new PincodeResponseDto();
        dto.setId(pincode.getId());
        dto.setName(pincode.getName());
        dto.setPincodeCode(pincode.getPincodeCode());
        dto.setAreaName(pincode.getAreaName());
        dto.setLatitude(pincode.getLatitude());
        dto.setLongitude(pincode.getLongitude());
        dto.setIsActive(pincode.isActive());
        dto.setCreatedOn(pincode.getCreatedOn());

        if (includeLocation && pincode.getLocation() != null) {
            dto.setLocationId(pincode.getLocation().getId());
            dto.setLocationName(pincode.getLocation().getName());
            dto.setLocationCity(pincode.getLocation().getCity());
        }

        return dto;
    }
}
