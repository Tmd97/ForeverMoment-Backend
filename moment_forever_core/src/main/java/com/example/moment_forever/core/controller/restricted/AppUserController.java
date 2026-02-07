package com.example.moment_forever.core.controller.restricted;

import com.example.moment_forever.common.response.ApiResponse;
import com.example.moment_forever.common.response.ResponseUtil;
import com.example.moment_forever.common.utils.AppConstants;
import com.example.moment_forever.core.dto.ApplicationUserDto;
import com.example.moment_forever.core.dto.CategoryDto;
import com.example.moment_forever.core.services.AppUserService;
import com.example.moment_forever.core.services.CategoryService;
import com.example.moment_forever.security.config.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class AppUserController {


    @Autowired
    private AppUserService appUserService;


//    @PostMapping("/profile/")
//    public ResponseEntity<ApiResponse<?>> createUserProfile(@RequestBody ApplicationUserDto userDto) {
//        ApplicationUserDto applicationUserDto = appUserService.createUserProfile(userDto);
//        return ResponseEntity.ok(
//                ResponseUtil.buildCreatedResponse(applicationUserDto, AppConstants.MSG_CREATED)
//        );
//    }


    @GetMapping("/profile/{userId}")
    public ResponseEntity<ApiResponse<?>> getUserProfile(@PathVariable Long userId) {
        ApplicationUserDto applicationUserDto = appUserService.getAppUserById(userId);
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(applicationUserDto, AppConstants.MSG_FETCHED)
        );
    }

    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<?>> getUserProfile(@RequestBody ApplicationUserDto applicationUserDto) {
        ApplicationUserDto res = appUserService.getAppUserByEmailId(applicationUserDto.getEmail());
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(res, AppConstants.MSG_FETCHED)
        );
    }

    @PutMapping("/profile/{userId}")
    public ResponseEntity<ApiResponse<?>> updateUserProfile(
            @PathVariable Long userId,
            @RequestBody ApplicationUserDto userDto
    ) {
        ApplicationUserDto updatedUser = appUserService.updateUserProfile(userId, userDto);
        return ResponseEntity.ok(
                ResponseUtil.buildCreatedResponse(updatedUser, "User profile updated")
        );
    }

    @DeleteMapping("/profile/{userId}")
    public ResponseEntity<ApiResponse<?>> deleteUserProfile(@PathVariable Long userId) {
        appUserService.deleteUser(userId);
        return ResponseEntity.ok(
                ResponseUtil.buildCreatedResponse(null, "User profile deleted")
        );
    }


    @GetMapping("/profiles")
    public ResponseEntity<ApiResponse<?>> getUserProfiles() {
        List<ApplicationUserDto> applicationUserDto = appUserService.getAllProfiles();
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(applicationUserDto, AppConstants.MSG_FETCHED)
        );
    }

    @GetMapping("/profile/me")
    public ResponseEntity<ApiResponse<?>> getCurrentUser() {
        ApplicationUserDto currentUser = appUserService.getCurrentUser();
        return ResponseEntity.ok(
                ResponseUtil.buildOkResponse(currentUser, AppConstants.MSG_FETCHED)
        );

    }

}
