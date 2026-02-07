package com.example.moment_forever.security.config;
import com.example.moment_forever.common.response.ApiResponse;
import com.example.moment_forever.common.response.ResponseUtil;
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
    public ResponseEntity<ApiResponse<?>> register(
            @Valid @RequestBody RegisterRequest request) {

        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.buildCreatedResponse(response,"User registered successfully"));
    }

    /**
     * Login user
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.buildOkResponse(response,"User logged in successfully"));
    }

    /**
     * Refresh access token using refresh token
     * POST /api/auth/refresh
     */
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

    /**
     * Logout user (invalidate token)
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(
            @RequestHeader("Authorization") String token) {

        // Remove "Bearer " prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        authService.logout(token);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.buildOkResponse(null,"Logout successful"));
    }

    /**
     * Check if email is available
     * GET /api/auth/check-email?email=jwt_exception_handler@example.com
     */
//    @GetMapping("/check-email")
//    public ResponseEntity<Boolean> checkEmailAvailable(
//            @RequestParam String email) {
//
//        boolean available = authService.isEmailAvailable(email);
//        return ResponseEntity.ok(available);
//    }

    /**
     * Public endpoint to jwt_exception_handler if API is running
     * GET /api/auth/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Authentication API is running");
    }
}