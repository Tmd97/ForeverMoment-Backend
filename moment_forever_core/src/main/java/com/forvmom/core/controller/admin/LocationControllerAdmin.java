package com.forvmom.core.controller.admin;

import com.forvmom.common.dto.request.ExperienceLocationAttachRequestDto;
import com.forvmom.common.dto.request.LocationRequestDto;
import com.forvmom.common.dto.request.PincodeRequestDto;
import com.forvmom.common.dto.request.ReorderRequestDto;
import com.forvmom.common.dto.response.ExperienceLocationResponseDto;
import com.forvmom.common.dto.response.LocationResponseDto;
import com.forvmom.common.dto.response.PincodeResponseDto;
import com.forvmom.common.response.ApiResponse;
import com.forvmom.common.response.ResponseUtil;
import com.forvmom.common.utils.AppConstants;
import com.forvmom.core.services.LocationService;
import com.forvmom.core.services.ReorderingService;
import com.forvmom.data.entities.Location;
import com.forvmom.data.entities.TimeSlot;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/locations")
@Tag(name = "Admin Location API", description = "Endpoints for managing locations and pincodes (Admin only)")
public class LocationControllerAdmin {

    @Autowired
    private ReorderingService reorderingService;

    @Autowired
    private LocationService locationService;

    @PostMapping
    @Operation(summary = "Create Location", description = "Create a new serviceable location")
    public ResponseEntity<ApiResponse<?>> createLocation(@RequestBody LocationRequestDto requestDto) {
        LocationResponseDto response = locationService.createLocation(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.buildCreatedResponse(response, AppConstants.MSG_CREATED));
    }

    // TODO: can use flag from UI for including nested object or not(like pincodes)
    @GetMapping
    @Operation(summary = "Get All Locations", description = "Fetch all locations (including inactive)")
    public ResponseEntity<ApiResponse<?>> getAllLocations() {
        List<LocationResponseDto> response = locationService.getAll();
        return ResponseEntity.ok(ResponseUtil.buildOkResponse(response, AppConstants.MSG_FETCHED));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Location by ID", description = "Fetch a location with its pincodes")
    public ResponseEntity<ApiResponse<?>> getLocationById(@PathVariable Long id) {
        LocationResponseDto response = locationService.getById(id);
        return ResponseEntity.ok(ResponseUtil.buildOkResponse(response, AppConstants.MSG_FETCHED));
    }

    @GetMapping("/city/{city}")
    @Operation(summary = "Get Locations by City", description = "Fetch all locations in a city")
    public ResponseEntity<ApiResponse<?>> getLocationsByCity(@PathVariable String city) {
        List<LocationResponseDto> response = locationService.getByCity(city);
        return ResponseEntity.ok(ResponseUtil.buildOkResponse(response, AppConstants.MSG_FETCHED));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Location", description = "Update an existing location")
    public ResponseEntity<ApiResponse<?>> updateLocation(@PathVariable Long id,
            @RequestBody LocationRequestDto requestDto) {
        LocationResponseDto response = locationService.updateLocation(id, requestDto);
        return ResponseEntity.ok(ResponseUtil.buildOkResponse(response, AppConstants.MSG_UPDATED));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Location", description = "Soft delete a location")
    public ResponseEntity<ApiResponse<?>> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.ok(ResponseUtil.buildOkResponse(null, AppConstants.MSG_DELETED));
    }

    @PatchMapping("/{id}/toggle")
    @Operation(summary = "Toggle Location Active", description = "Toggle is_active for a location")
    public ResponseEntity<ApiResponse<?>> toggleLocation(@PathVariable Long id) {
        locationService.toggleActive(id);
        return ResponseEntity.ok(ResponseUtil.buildOkResponse(null, "Location status toggled successfully"));
    }

    @PostMapping("/pincodes")
    @Operation(summary = "Add Pincode", description = "Add a pincode to a location")
    public ResponseEntity<ApiResponse<?>> addPincode(@RequestBody PincodeRequestDto requestDto) {
        PincodeResponseDto response = locationService.addPincode(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.buildCreatedResponse(response, AppConstants.MSG_CREATED));
    }

    @GetMapping("/{locationId}/pincodes")
    @Operation(summary = "Get Pincodes by Location", description = "List all pincodes under a location")
    public ResponseEntity<ApiResponse<?>> getPincodesByLocation(@PathVariable Long locationId) {
        List<PincodeResponseDto> response = locationService.getPincodesByLocation(locationId);
        return ResponseEntity.ok(ResponseUtil.buildOkResponse(response, AppConstants.MSG_FETCHED));
    }

    @PutMapping("/pincodes/{pincodeId}")
    @Operation(summary = "Update Pincode", description = "Update a pincode's details")
    public ResponseEntity<ApiResponse<?>> updatePincode(@PathVariable Long pincodeId,
            @RequestBody PincodeRequestDto requestDto) {
        PincodeResponseDto response = locationService.updatePincode(pincodeId, requestDto);
        return ResponseEntity.ok(ResponseUtil.buildOkResponse(response, AppConstants.MSG_UPDATED));
    }

    @DeleteMapping("/pincodes/{pincodeId}")
    @Operation(summary = "Delete Pincode", description = "Soft delete a pincode")
    public ResponseEntity<ApiResponse<?>> deletePincode(@PathVariable Long pincodeId) {
        locationService.deletePincode(pincodeId);
        return ResponseEntity.ok(ResponseUtil.buildOkResponse(null, AppConstants.MSG_DELETED));
    }

    // ── Experience Association ────────────────────────────────────────────────

    @GetMapping("/{locationId}/experiences")
    @Operation(summary = "Get Experiences for Location", description = "Lists all experiences this location is attached to")
    public ResponseEntity<ApiResponse<?>> getExperiencesForLocation(
            @PathVariable Long locationId) {
        List<ExperienceLocationResponseDto> response = locationService.getExperiencesForLocation(locationId);
        return ResponseEntity.ok(ResponseUtil.buildOkResponse(response, AppConstants.MSG_FETCHED));
    }

    @PostMapping("/{locationId}/experiences/{experienceId}")
    @Operation(summary = "Attach Location to Experience", description = "Creates an ExperienceLocationMapper row. "
            + "Optional body: priceOverride (null = use Experience.basePrice), validFrom, validTo")
    public ResponseEntity<ApiResponse<?>> attachToExperience(
            @PathVariable Long locationId,
            @PathVariable Long experienceId,
            @RequestBody(required = false) @Valid ExperienceLocationAttachRequestDto requestDto) {
        if (requestDto == null)
            requestDto = new ExperienceLocationAttachRequestDto();
        ExperienceLocationResponseDto response = locationService.attachToExperience(locationId, experienceId,
                requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.buildCreatedResponse(response, AppConstants.MSG_CREATED));
    }

    @PutMapping("/{locationId}/experiences/{experienceId}")
    @Operation(summary = "Update Experience Attachment", description = "Updates priceOverride, isActive, or validity dates for an existing attachment")
    public ResponseEntity<ApiResponse<?>> updateExperienceAttachment(
            @PathVariable Long locationId,
            @PathVariable Long experienceId,
            @Valid @RequestBody ExperienceLocationAttachRequestDto requestDto) {
        ExperienceLocationResponseDto response = locationService.updateExperienceAttachment(locationId, experienceId,
                requestDto);
        return ResponseEntity.ok(ResponseUtil.buildOkResponse(response, AppConstants.MSG_UPDATED));
    }

    @DeleteMapping("/{locationId}/experiences/{experienceId}")
    @Operation(summary = "Detach Location from Experience", description = "Soft-deletes the junction row (and cascades to timeslot mappings for this pair)")
    public ResponseEntity<ApiResponse<?>> detachFromExperience(
            @PathVariable Long locationId,
            @PathVariable Long experienceId) {
        locationService.detachFromExperience(locationId, experienceId);
        return ResponseEntity.ok(ResponseUtil.buildOkResponse(null, AppConstants.MSG_DELETED));
    }

    @PatchMapping("/{locationId}/experiences/{mapperId}/toggle")
    @Operation(summary = "Toggle Experience Attachment Active", description = "Toggles is_active on the ExperienceLocationMapper row by its mapperId")
    public ResponseEntity<ApiResponse<?>> toggleExperienceAttachmentActive(
            @PathVariable Long locationId,
            @PathVariable Long mapperId) {
        locationService.toggleExperienceAttachmentActive(mapperId);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(null, "Location-Experience attachment status toggled"));
    }

    @PatchMapping("/reorder")
    public ResponseEntity<ApiResponse<?>> reOrderTheItems(
            @RequestBody ReorderRequestDto reorderRequestDto) {
        reorderingService.reorderItems(reorderRequestDto.getId(), reorderRequestDto.getNewPosition(), Location.class);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(null, AppConstants.MSG_UPDATED));
    }
}
