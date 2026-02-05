package com.example.moment_forever.data.dao.auth;

import com.example.moment_forever.data.dao.GenericDao;
import com.example.moment_forever.data.entities.auth.AuthUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthUserDao extends GenericDao<AuthUser, Long>{

    Optional<AuthUser> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("SELECT DISTINCT au FROM AuthUser au " +
            "LEFT JOIN FETCH au.roles " +
            "WHERE au.username = :username")
    Optional<AuthUser> findByUsernameWithRoles(@Param("username") String username);

    boolean existsByExternalUserId(Long externalUserId);
}