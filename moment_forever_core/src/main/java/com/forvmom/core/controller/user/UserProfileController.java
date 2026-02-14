package com.forvmom.core.controller.user;

import com.forvmom.common.response.ApiResponse;
import com.forvmom.common.response.ResponseUtil;
import com.forvmom.common.dto.response.AppUserResponseDto;
import com.forvmom.common.dto.request.UserProfileRequestDto;
import com.forvmom.core.services.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/user/profile")
@Tag(name = "User Profile API", description = "Endpoints for managing user profile")
public class UserProfileController {

    private final UserProfileService userService;

    @Autowired
    public UserProfileController(UserProfileService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get Current User Profile", description = "Fetch the profile of the currently authenticated user")
    public ResponseEntity<ApiResponse<?>> getMe() {
        AppUserResponseDto currentUser = userService.getCurrentUserProfile();
        return ResponseEntity.ok(ResponseUtil.buildOkResponse(currentUser, "User profile fetched"));
    }

    @PutMapping
    @Operation(summary = "Update User Profile", description = "Update the profile details of the currently authenticated user")
    public ResponseEntity<ApiResponse<?>> updateMe(@RequestBody @Valid UserProfileRequestDto userProfileRequestDto) {
        AppUserResponseDto updatedProfile = userService.updateCurrentUserProfile(userProfileRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.buildCreatedResponse(updatedProfile, "User profile updated"));
    }

    @DeleteMapping
    @Operation(summary = "Delete User Profile", description = "Permanently delete the current user's profile (Requires password confirmation)")
    public ResponseEntity<ApiResponse<?>> deleteMe(@RequestParam String confirmPassword) {
        userService.deleteCurrentUserProfile(confirmPassword);
        return ResponseEntity.noContent().build();
    }

    // delete account endpoint, which will invalidate all tokens for the user and
    // delete the user account
    // TODO: delete account can be delete via emailId, or username or refresh token
    @PostMapping("/deactivate")
    @Operation(summary = "Deactivate Account", description = "Deactivate the current user's account")
    public ResponseEntity<ApiResponse<?>> deActivateAccount(
            @RequestHeader("Authorization") String token) {
        userService.deactivateCurrentAccount(token);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.buildOkResponse(null, "Account deleted successfully"));
    }
}