package com.example.moment_forever.core.services;

import com.example.moment_forever.common.errorhandler.CustomAuthException;
import com.example.moment_forever.core.dto.ApplicationUserDto;
import com.example.moment_forever.core.dto.UserProfileRequestDto;
import com.example.moment_forever.core.mapper.ApplicationUserBeanMapper;
import com.example.moment_forever.data.dao.ApplicationUserDao;
import com.example.moment_forever.data.dao.auth.AuthUserDao;
import com.example.moment_forever.data.dao.auth.RefreshTokenDao;
import com.example.moment_forever.data.entities.ApplicationUser;
import com.example.moment_forever.data.entities.auth.AuthUser;
import com.example.moment_forever.data.entities.auth.RefreshToken;
import com.example.moment_forever.security.config.PasswordConfig;
import com.example.moment_forever.security.service.JwtService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public ApplicationUserDto updateCurrentUserProfile(@Valid UserProfileRequestDto userProfileRequestDto) {
        Object o = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (o instanceof UserDetails) {
            AuthUser userDetails = (AuthUser) o;
            ApplicationUser applicationUser = applicationUserDao.findByAuthUserId(userDetails.getId()).get();

            // Update fields
            applicationUser.setFullName(userProfileRequestDto.getFullName());
            applicationUser.setEmail(userProfileRequestDto.getEmail());
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

    public ApplicationUserDto getCurrentUserProfile() {
        // Add null check
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomAuthException("No authenticated user");
        }

        Object o = authentication.getPrincipal();
        if (!(o instanceof UserDetails)) {  // Check type before casting
            throw new CustomAuthException("Invalid principal type");
        }

        AuthUser userDetails = (AuthUser) o;
        ApplicationUser applicationUser = applicationUserDao.findByAuthUserId(userDetails.getId()).get();
        return ApplicationUserBeanMapper.mapEntityToDto(applicationUser);
    }

    //delete the user profile of the currently authenticated user, this will delete the authUser and cascade delete the applicationUser as well
    public void deleteCurrentUserProfile(String password) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomAuthException("No authenticated user");
        }

        Object o = authentication.getPrincipal();
        if (!(o instanceof UserDetails)) {  // Check type before casting
            throw new CustomAuthException("Invalid principal type");
        }

        AuthUser userDetails = (AuthUser) o;
        ApplicationUser applicationUser = applicationUserDao.findByAuthUserId(userDetails.getId()).get();

        // Verify password before deletion
        if (!passwordEncoder.passwordEncoder().matches(password, userDetails.getPassword())) {
            throw new CustomAuthException("Invalid password");
        }
        authUserDao.delete(applicationUser.getAuthUser());
    }

    @Transactional
    public void deactivateCurrentAccount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AuthUser authUser = (AuthUser) auth.getPrincipal();

        authUser.setAccountNonLocked(false);
        authUserDao.update(authUser);

        // Revoke all refresh tokens for this user
        List<RefreshToken> tokens = refreshTokenDao
                .findByAuthUserIdAndRevokedFalse(authUser.getId());
        tokens.forEach(token -> {
            token.setRevoked(true);
            refreshTokenDao.save(token);
        });
        logger.info("User account deActivated successfully for userId: {}",authUser.getUsername());
    }
}
