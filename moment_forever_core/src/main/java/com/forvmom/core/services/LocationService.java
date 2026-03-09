package com.forvmom.core.services;

import com.forvmom.common.dto.request.*;
import com.forvmom.common.dto.response.*;

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

    /// /////////category association with location
    CategoryLocationResponseDto attachCategoryToLocation(Long locationId, Long categoryId, CategoryLocationAttachRequestDto requestDto);
    void detachCategoryFromLocation(Long locationId, Long categoryId);
    List<CategoryLocationResponseDto> getCategoriesForLocation(Long locationId);
    CategoryLocationResponseDto updateCategoryAttachment(Long locationId, Long categoryId, CategoryLocationAttachRequestDto requestDto);
    void toggleCategoryAttachmentActive(Long mapperId);
    List<CategoryByLocationDto> getActiveCategoriesByLocation(Long locationId); // public

    /// //////////sub-category association with location
    SubCategoryLocationResponseDto attachSubCategoryToLocation(Long locationId, Long subCategoryId, SubCategoryLocationAttachRequestDto requestDto);
    void detachSubCategoryFromLocation(Long locationId, Long subCategoryId);
    List<SubCategoryLocationResponseDto> getSubCategoriesForLocation(Long locationId);
    SubCategoryLocationResponseDto updateSubCategoryAttachment(Long locationId, Long subCategoryId, SubCategoryLocationAttachRequestDto requestDto);
    void toggleSubCategoryAttachmentActive(Long mapperId);
    List<SubCategoryByLocationDto> getActiveSubCategoriesByLocation(Long locationId);
    List<SubCategoryByLocationDto> getActiveSubCategoriesByLocationAndCategory(Long locationId, Long categoryId);
}
