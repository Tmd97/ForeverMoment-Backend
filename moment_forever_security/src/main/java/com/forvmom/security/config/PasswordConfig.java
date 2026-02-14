package com.forvmom.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * PasswordConfig - Configuration for password encoding
 *
 * Why separate configuration class?
 * - Centralized password configuration
 * - Easy to change encoder strength later
 * - Clean separation from security config
 */
@Configuration
public class PasswordConfig {

    /**
     * Creates a BCryptPasswordEncoder bean
     *
     * BCrypt features:
     * - Automatically generates salt (different hash for same password)
     * - Configurable strength (4-31, default 10)
     * - Slow hashing (prevents brute force attacks)
     * - Includes salt in the hash output
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Strength 10 = 2^10 iterations = good balance of security & performance
        return new BCryptPasswordEncoder();
    }
}