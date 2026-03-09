package com.forvmom.core.controller.pub;

import com.forvmom.common.dto.request.CategoryByLocationDto;
import com.forvmom.common.dto.response.LocationResponseDto;
import com.forvmom.common.dto.response.PincodeResponseDto;
import com.forvmom.common.dto.response.SubCategoryByLocationDto;
import com.forvmom.common.response.ApiResponse;
import com.forvmom.common.response.ResponseUtil;
import com.forvmom.common.utils.AppConstants;
import com.forvmom.core.services.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/locations")
@Tag(name = "Public Location API", description = "Endpoints for browsing locations and checking pincodes")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @GetMapping
    @Operation(summary = "Get All Active Locations", description = "Fetch all active serviceable locations")
    public ResponseEntity<ApiResponse<?>> getAllLocations() {
        List<LocationResponseDto> response = locationService.getAllActive();
        return ResponseEntity.ok(ResponseUtil.buildOkResponse(response, AppConstants.MSG_FETCHED));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Location by ID", description = "Fetch a location with its pincodes")
    public ResponseEntity<ApiResponse<?>> getLocationById(@PathVariable Long id) {
        LocationResponseDto response = locationService.getById(id);
        return ResponseEntity.ok(ResponseUtil.buildOkResponse(response, AppConstants.MSG_FETCHED));
    }

    @GetMapping("/{locationId}/pincodes")
    @Operation(summary = "Get Pincodes by Location", description = "List all serviceable pincodes under a location")
    public ResponseEntity<ApiResponse<?>> getPincodesByLocation(@PathVariable Long locationId) {
        List<PincodeResponseDto> response = locationService.getPincodesByLocation(locationId);
        return ResponseEntity.ok(ResponseUtil.buildOkResponse(response, AppConstants.MSG_FETCHED));
    }

    @GetMapping("/check-pincode")
    @Operation(summary = "Check Pincode Serviceability", description = "Check if a pincode is serviceable and return its location info")
    public ResponseEntity<ApiResponse<?>> checkPincode(@RequestParam String pincode) {
        PincodeResponseDto response = locationService.checkPincode(pincode);
        return ResponseEntity.ok(ResponseUtil.buildOkResponse(response, AppConstants.MSG_FETCHED));
    }

    @GetMapping("/{locationId}/subcategories")
    @Operation(summary = "Get active subcategories for a location", description = "Optionally filter by categoryId")
    public ResponseEntity<ApiResponse<List<SubCategoryByLocationDto>>> getSubCategoriesByLocation(
            @PathVariable Long locationId,
            @RequestParam(required = false) Long categoryId) {
        List<SubCategoryByLocationDto> list;
        if (categoryId != null) {
            list = locationService.getActiveSubCategoriesByLocationAndCategory(locationId, categoryId);
        } else {
            list = locationService.getActiveSubCategoriesByLocation(locationId);
        }
        return ResponseEntity.ok(ResponseUtil.buildOkResponse(list, AppConstants.MSG_FETCHED));
    }

    @GetMapping("/{locationId}/categories")
    @Operation(summary = "Get active categories for a location")
    public ResponseEntity<ApiResponse<List<CategoryByLocationDto>>> getCategoriesByLocation(
            @PathVariable Long locationId) {
        List<CategoryByLocationDto> list = locationService.getActiveCategoriesByLocation(locationId);
        return ResponseEntity.ok(ResponseUtil.buildOkResponse(list, AppConstants.MSG_FETCHED));
    }
}
