package com.forvmom.data.seeder;

import com.forvmom.data.dao.auth.RoleDao;
import com.forvmom.data.entities.auth.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * RoleDataSeeder - Creates initial system roles on application startup
 *
 * Why use CommandLineRunner?
 * - Runs after Spring Boot application context is loaded
 * - Executes before the application starts accepting requests
 * - Ensures system has required data before users try to register/login
 *
 * Why @Order(1)?
 * - Runs before other seeders (user seeders, etc.)
 * - Roles must exist before we can assign them to users
 */
// @Component
// @Order(1) // Run first
public class RoleDataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(RoleDataSeeder.class);

    private final RoleDao roleDao;

    // Constructor injection (better than @Autowired on field)
    public RoleDataSeeder(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Override
    @Transactional
    public void run(String... args) {
        logger.info("Starting role data initialization...");

        // 0. SUPER_ADMIN Role - Absolute access
        createRoleIfNotFound(
                "SUPER_ADMIN",
                "Super Administrator - absolute access",
                1000, // Highest permission level
                true // System role
        );

        // 1. USER Role - Basic customer role
        createRoleIfNotFound(
                "USER",
                "Regular customer - can book experiences and manage profile",
                10, // Lowest permission level
                true // System role (cannot be deleted)
        );

        // 2. ADMIN Role - Full system administrator
        createRoleIfNotFound(
                "ADMIN",
                "System administrator - full access to all features",
                100, // Highest permission level
                true // System role
        );

        // 3. CONTENT_MANAGER Role - Can manage experiences
        createRoleIfNotFound(
                "CONTENT_MANAGER",
                "Can create, edit, and manage experiences (services)",
                50, // Mid-level permission
                false // Can be deleted/modified
        );

        // 4. BOOKING_MANAGER Role - Can manage bookings
        createRoleIfNotFound(
                "BOOKING_MANAGER",
                "Can view, confirm, and cancel customer bookings",
                40, // Mid-level permission
                false // Can be deleted/modified
        );

        logger.info("Role data initialization completed.");
        // logger.info("Total roles in system: {}", roleDao.count());
    }

    /**
     * Helper method to create role if it doesn't exist
     *
     * Why this method?
     * - Idempotent: Can be run multiple times without creating duplicates
     * - Safe: Won't overwrite existing roles
     * - Logging: Tracks what's being created
     */
    private void createRoleIfNotFound(String name, String description,
            Integer permissionLevel, boolean systemRole) {

        // Check if role already exists (case-insensitive)
        boolean exists = roleDao.existsByNameIgnoreCase(name);

        if (!exists) {
            Role role = new Role(name, description);
            role.setPermissionLevel(permissionLevel);
            role.setSystemRole(systemRole);
            role.setActive(true);

            roleDao.save(role);

            logger.info("Created role: {} (Level: {}, System: {})",
                    name, permissionLevel, systemRole);
        } else {
            logger.debug("Role already exists: {}", name);

            // Optional: Update existing role if needed
            // updateExistingRoleIfChanged(name, description, permissionLevel, systemRole);
        }
    }

    /**
     * Optional: Update existing role if properties have changed
     * Useful for updating role descriptions/permissions across deployments
     */
    private void updateExistingRoleIfChanged(String name, String newDescription,
            Integer newPermissionLevel, boolean newSystemRole) {

        roleDao.findByNameIgnoreCase(name).ifPresent(existingRole -> {
            boolean needsUpdate = false;

            // Update description if different
            if (newDescription != null && !newDescription.equals(existingRole.getDescription())) {
                existingRole.setDescription(newDescription);
                needsUpdate = true;
            }

            // Update permission level if different
            if (newPermissionLevel != null && !newPermissionLevel.equals(existingRole.getPermissionLevel())) {
                existingRole.setPermissionLevel(newPermissionLevel);
                needsUpdate = true;
            }

            // Update system role flag if different
            if (newSystemRole != existingRole.isSystemRole()) {
                existingRole.setSystemRole(newSystemRole);
                needsUpdate = true;
            }

            if (needsUpdate) {
                roleDao.save(existingRole);
                logger.info("Updated role: {}", name);
            }
        });
    }
}