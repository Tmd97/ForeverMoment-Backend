package com.example.moment_forever.security.controller;

import com.example.moment_forever.common.response.ApiResponse;
import com.example.moment_forever.common.response.ResponseUtil;
import com.example.moment_forever.security.dto.AuthResponse;
import com.example.moment_forever.security.service.AuthService;
import com.example.moment_forever.security.dto.LoginRequest;
import com.example.moment_forever.security.dto.RegisterRequestDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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