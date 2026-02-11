package com.example.moment_forever.security.controller;

import com.example.moment_forever.common.errorhandler.NotAllowedCustomException;
import com.example.moment_forever.common.response.ApiResponse;
import com.example.moment_forever.common.response.ResponseUtil;
import com.example.moment_forever.data.entities.auth.AuthUser;
import com.example.moment_forever.security.dto.AuthResponse;
import com.example.moment_forever.security.dto.JwtUserDetails;
import com.example.moment_forever.security.service.AuthService;
import com.example.moment_forever.security.dto.LoginRequest;
import com.example.moment_forever.security.dto.RegisterRequestDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasRole;

//TODO: Email Based authentication, Password Reset, Account Verification, etc.
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(
            @Valid @RequestBody RegisterRequestDto request) {
//        // 1. Get current authenticated user
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        JwtUserDetails currentUser = (JwtUserDetails) authentication.getPrincipal();
//        // 2. CHECK if current user is SUPER_ADMIN
//        boolean isSuperAdmin = currentUser.getAuthorities().stream()
//                .anyMatch(authUserRole -> authUserRole.getAuthority().equalsIgnoreCase("SUPER_ADMIN"));
//        // 3. ENFORCE the rule - throw exception if not SUPER_ADMIN
//        if (!isSuperAdmin) {
//            throw new NotAllowedCustomException("Only Super Admin can register new users");
//        }
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.buildCreatedResponse(response, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.buildOkResponse(response, "User logged in successfully"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> getRefreshToken(
            @RequestHeader("Authorization") String token) {

        // Remove "Bearer " prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        AuthResponse response = authService.generateRefreshToken(token);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(
            @RequestHeader("Authorization") String token) {

        // Remove "Bearer " prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        authService.logout(token);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.buildOkResponse(null, "Logout successful"));
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Authentication API is running");
    }
}