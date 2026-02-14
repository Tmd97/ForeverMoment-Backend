package com.example.moment_forever.core.controller.admin;

import com.example.moment_forever.common.dto.response.AdminAppUserResponseDto;
import com.example.moment_forever.common.dto.response.RoleResponseDto;
import com.example.moment_forever.common.response.ApiResponse;
import com.example.moment_forever.common.response.ResponseUtil;
import com.example.moment_forever.common.utils.AppConstants;
import com.example.moment_forever.common.dto.request.UserProfileRequestDto;
import com.example.moment_forever.core.services.AdminUserService;
import com.example.moment_forever.data.entities.auth.Role;
import com.example.moment_forever.security.dto.RegisterRequestDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/user")
public class AdminUserController {
    @Autowired
    private AdminUserService appUserService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<?>> createUser(
            @Valid @RequestBody RegisterRequestDto request) {
        AdminAppUserResponseDto response = appUserService
                .createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseUtil.buildCreatedResponse(response, "User created successfully by Super Admin"));
    }

    @GetMapping("/profile/{userId}")
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
    public ResponseEntity<ApiResponse<?>> getUserRoles(@PathVariable Long userId) {
        List<RoleResponseDto> roleResponseDtos = appUserService.getUserRoles(userId);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(roleResponseDtos, "User roles fetched successfully"));
    }

}
