package com.forvmom.security.config;

import com.forvmom.security.service.CustomUserDetailsService;
import com.forvmom.security.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthenticationFilter - Intercepts requests and validates JWT tokens
 * <p>
 * This filter runs BEFORE Spring Security's authentication mechanisms
 * It extracts JWT token from Authorization header and validates it
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String requestPath = request.getRequestURI();
        logger.debug("Processing request to: {}", requestPath);

        // Skip filter for public endpoints (performance optimization)
        if (isPublicEndpoint(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 1: Extract JWT token from Authorization header
        final String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        final String jwt;
        final String username;

        // Check if header is present and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            logger.debug("No Bearer token found for request to: {}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        // Extract token (remove "Bearer " prefix)
        jwt = authHeader.substring(BEARER_PREFIX.length());
        logger.debug("JWT token extracted: {}...", jwt.substring(0, Math.min(20, jwt.length())));

        try {
            // Step 2: Extract username from token
            username = jwtService.extractUsername(jwt);
            logger.debug("Extracted username from token: {}", username);

            UserDetails userDetails = jwtService.buildUserDetailsFromToken(jwt);

            // Step 5: Validate token
            if (jwtService.validateToken(jwt)) {
                logger.debug("Token validation successful for user: {}", username);

                // Step 6: Create Authentication object
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null, // credentials are null because we're using token
                                userDetails.getAuthorities()  // may cause lazy loading issues if roles are not eagerly fetched, ensure your UserDetails implementation handles this
                        );

                // Add request details to authentication
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Step 7: Set authentication in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.debug("Authentication set in SecurityContext for user: {}", username);
            } else {
                logger.warn("Token validation failed for user: {}", username);
            }

        } catch (Exception e) {
            logger.error("JWT authentication error for request to {}: {}",
                    requestPath, e.getMessage());
            // Don't throw exception - let Spring Security handle unauthorized access
            // The request will continue but SecurityContext will remain empty
        }

        // Step 8: Continue filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Check if endpoint is public (no authentication required)
     * This is an optimization to skip token processing for public endpoints
     */
    private boolean isPublicEndpoint(String requestPath) {
        // Match the same public endpoints from SecurityConfig
        return requestPath.startsWith("/api/auth/") ||
                requestPath.startsWith("/api/public/") ||
                requestPath.startsWith("/api/swagger-ui/") ||
                requestPath.startsWith("/v3/api-docs/") ||
                requestPath.equals("/error");
    }

    /**
     * Extract JWT token from Authorization header (helper method)
     */
    public String extractTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }

        return null;
    }
}