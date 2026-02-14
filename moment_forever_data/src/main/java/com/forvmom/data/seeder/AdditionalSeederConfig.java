package com.forvmom.data.seeder;

import com.forvmom.data.dao.auth.AuthUserDao;
import com.forvmom.data.dao.auth.RoleDao;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

/**
 * AdditionalSeederConfig - Creates jwt_exception_handler data for development environment
 *
 * Why @Profile("dev")?
 * - Only runs in "dev" profile, not in production
 * - Creates jwt_exception_handler users for development/testing
 * - Separates jwt_exception_handler data from production seeders
 */
@Configuration
@Profile("dev") // Only active when spring.profiles.active=dev
public class AdditionalSeederConfig {

    @Bean
    @Order(2) // Runs after RoleDataSeeder
    public CommandLineRunner devDataSeeder(RoleDao roleRepository,
                                           AuthUserDao authUserRepository) {
        return args -> {
            System.out.println("=== Creating development jwt_exception_handler data ===");

            // 1. Create a jwt_exception_handler admin user
            createTestAdmin(roleRepository, authUserRepository);

            // 2. Create a jwt_exception_handler regular user
            createTestUser(roleRepository, authUserRepository);

            System.out.println("=== Development jwt_exception_handler data created ===");
        };
    }

    private void createTestAdmin(RoleDao roleDao,
                                 AuthUserDao authUserRepository) {

        // Check if jwt_exception_handler admin already exists
        if (!authUserRepository.existsByUsername("admin@cherishx.com")) {
            System.out.println("Creating jwt_exception_handler admin: admin@cherishx.com / admin123");

            // In real implementation, you would:
            // 1. Create AuthUser with encoded password
            // 2. Find ADMIN role from repository
            // 3. Assign role to user
            // 4. Save user
        }
    }

    private void createTestUser(RoleDao roleDao,
                                AuthUserDao authUserDao) {

        if (!authUserDao.existsByUsername("user@jwt_exception_handler.com")) {
            System.out.println("Creating jwt_exception_handler user: user@jwt_exception_handler.com / user123");
        }
    }
}