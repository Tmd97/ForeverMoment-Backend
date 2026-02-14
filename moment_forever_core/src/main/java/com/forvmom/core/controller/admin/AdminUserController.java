package com.forvmom.core.controller.admin;

import com.forvmom.common.dto.response.AdminAppUserResponseDto;
import com.forvmom.common.dto.response.RoleResponseDto;
import com.forvmom.common.response.ApiResponse;
import com.forvmom.common.response.ResponseUtil;
import com.forvmom.common.utils.AppConstants;
import com.forvmom.common.dto.request.UserProfileRequestDto;
import com.forvmom.core.services.AdminUserService;
import com.forvmom.data.entities.auth.Role;
import com.forvmom.security.dto.RegisterRequestDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/admin/user")
@Tag(name = "Admin User API", description = "Endpoints for managing users (Admin/SuperAdmin only)")
public class AdminUserController {
        @Autowired
        private AdminUserService appUserService;

        @PostMapping("/create")
        @PreAuthorize("hasRole('SUPER_ADMIN')")
        @Operation(summary = "Create User (Super Admin)", description = "Create a new user account (Requires Super Admin role)")
        public ResponseEntity<ApiResponse<?>> createUser(
                        @Valid @RequestBody RegisterRequestDto request) {
                AdminAppUserResponseDto response = appUserService
                                .createUser(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(
                                ResponseUtil.buildCreatedResponse(response,
                                                "User created successfully by Super Admin"));
        }

        @GetMapping("/profile/{userId}")
        @Operation(summary = "Get User Profile", description = "Fetch a user's profile by ID")
        public ResponseEntity<ApiResponse<?>> getUserProfile(@PathVariable Long userId) {
                AdminAppUserResponseDto appUserResponseDto = appUserService
                                .getAppUserById(userId);
                return ResponseEntity.ok(
                                ResponseUtil.buildOkResponse(appUserResponseDto, AppConstants.MSG_FETCHED));
        }

        // TODO: currently this is for fetching user profile by any unique fields
        @PostMapping("/profile")
        public ResponseEntity<ApiResponse<?>> getUserProfile(
                        @Valid @RequestBody UserProfileRequestDto userProfileRequestDto) {
                AdminAppUserResponseDto res = appUserService
                                .getAppUserByEmailId(userProfileRequestDto.getEmail());
                return ResponseEntity.ok(
                                ResponseUtil.buildOkResponse(res, AppConstants.MSG_FETCHED));
        }

        @PutMapping("/profile/{userId}")
        public ResponseEntity<ApiResponse<?>> updateUserProfile(
                        @PathVariable Long userId,
                        @Valid @RequestBody UserProfileRequestDto userDto) {
                AdminAppUserResponseDto updatedUser = appUserService
                                .updateAppUser(userId, userDto);
                return ResponseEntity.ok(
                                ResponseUtil.buildCreatedResponse(updatedUser, "User profile updated"));
        }

        @GetMapping("/profiles")
        @Operation(summary = "Get All User Profiles", description = "Fetch all user profiles")
        public ResponseEntity<ApiResponse<?>> getUserProfiles() {
                List<AdminAppUserResponseDto> appUserResponseDto = appUserService
                                .getAllAppUser();
                return ResponseEntity.ok(
                                ResponseUtil.buildOkResponse(appUserResponseDto, AppConstants.MSG_FETCHED));
        }

        @DeleteMapping("/profile/{userId}")
        public ResponseEntity<ApiResponse<?>> deleteProfile(@PathVariable Long userId) {
                appUserService.deleteUserProfile(userId);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ResponseUtil.buildCreatedResponse(null, "User Profile deleted successfully"));
        }

        @PostMapping("/deleteAccount")
        public ResponseEntity<ApiResponse<?>> deleteAccount(Long userId) {
                appUserService.deleteAccount(userId);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ResponseUtil.buildOkResponse(null, "User Profile deleted successfully"));
        }

        @GetMapping("/{userId}/roles")
        @Operation(summary = "Get User Roles", description = "Fetch all roles assigned to a user")
        public ResponseEntity<ApiResponse<?>> getUserRoles(@PathVariable Long userId) {
                List<RoleResponseDto> roleResponseDtos = appUserService.getUserRoles(userId);
                return ResponseEntity.ok(
                                ResponseUtil.buildOkResponse(roleResponseDtos, "User roles fetched successfully"));
        }

}
