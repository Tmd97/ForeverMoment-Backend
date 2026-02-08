package com.example.moment_forever.core.controller.admin;

import com.example.moment_forever.common.response.ApiResponse;
import com.example.moment_forever.common.response.ResponseUtil;
import com.example.moment_forever.common.utils.AppConstants;
import com.example.moment_forever.core.dto.ApplicationUserDto;
import com.example.moment_forever.core.dto.UserProfileRequestDto;
import com.example.moment_forever.core.services.AdminUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/user")
public class AdminUserController {
    @Autowired
    private AdminUserService appUserService;

    @GetMapping("/profile/{userId}")
    public ResponseEntity<ApiResponse<?>> getUserProfile(@PathVariable Long userId) {
        ApplicationUserDto applicationUserDto = appUserService.getAppUserById(userId);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(applicationUserDto, AppConstants.MSG_FETCHED)
        );
    }

    //TODO: currently this is for fetching user profile by any unique fields
    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<?>> getUserProfile(@Valid @RequestBody UserProfileRequestDto userProfileRequestDto) {
        ApplicationUserDto res = appUserService.getAppUserByEmailId(userProfileRequestDto.getEmail());
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(res, AppConstants.MSG_FETCHED)
        );
    }

    @PutMapping("/profile/{userId}")
    public ResponseEntity<ApiResponse<?>> updateUserProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UserProfileRequestDto userDto
    ) {
        ApplicationUserDto updatedUser = appUserService.updateAppUser(userId, userDto);
        return ResponseEntity.ok(
                ResponseUtil.buildCreatedResponse(updatedUser, "User profile updated")
        );
    }

    @GetMapping("/profiles")
    public ResponseEntity<ApiResponse<?>> getUserProfiles() {
        List<ApplicationUserDto> applicationUserDto = appUserService.getAllAppUser();
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(applicationUserDto, AppConstants.MSG_FETCHED)
        );
    }

    @DeleteMapping("/profile/{userId}")
    public ResponseEntity<ApiResponse<?>> deleteProfile(@PathVariable Long userId) {
        appUserService.deleteUserProfile(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/deleteAccount")
    public ResponseEntity<ApiResponse<?>> deleteAccount(Long userId) {
        appUserService.deleteAccount(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.buildOkResponse(null, "User Profile deleted successfully"));
    }

}
