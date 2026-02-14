package com.forvmom.security.controller;

import com.forvmom.common.errorhandler.NotAllowedCustomException;
import com.forvmom.common.response.ApiResponse;
import com.forvmom.common.response.ResponseUtil;
import com.forvmom.data.entities.auth.AuthUser;
import com.forvmom.security.dto.AuthResponse;
import com.forvmom.security.dto.JwtUserDetails;
import com.forvmom.security.service.AuthService;
import com.forvmom.security.dto.LoginRequest;
import com.forvmom.security.dto.RegisterRequestDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasRole;

//TODO: Email Based authentication, Password Reset, Account Verification, etc.
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication API", description = "Endpoints for user registration and login")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register User", description = "Register a new user account")
    public ResponseEntity<ApiResponse<?>> register(
            @Valid @RequestBody RegisterRequestDto request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.buildCreatedResponse(null, "User registered successfully"));
    }

    @PostMapping("/login")
    @Operation(summary = "Login User", description = "Authenticate user and return JWT token")
    public ResponseEntity<ApiResponse<?>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseUtil.buildOkResponse(response, "User logged in successfully"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh Token", description = "Generate a new access token using refresh token")
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
    @Operation(summary = "Logout User", description = "Invalidate current session/token")
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
    @Operation(summary = "Health Check", description = "Check if auth service is running")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Authentication API is running");
    }
}