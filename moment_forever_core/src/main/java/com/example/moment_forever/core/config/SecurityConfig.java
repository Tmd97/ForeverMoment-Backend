package com.example.moment_forever.core.config;

import com.example.moment_forever.core.security.JwtAuthenticationFilter;
import com.example.moment_forever.core.services.auth.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SecurityConfig - Main security configuration
 *
 * Configures:
 * 1. Which endpoints are public/protected
 * 2. JWT authentication filter
 * 3. Password encoder
 * 4. UserDetailsService
 * 5. Session management (stateless)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        securedEnabled = true,  // Enables @Secured annotation
        jsr250Enabled = true,   // Enables @RolesAllowed annotation
        prePostEnabled = true   // Enables @PreAuthorize, @PostAuthorize
)
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfig(
            CustomUserDetailsService userDetailsService,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Main security filter chain configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                // Configure authorization rules - ORDER IS IMPORTANT!
                .authorizeHttpRequests(auth -> auth
                        // 1. Public endpoints - should come FIRST
                        // Request matchers strip the context path automatically
                        .requestMatchers(
                                "/auth/**",// All auth endpoints
                                "/public/**",         // All public endpoints
                                "/error",                 // Error endpoint
                                "/swagger-ui/**",         // Swagger UI
                                "/v3/api-docs/**",        // API docs
                                "/swagger-ui.html",       // Swagger HTML
                                "/webjars/**",            // WebJars for Swagger
                                "/swagger-resources/**"   // Swagger resources
                        ).permitAll()
                         //2. Admin endpoints
                        .requestMatchers("/admin/**", "/admin/**").hasRole("ADMIN")

                        // 3. User endpoints
                        .requestMatchers(
                                "/user/**",
                                "/booking/**",
                                "/review/**"
                        ).hasRole("USER")

                        // 4. All other requests require authentication - MUST BE LAST
                        .anyRequest().authenticated()
                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Authentication provider using our UserDetailsService and PasswordEncoder
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    /**
     * Authentication manager bean
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}