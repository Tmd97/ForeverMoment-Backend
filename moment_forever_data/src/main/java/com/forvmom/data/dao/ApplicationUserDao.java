package com.forvmom.data.dao;

import com.forvmom.data.entities.ApplicationUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * ApplicationUserDao - Data access for business user profiles
 */
public interface ApplicationUserDao extends GenericDao<ApplicationUser, Long> {

    /**
     * Find ApplicationUser by email (case-insensitive)
     */
    Optional<ApplicationUser> findByEmailIgnoreCase(String email);

    /**
     * Find ApplicationUser by auth_user_id
     * This is the CRITICAL link between AuthUser and ApplicationUser
     */
    Optional<ApplicationUser> findByAuthUserId(Long authUserId);

    /**
     * Check if email already exists (case-insensitive)
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Check if phone number already exists
     */
    boolean existsByPhoneNumber(String phoneNumber);

    /**
     * Find users by preferred city (case-insensitive)
     */
    List<ApplicationUser> findByPreferredCityIgnoreCase(String city);

    /**
     * Find users created after a specific date
     */
    List<ApplicationUser> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Search users by name or email (partial match, case-insensitive)
     */
    List<ApplicationUser> searchByNameOrEmail(String searchTerm);

    void deleteByAppUserId(Long id);

    /**
     * Optimized fetch for Admin: Get all users with AuthUser and Roles in one query
     */
    List<ApplicationUser> findAllWithAuthAndRoles();

    /**
     * Optimized fetch for Admin: Get user by ID with AuthUser and Roles in one
     * query
     */
    Optional<ApplicationUser> findByIdWithAuthAndRoles(Long id);
}