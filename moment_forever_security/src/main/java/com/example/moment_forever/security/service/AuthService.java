package com.example.moment_forever.security.service;

import com.example.moment_forever.common.dto.response.AppUserResponseDto;
import com.example.moment_forever.common.errorhandler.CustomAuthException;
import com.example.moment_forever.data.dao.ApplicationUserDao;
import com.example.moment_forever.data.dao.auth.AuthUserDao;
import com.example.moment_forever.data.dao.auth.AuthUserRoleDao;
import com.example.moment_forever.data.dao.auth.RefreshTokenDao;
import com.example.moment_forever.data.dao.auth.RoleDao;
import com.example.moment_forever.data.entities.ApplicationUser;
import com.example.moment_forever.data.entities.auth.AuthUser;
import com.example.moment_forever.data.entities.auth.AuthUserRole;
import com.example.moment_forever.data.entities.auth.Role;
import com.example.moment_forever.security.dto.*;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final AuthUserDao authUserDao;
    private final AuthUserRoleDao authUserRoleDao;
    private final ApplicationUserDao applicationUserDao;
    private final RoleDao roleDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenDao refreshTokenDao;

    public AuthService(AuthenticationManager authenticationManager, AuthUserDao authUserDao,
                       AuthUserRoleDao authUserRoleDao, ApplicationUserDao applicationUserDao, RoleDao roleDao,
                       PasswordEncoder passwordEncoder, JwtService jwtService, RefreshTokenDao refreshTokenDao) {
        this.authenticationManager = authenticationManager;
        this.authUserDao = authUserDao;
        this.authUserRoleDao = authUserRoleDao;
        this.applicationUserDao = applicationUserDao;
        this.roleDao = roleDao;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenDao = refreshTokenDao;
    }

    @Transactional
    public AuthResponse register(RegisterRequestDto request) {

        validateEmailNotExists(request.getEmail());
        AuthUser authUser = AuthBeanMapper.mapDtoToEntity(request);
        // TODO: (encoding to be done) ENCODE THE PASSWORD HERE!
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        authUser.setPassword(encodedPassword);
        Role role = roleDao.findById(request.getRoleId());
        authUser.addRole(role);
        AuthUser savedAuthUser = authUserDao.save(authUser);
        ApplicationUser appUser = AppUserBeanMapper.mapDtoToEntity(request);
        appUser.setAuthUser(authUser);
        ApplicationUser savedAppUser = applicationUserDao.save(appUser);
        return buildRegistrationResponseWithoutToken(savedAuthUser, savedAppUser);
    }

    private AuthResponse buildRegistrationResponseWithoutToken(AuthUser authUser, ApplicationUser appUser) {
        // TODO using the JWT auth response for registration response , not good coding
        // design, need to refactor later
        AuthResponse registrationResponse = new AuthResponse();
        registrationResponse.setEmail(authUser.getUsername());
        registrationResponse.setMessage("Registration successful for " + authUser.getUsername() + " Please verify");
        List<Long> roleIdList = authUser.getUserRoles().stream().map(authUserRole -> authUserRole.getRole().getId())
                .collect(Collectors.toList());
        registrationResponse.setRoleIds(roleIdList);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object o = authentication.getPrincipal();
            if (o instanceof JwtUserDetails) {
                JwtUserDetails currentUser = (JwtUserDetails) o;
                registrationResponse.setAssignedBy(currentUser.getUsername());
            } else if (o instanceof String) {
                registrationResponse.setAssignedBy((String) o);
            }
        } else {
            registrationResponse.setAssignedBy("SYSTEM"); // called SuperAdmin
        }
        registrationResponse.setUserId(authUser.getId());
        registrationResponse.setEmail(authUser.getUsername());
        return registrationResponse;
    }

    private void validateEmailNotExists(String email) {
        if (authUserDao.existsByUsername(email)) {
            logger.warn("Registration failed: Email already exists - {}", email);
            throw new CustomAuthException("Email already in use: " + email);
        }
    }

    @Transactional
    public AuthResponse login(@Valid LoginRequest request) {
        Optional<AuthUser> authUserOptional = authUserDao.findByUsernameWithRoles(request.getEmail());
        if (authUserOptional.isEmpty()) {
            logger.warn("Login failed: User not found - {}", request.getEmail());
            throw new CustomAuthException("Please register before logging in. User not found: " + request.getEmail());
        }
        AuthUser authUser = authUserOptional.get();

        if (!authUser.isAccountNonLocked()) {
            logger.warn("Login failed: Account is locked for user - {}", request.getEmail());
            throw new CustomAuthException("Your account is locked. Please contact support.");
        }
        if (!authUser.isAccountNonExpired()) {
            logger.warn("Login failed: Account is expired for user - {}", request.getEmail());
            throw new CustomAuthException("Your account is expired. Please verify your email.");
        }

        validateUserCredentials(request, authUser);
        String jwtToken = jwtService.generateToken(authUser);
        String refreshToken = jwtService.generateAndSaveRefreshToken(authUser);
        AuthResponse authResponse = new AuthResponse();
        authResponse.setEmail(authUser.getUsername());
        authResponse.setToken(jwtToken);
        authResponse.setRefreshToken(refreshToken);
        authResponse.setMessage("Login successful");
        return authResponse;

    }

    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new CustomAuthException("Refresh token is required for logout");
        }
        try {
            // revoke the refresh token to invalidate the session, access token will expire
            // on its own after short time
            // it will log out from the current session, other sessions will remain active
            // until their refresh tokens are revoked or expired
            jwtService.revokeRefreshToken(refreshToken);
            logger.info("User logged out successfully");
        } catch (Exception e) {
            logger.error("Logout failed: unable to revoke refresh token", e);
            throw new CustomAuthException("Logout failed");
        }
    }

    private void validateUserCredentials(@Valid LoginRequest request, AuthUser authUser) {
        PasswordEncoder passwordEncoder = this.passwordEncoder;
        if (!passwordEncoder.matches(request.getPassword(), authUser.getPassword())) {
            logger.warn("Login failed: Invalid password for user - {}", request.getEmail());
            throw new CustomAuthException("Invalid email or password");
        }
    }

    /*
     * Generate a new refresh token using the provided refresh token
     */
    @Transactional
    public AuthResponse generateRefreshToken(String oldRefreshToken) {
        return jwtService.generateRefreshTokenWithAccessToken(oldRefreshToken);
    }
}
