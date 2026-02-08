package com.example.moment_forever.data.dao.auth;

import com.example.moment_forever.common.errorhandler.ResourceNotFoundException;
import com.example.moment_forever.data.dao.GenericDaoImpl;
import com.example.moment_forever.data.entities.auth.AuthUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

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
                AuthUser.class
        );
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
                Long.class
        );
        query.setParameter("username", username);

        return query.getSingleResult() > 0;
    }

    @Override
    public boolean existsByExternalUserId(Long externalUserId) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(a) FROM AuthUser a WHERE a.externalUserId = :externalUserId",
                Long.class
        );
        query.setParameter("externalUserId", externalUserId);

        return query.getSingleResult() > 0;
    }

    @Override
    public Optional<AuthUser> findByUsernameWithRoles(String username) {
        TypedQuery<AuthUser> query = em.createQuery(
                "SELECT DISTINCT a FROM AuthUser a " +
                        "LEFT JOIN FETCH a.userRoles ur " +
                        "LEFT JOIN FETCH ur.role " +
                        "WHERE a.username = :username",
                AuthUser.class
        );
        query.setParameter("username", username);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            throw new ResourceNotFoundException("User not found with username: " + username);
        }
    }
}