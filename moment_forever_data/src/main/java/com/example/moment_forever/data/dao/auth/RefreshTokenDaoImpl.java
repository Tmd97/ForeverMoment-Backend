package com.example.moment_forever.data.dao.auth;

import com.example.moment_forever.data.dao.GenericDaoImpl;
import com.example.moment_forever.data.entities.auth.RefreshToken;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
@Transactional
public class RefreshTokenDaoImpl extends GenericDaoImpl<RefreshToken, Long> implements RefreshTokenDao {

    public RefreshTokenDaoImpl() {
        super(RefreshToken.class);
    }

    @Override
    public RefreshToken findByTokenHashAndRevokedFalse(String tokenHash) {
        try {
            return em.createQuery(
                            "SELECT rt FROM RefreshToken rt " +
                                    "WHERE rt.tokenHash = :tokenHash " +
                                    "AND rt.revoked = false",
                            RefreshToken.class
                    )
                    .setParameter("tokenHash", tokenHash)
                    .getSingleResult();

        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("No refresh token found for token hash: " + tokenHash);
        }
    }
}
