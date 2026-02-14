package com.forvmom.data.dao.auth;

import com.forvmom.common.errorhandler.ResourceNotFoundException;
import com.forvmom.data.dao.GenericDaoImpl;
import com.forvmom.data.entities.auth.AuthUser;
import com.forvmom.data.entities.auth.AuthUserRole;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class AuthUserDaoImpl extends GenericDaoImpl<AuthUser, Long> implements AuthUserDao {

    public AuthUserDaoImpl() {
        super(AuthUser.class);
    }

    @Override
    public Optional<AuthUser> findByUsername(String username) {
        TypedQuery<AuthUser> query = em.createQuery(
                "SELECT a FROM AuthUser a WHERE a.username = :username",
                AuthUser.class);
        query.setParameter("username", username);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            throw new ResourceNotFoundException("User not found with username: " + username);
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(a) FROM AuthUser a WHERE a.username = :username",
                Long.class);
        query.setParameter("username", username);

        return query.getSingleResult() > 0;
    }

    @Override
    public boolean existsByExternalUserId(Long externalUserId) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(a) FROM AuthUser a WHERE a.externalUserId = :externalUserId",
                Long.class);
        query.setParameter("externalUserId", externalUserId);

        return query.getSingleResult() > 0;
    }

    @Override
    public Optional<AuthUser> findByUsernameWithRoles(String username) {
        try {
            AuthUser user = em.createQuery(
                    "SELECT DISTINCT a FROM AuthUser a " +
                            "LEFT JOIN FETCH a.userRoles ur " +
                            "LEFT JOIN FETCH ur.role " +
                            "WHERE a.username = :username",
                    AuthUser.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<AuthUser> findByIdWithRoles(Long id) {
        try {
            AuthUser user = em.createQuery(
                    "SELECT DISTINCT a FROM AuthUser a " +
                            "LEFT JOIN FETCH a.userRoles ur " +
                            "LEFT JOIN FETCH ur.role " +
                            "WHERE a.id = :id",
                    AuthUser.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    // it return the empty list not null when no user found with the role id
    @Override
    public List<AuthUserRole> findAuthUserByRole(Long id) {
        TypedQuery<AuthUserRole> query = em.createQuery(
                "SELECT ur FROM AuthUserRole ur " +
                        "JOIN FETCH ur.authUser au " +
                        "WHERE ur.role.id = :roleId",
                AuthUserRole.class);
        query.setParameter("roleId", id);
        return query.getResultList();
    }
}