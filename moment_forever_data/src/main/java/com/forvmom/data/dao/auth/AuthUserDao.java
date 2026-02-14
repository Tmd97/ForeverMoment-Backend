package com.forvmom.data.dao.auth;

import com.forvmom.data.dao.GenericDao;
import com.forvmom.data.entities.auth.AuthUser;
import com.forvmom.data.entities.auth.AuthUserRole;
import jakarta.persistence.TypedQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthUserDao extends GenericDao<AuthUser, Long> {

    Optional<AuthUser> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("SELECT DISTINCT au FROM AuthUser au " +
            "LEFT JOIN FETCH au.userRoles ur " +
            "LEFT JOIN FETCH ur.role " +
            "WHERE au.username = :username")
    Optional<AuthUser> findByUsernameWithRoles(@Param("username") String username);

    @Query("SELECT DISTINCT au FROM AuthUser au " +
            "LEFT JOIN FETCH au.userRoles ur " +
            "LEFT JOIN FETCH ur.role " +
            "WHERE au.id = :id")
    Optional<AuthUser> findByIdWithRoles(@Param("id") Long id);

    boolean existsByExternalUserId(Long externalUserId);

    List<AuthUserRole> findAuthUserByRole(Long id);
}