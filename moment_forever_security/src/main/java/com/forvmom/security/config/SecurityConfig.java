package com.forvmom.security.config;

import com.forvmom.security.jwt_exception_handler.JwtAccessDeniedHandler;
import com.forvmom.security.jwt_exception_handler.JwtAuthenticationEntryPoint;
import com.forvmom.security.service.CustomUserDetailsService;
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
 * <p>
 * Configures:
 * 1. Which endpoints are public/protected
 * 2. JWT authentication filter
 * 3. Password encoder
 * 4. UserDetailsService
 * 5. Session management (stateless)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, // Enables @Secured annotation
                jsr250Enabled = true, // Enables @RolesAllowed annotation
                prePostEnabled = true // Enables @PreAuthorize, @PostAuthorize
)
public class SecurityConfig {

        private final CustomUserDetailsService userDetailsService;
        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final PasswordEncoder passwordEncoder;
        private final JwtAuthenticationEntryPoint authenticationEntryPoint;
        private final JwtAccessDeniedHandler accessDeniedHandler;

        @Autowired
        public SecurityConfig(
                        CustomUserDetailsService userDetailsService,
                        JwtAuthenticationFilter jwtAuthenticationFilter,
                        PasswordEncoder passwordEncoder,
                        JwtAuthenticationEntryPoint authenticationEntryPoint,
                        JwtAccessDeniedHandler accessDeniedHandler) {
                this.userDetailsService = userDetailsService;
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
                this.passwordEncoder = passwordEncoder;
                this.authenticationEntryPoint = authenticationEntryPoint;
                this.accessDeniedHandler = accessDeniedHandler;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())

                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint(authenticationEntryPoint)
                                                .accessDeniedHandler(accessDeniedHandler))
                                // Configure authorization rules - ORDER IS IMPORTANT!
                                .authorizeHttpRequests(auth -> auth
                                                // Public endpoints - should come FIRST
                                                // Request matchers strip the context path automatically
                                                .requestMatchers(
                                                                "/auth/**",
                                                                "/public/**",
                                                                "/error",
                                                                "/v3/api-docs/**",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html",
                                                                "/swagger-resources/**",
                                                                "/webjars/**" // for Swagger UI assets)
                                                ).permitAll()
                                                .requestMatchers("/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                                                .requestMatchers(
                                                                "/user/**",
                                                                "/booking/**",
                                                                "/review/**")
                                                .hasAnyRole("USER", "ADMIN")
                                                .anyRequest().authenticated())

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

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