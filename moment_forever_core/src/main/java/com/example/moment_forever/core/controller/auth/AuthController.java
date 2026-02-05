package com.example.moment_forever.core.controller.auth;
import com.example.moment_forever.core.dto.auth.AuthResponse;
import com.example.moment_forever.core.dto.auth.LoginRequest;
import com.example.moment_forever.core.dto.auth.RegisterRequest;
import com.example.moment_forever.core.services.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Register a new user
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Login user
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Refresh access token using refresh token
     * POST /api/auth/refresh
     */
//    @PostMapping("/refresh")
//    public ResponseEntity<AuthResponse> refreshToken(
//            @RequestHeader("Authorization") String refreshToken) {
//
//        // Remove "Bearer " prefix if present
//        if (refreshToken.startsWith("Bearer ")) {
//            refreshToken = refreshToken.substring(7);
//        }
//
//        AuthResponse response = authService.refreshToken(refreshToken);
//        return ResponseEntity.ok(response);
//    }

    /**
     * Logout user (invalidate token)
     * POST /api/auth/logout
     */
//    @PostMapping("/logout")
//    public ResponseEntity<Void> logout(
//            @RequestHeader("Authorization") String token) {
//
//        // Remove "Bearer " prefix if present
//        if (token.startsWith("Bearer ")) {
//            token = token.substring(7);
//        }
//
//        authService.logout(token);
//        return ResponseEntity.ok().build();
//    }

    /**
     * Check if email is available
     * GET /api/auth/check-email?email=test@example.com
     */
//    @GetMapping("/check-email")
//    public ResponseEntity<Boolean> checkEmailAvailable(
//            @RequestParam String email) {
//
//        boolean available = authService.isEmailAvailable(email);
//        return ResponseEntity.ok(available);
//    }

    /**
     * Public endpoint to test if API is running
     * GET /api/auth/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Authentication API is running");
    }
}