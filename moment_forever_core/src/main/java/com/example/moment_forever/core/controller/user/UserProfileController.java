package com.example.moment_forever.core.controller.user;

import com.example.moment_forever.common.response.ApiResponse;
import com.example.moment_forever.common.response.ResponseUtil;
import com.example.moment_forever.core.dto.ApplicationUserDto;
import com.example.moment_forever.core.dto.UserProfileRequestDto;
import com.example.moment_forever.core.services.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/profile")
public class UserProfileController {

    private final UserProfileService userService;

    @Autowired
    public UserProfileController(UserProfileService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getMe() {
        ApplicationUserDto currentUser = userService.getCurrentUserProfile();
        return ResponseEntity.ok(ResponseUtil.buildOkResponse(currentUser, "User profile fetched"));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<?>> updateMe(@RequestBody @Valid UserProfileRequestDto userProfileRequestDto) {
        ApplicationUserDto updatedProfile = userService.updateCurrentUserProfile(userProfileRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.buildCreatedResponse(updatedProfile, "User profile updated"));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<?>> deleteMe(@RequestParam String confirmPassword) {
        userService.deleteCurrentUserProfile(confirmPassword);
        return ResponseEntity.noContent().build();
    }

    // delete account endpoint, which will invalidate all tokens for the user and delete the user account
    //TODO: delete account can be delete via emailId, or username or refresh token
    @PostMapping("/deactivate")
    public ResponseEntity<ApiResponse<?>> deActivateAccount(
            @RequestHeader("Authorization") String token) {
        userService.deActivateAccount(token);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.buildOkResponse(null, "Account deleted successfully"));
    }
}