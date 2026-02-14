package com.forvmom.data.dao.auth;


import com.forvmom.data.dao.GenericDao;
import com.forvmom.data.entities.auth.AuthUserRole;

public interface AuthUserRoleDao extends GenericDao<AuthUserRole, Long> {

    // Check if user already has this role
    boolean existsByAuthUserIdAndRoleId(Long authUserId, Long roleId);

    // Find all role assignments for a user
    java.util.List<AuthUserRole> findByAuthUserId(Long authUserId);

    // Find all users with a specific role
    java.util.List<AuthUserRole> findByRoleId(Long roleId);

    // Remove specific role from user
    void deleteByAuthUserIdAndRoleId(Long authUserId, Long roleId);
}