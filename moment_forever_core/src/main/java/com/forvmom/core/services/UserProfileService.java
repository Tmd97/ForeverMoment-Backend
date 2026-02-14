package com.forvmom.core.services;

import com.forvmom.common.errorhandler.CustomAuthException;
import com.forvmom.common.dto.response.AppUserResponseDto;
import com.forvmom.common.dto.request.UserProfileRequestDto;
import com.forvmom.core.mapper.ApplicationUserBeanMapper;
import com.forvmom.data.dao.ApplicationUserDao;
import com.forvmom.data.dao.auth.AuthUserDao;
import com.forvmom.data.dao.auth.RefreshTokenDao;
import com.forvmom.data.entities.ApplicationUser;
import com.forvmom.data.entities.auth.AuthUser;
import com.forvmom.data.entities.auth.RefreshToken;
import com.forvmom.security.config.PasswordConfig;
import com.forvmom.security.dto.JwtUserDetails;
import com.forvmom.security.service.JwtService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserProfileService {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileService.class);

    @Autowired
    private ApplicationUserDao applicationUserDao;

    @Autowired
    private AuthUserDao authUserDao;

    @Autowired
    private RefreshTokenDao refreshTokenDao;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordConfig passwordEncoder;

    @Transactional
    public AppUserResponseDto updateCurrentUserProfile(@Valid UserProfileRequestDto userProfileRequestDto) {
        Object o = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (o instanceof UserDetails) {
            JwtUserDetails jwtUserDetails = (JwtUserDetails) o;
            ApplicationUser applicationUser = applicationUserDao.findById(jwtUserDetails.getId());

            // Update fields
            applicationUser.setFullName(userProfileRequestDto.getFullName());

            // Sync email change with AuthUser
            if (!applicationUser.getEmail().equalsIgnoreCase(userProfileRequestDto.getEmail())) {
                if (authUserDao.existsByUsername(userProfileRequestDto.getEmail())) {
                    throw new CustomAuthException("Email already in use: " + userProfileRequestDto.getEmail());
                }
                AuthUser authUser = applicationUser.getAuthUser();
                authUser.setUsername(userProfileRequestDto.getEmail());
                authUserDao.save(authUser);
                applicationUser.setEmail(userProfileRequestDto.getEmail());
            }

            applicationUser.setPhoneNumber(userProfileRequestDto.getPhoneNumber());
            applicationUser.setProfilePictureUrl(userProfileRequestDto.getProfilePictureUrl());
            applicationUser.setDateOfBirth(userProfileRequestDto.getDateOfBirth());
            applicationUser.setPreferredCity(userProfileRequestDto.getPreferredCity());

            // Save updated user
            ApplicationUser updatedUser = applicationUserDao.update(applicationUser);
            return ApplicationUserBeanMapper.mapEntityToDto(updatedUser);
        }
        throw new RuntimeException("User not authenticated");
    }

    public AppUserResponseDto getCurrentUserProfile() {
        // Add null check
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomAuthException("No authenticated user");
        }

        Object o = authentication.getPrincipal();
        if (!(o instanceof UserDetails)) { // Check type before casting
            throw new CustomAuthException("Invalid principal type");
        }

        JwtUserDetails jwtUserDetails = (JwtUserDetails) o;
        ApplicationUser applicationUser = applicationUserDao.findById(jwtUserDetails.getId());
        return ApplicationUserBeanMapper.mapEntityToDto(applicationUser);
    }

    // delete the user profile of the currently authenticated user, this will delete
    // the authUser and cascade delete the applicationUser as well
    public void deleteCurrentUserProfile(String password) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomAuthException("No authenticated user");
        }

        Object o = authentication.getPrincipal();
        if (!(o instanceof UserDetails)) { // Check type before casting
            throw new CustomAuthException("Invalid principal type");
        }

        JwtUserDetails jwtUserDetails = (JwtUserDetails) o;
        ApplicationUser applicationUser = applicationUserDao.findById(jwtUserDetails.getId());

        // Verify password before deletion
        if (!passwordEncoder.passwordEncoder().matches(password, jwtUserDetails.getPassword())) {
            throw new CustomAuthException("Invalid password");
        }
        authUserDao.delete(applicationUser.getAuthUser());
    }

    @Transactional
    public void deactivateCurrentAccount(String refreshToken) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Object o = authentication.getPrincipal();
        if (!(o instanceof UserDetails)) {
            throw new CustomAuthException("Invalid principal type");
        }
        JwtUserDetails jwtUserDetails = (JwtUserDetails) o;
        ApplicationUser applicationUser = applicationUserDao.findById(jwtUserDetails.getId());
        AuthUser authUser = applicationUser.getAuthUser();
        authUser.setAccountNonLocked(false);
        authUserDao.update(authUser);

        // Revoke all refresh tokens for this user
        List<RefreshToken> tokens = refreshTokenDao
                .findByAuthUserIdAndRevokedFalse(authUser.getId());
        tokens.forEach(token -> {
            token.setRevoked(true);
            refreshTokenDao.save(token);
        });
        logger.info("User account deActivated successfully for userId: {}", authUser.getUsername());
    }
}
