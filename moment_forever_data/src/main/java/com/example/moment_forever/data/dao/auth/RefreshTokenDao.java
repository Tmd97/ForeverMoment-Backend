package com.example.moment_forever.data.dao.auth;

import com.example.moment_forever.data.dao.GenericDao;
import com.example.moment_forever.data.entities.auth.RefreshToken;

import java.util.Optional;

public interface RefreshTokenDao extends GenericDao<RefreshToken, Long> {
    RefreshToken findByTokenHashAndRevokedFalse(String tokenHash);
}
