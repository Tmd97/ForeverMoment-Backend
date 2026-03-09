package com.forvmom.core.services;

import com.forvmom.common.dto.request.*;
import com.forvmom.common.dto.response.CategoryLocationResponseDto;
import com.forvmom.common.dto.response.ExperienceLocationResponseDto;
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

    // ── Experience Association ────────────────────────────────────────────────

    ExperienceLocationResponseDto attachToExperience(Long locationId, Long experienceId,
            ExperienceLocationAttachRequestDto requestDto);

    void detachFromExperience(Long locationId, Long experienceId);

    List<ExperienceLocationResponseDto> getExperiencesForLocation(Long locationId);

    ExperienceLocationResponseDto updateExperienceAttachment(Long locationId, Long experienceId,
            ExperienceLocationAttachRequestDto requestDto);

    void toggleExperienceAttachmentActive(Long mapperId);

    CategoryLocationResponseDto attachCategoryToLocation(Long locationId, Long categoryId, CategoryLocationAttachRequestDto requestDto);
    void detachCategoryFromLocation(Long locationId, Long categoryId);
    List<CategoryLocationResponseDto> getCategoriesForLocation(Long locationId);
    CategoryLocationResponseDto updateCategoryAttachment(Long locationId, Long categoryId, CategoryLocationAttachRequestDto requestDto);
    void toggleCategoryAttachmentActive(Long mapperId);
    List<CategoryByLocationDto> getActiveCategoriesByLocation(Long locationId); // public
}
