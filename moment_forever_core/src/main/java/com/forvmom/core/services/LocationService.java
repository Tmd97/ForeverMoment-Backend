package com.forvmom.core.services;

import com.forvmom.common.dto.request.LocationRequestDto;
import com.forvmom.common.dto.request.PincodeRequestDto;
import com.forvmom.common.dto.response.LocationResponseDto;
import com.forvmom.common.dto.response.PincodeResponseDto;

import java.util.List;

public interface LocationService {

    LocationResponseDto createLocation(LocationRequestDto requestDto);

    LocationResponseDto updateLocation(Long id, LocationRequestDto requestDto);

    LocationResponseDto getById(Long id);

    List<LocationResponseDto> getAll();

    List<LocationResponseDto> getAllActive();

    List<LocationResponseDto> getByCity(String city);

    boolean deleteLocation(Long id);

    void toggleActive(Long id);

    // Pincode operations
    PincodeResponseDto addPincode(PincodeRequestDto requestDto);

    PincodeResponseDto updatePincode(Long pincodeId, PincodeRequestDto requestDto);

    List<PincodeResponseDto> getPincodesByLocation(Long locationId);

    PincodeResponseDto checkPincode(String pincodeCode);

    boolean deletePincode(Long pincodeId);
}
