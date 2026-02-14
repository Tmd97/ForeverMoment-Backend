package com.forvmom.security.service;

import com.forvmom.common.errorhandler.CustomAuthException;
import com.forvmom.data.dao.auth.AuthUserDao;
import com.forvmom.data.dao.auth.RefreshTokenDao;
import com.forvmom.data.entities.auth.AuthUser;
import com.forvmom.data.entities.auth.AuthUserRole;
import com.forvmom.data.entities.auth.RefreshToken;
import com.forvmom.data.entities.auth.Role;
import com.forvmom.security.dto.AuthResponse;
import com.forvmom.security.dto.JwtUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    private final AuthUserDao authUserDao;
    private final RefreshTokenDao refreshTokenDao;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration; // in milliseconds (e.g., 86400000 = 24 hours)

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration; // in milliseconds (e.g., 604800000 = 7 days)

    @Autowired
    public JwtService(AuthUserDao authUserDao, RefreshTokenDao refreshTokenDao) {
        this.authUserDao = authUserDao;
        this.refreshTokenDao = refreshTokenDao;
    }

    // Generate signing key from secret
    private SecretKey getSigningKey() {
        // Convert string secret to cryptographic key
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generate JWT token for a user
     *
     * @param authUser The authenticated user
     * @return JWT token string
     */
    public String generateToken(AuthUser authUser) {
        logger.debug("Generating JWT token for user: {}", authUser.getUsername());

        // Create claims (payload data)
        Map<String, Object> claims = new HashMap<>();

        // Add user info to token payload
        claims.put("userId", authUser.getId());
        claims.put("username", authUser.getUsername());
        claims.put("externalUserId", authUser.getExternalUserId());

        // Add roles as comma-separated string
        // TODO: need to fix this, Not seems good coding design (FIXED)
        String roles = authUser.getUserRoles().stream()
                .map(authUserRole -> {
                    Role role = authUserRole.getRole();
                    return role != null ? role.getName() : "";
                })
                .filter(roleName -> !roleName.isEmpty())
                .collect(Collectors.joining(","));
        // Add user status
        claims.put("roles", roles);
        claims.put("enabled", authUser.isEnabled());
        claims.put("accountNonLocked", authUser.isAccountNonLocked());

        return buildToken(claims, authUser.getUsername(), jwtExpiration);
    }

    /**
     * Generate refresh token (longer expiration, less data)
     */
    public String generateAndSaveRefreshToken(AuthUser authUser) {
        logger.debug("Generating refresh token for user: {}", authUser.getUsername());

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", authUser.getId());
        claims.put("externalUserId", authUser.getExternalUserId());
        claims.put("type", "refresh"); // Mark as refresh token

        String rawRefreshToken = buildToken(claims, authUser.getUsername(), refreshExpiration);
        // save it to the DB for old refresh token revoke, prevent attacks & hack, for
        // block account
        saveNewRefreshToken(authUser, rawRefreshToken);
        return rawRefreshToken;
    }

    // ===== Hash helper =====
    public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Token hashing failed", e);
        }
    }

    public void saveNewRefreshToken(AuthUser user, String rawToken) {
        RefreshToken token = new RefreshToken();
        token.setAuthUser(user);
        String hashedRefreshedToken = hashToken(rawToken);
        token.setTokenHash(hashedRefreshedToken);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiryDate(
                LocalDateTime.now().plus(refreshExpiration, ChronoUnit.MILLIS));
        token.setRevoked(false);
        refreshTokenDao.save(token);
    }

    /**
     * Build a JWT token with given claims
     */
    private String buildToken(Map<String, Object> claims, String subject, long expiration) {
        return Jwts.builder()
                .setClaims(claims) // Payload data
                .setSubject(subject) // User identifier
                .setIssuedAt(new Date(System.currentTimeMillis())) // When issued (iat)
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // When expires
                .setIssuer("moment-forever-app")
                .setAudience("moment-forever-client")
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Sign with secret key
                .compact(); // Convert to string
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", Long.class);
    }

    public Long extractExternalUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("externalUserId", Long.class);
    }

    public String extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", String.class);
    }

    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    /**
     * Validate token WITHOUT database call (stateless validation)
     * Checks: signature, expiration, required claims
     */
    public boolean isTokenValid(String token) {
        try {
            // This validates signature and parses the token
            Claims claims = extractAllClaims(token);

            // Check expiration
            if (isTokenExpired(token)) {
                logger.debug("Token expired for user: {}", claims.getSubject());
                return false;
            }

            // Check required claims
            if (claims.getSubject() == null || claims.getSubject().isEmpty()) {
                logger.debug("Token missing subject");
                return false;
            }

            // Check if user is enabled (from token claim)
            Boolean enabled = claims.get("enabled", Boolean.class);
            if (enabled != null && !enabled) {
                logger.debug("User account disabled in token: {}", claims.getSubject());
                return false;
            }

            // Check if account is locked
            Boolean accountNonLocked = claims.get("accountNonLocked", Boolean.class);
            if (accountNonLocked != null && !accountNonLocked) {
                logger.debug("User account locked in token: {}", claims.getSubject());
                return false;
            }

            return true;

        } catch (ExpiredJwtException e) {
            logger.debug("Token expired: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            logger.debug("Invalid token format: {}", e.getMessage());
            return false;
        } catch (SignatureException e) {
            logger.debug("Invalid token signature: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean validateToken(String token) {
        try {
            return isTokenValid(token);
        } catch (Exception e) {
            logger.debug("Token validation failed for user {}: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Legacy method - validates with UserDetails (for login)
     */
    // public boolean validateToken(String token, UserDetails userDetails) {
    // return validateToken(token, userDetails.getUsername());
    // }

    /**
     * Generic method to extract any claim
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // Verify signature with our secret
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public UserDetails buildUserDetailsFromToken(String token) {

        // Reuse existing methods (no duplication)
        String username = extractUsername(token);
        String rolesString = extractRoles(token);
        Long userId = extractUserId(token); // Make sure you have this method

        var authorities = rolesString == null || rolesString.isBlank()
                ? java.util.List.<org.springframework.security.core.authority.SimpleGrantedAuthority>of()
                : java.util.Arrays.stream(rolesString.split(","))
                        .map(String::trim)
                        .filter(role -> !role.isEmpty())
                        .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                "ROLE_" + role))
                        .toList();
        return new JwtUserDetails(userId, username, authorities);
    }

    public AuthResponse generateRefreshTokenWithAccessToken(String token) {
        try {
            // Step 1: Validate signature & expiration first
            if (!isTokenValid(token)) {
                throw new IllegalArgumentException("Invalid or expired token");
            }

            // Step 2: Extract claims
            Claims claims = extractAllClaims(token);

            // Step 3: Check if it is a refresh token
            String type = claims.get("type", String.class);
            if (!"refresh".equals(type)) {
                throw new IllegalArgumentException("Provided token is not a refresh token");
            }

            // Step 4: Rebuild AuthUser from DB (never from the token)
            // Use optimized query to fetch roles eagerly
            AuthUser authUser = authUserDao.findByIdWithRoles(claims.get("userId", Long.class))
                    .orElseThrow(() -> new CustomAuthException("User not found from refresh token"));

            // 4. HASH incoming refresh token
            String tokenHash = hashToken(token);

            // step 6: get the old refresh token from the DB
            // 5. Find token in DB
            RefreshToken storedToken;
            try {
                storedToken = refreshTokenDao
                        .findByTokenHashAndRevokedFalse(tokenHash);
            } catch (Exception e) {
                throw new CustomAuthException("Refresh token expired");
            }

            // 6. Check expiry in DB (extra safety)
            if (storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                throw new CustomAuthException("Refresh token expired");
            }

            // 7. Revoke OLD refresh token (rotation)
            storedToken.setRevoked(true);
            refreshTokenDao.save(storedToken);
            String accessToken = generateToken(authUser);
            String generatedRefreshToken = generateAndSaveRefreshToken(authUser);

            AuthResponse authResponse = new AuthResponse();
            authResponse.setToken(accessToken);
            authResponse.setRefreshToken(generatedRefreshToken);
            return authResponse;

        } catch (Exception e) {
            logger.debug("Failed to generate token from refresh token: {}", e.getMessage());
            throw new CustomAuthException("Invalid refresh token");
        }
    }

    @Transactional
    public void revokeRefreshToken(String rawToken) {
        String tokenHash = hashToken(rawToken);
        logger.debug("Revoking refresh token with hash: {}", tokenHash);
        RefreshToken storedToken = refreshTokenDao
                .findByTokenHashAndRevokedFalse(tokenHash);
        if (storedToken == null) {
            throw new CustomAuthException("Invalid refresh token");
        }
        storedToken.setRevoked(true);
        refreshTokenDao.save(storedToken);
    }
}