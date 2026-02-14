package com.forvmom.data.dao.auth;

import com.forvmom.data.dao.GenericDao;
import com.forvmom.data.entities.auth.RefreshToken;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenDao extends GenericDao<RefreshToken, Long> {
    RefreshToken findByTokenHashAndRevokedFalse(String tokenHash);

    List<RefreshToken> findByAuthUserIdAndRevokedFalse(Long authUserId);

    Optional<RefreshToken> findByAUserEmailId(String email);
}
