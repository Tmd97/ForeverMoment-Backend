package com.example.moment_forever.core.services;

import com.example.moment_forever.common.dto.response.AdminAppUserResponseDto;
import com.example.moment_forever.common.dto.response.RoleResponseDto;
import com.example.moment_forever.common.errorhandler.CustomAuthException;
import com.example.moment_forever.common.errorhandler.ResourceNotFoundException;
import com.example.moment_forever.common.dto.request.UserProfileRequestDto;
import com.example.moment_forever.core.mapper.ApplicationUserBeanMapper;
import com.example.moment_forever.core.mapper.RoleBeanMapper;
import com.example.moment_forever.data.dao.ApplicationUserDao;
import com.example.moment_forever.data.dao.auth.AuthUserDao;
import com.example.moment_forever.data.dao.auth.AuthUserRoleDao;
import com.example.moment_forever.data.entities.ApplicationUser;
import com.example.moment_forever.data.entities.auth.AuthUserRole;
import com.example.moment_forever.data.entities.auth.Role;
import com.example.moment_forever.security.dto.AuthResponse;
import com.example.moment_forever.security.dto.RegisterRequestDto;
import com.example.moment_forever.security.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminUserService {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserService.class);

    @Autowired
    private ApplicationUserDao applicationUserDao;

    @Autowired
    private AuthUserDao authUserDao;

    @Autowired
    private AuthUserRoleDao authUserRoleDao;

    @Autowired
    private AuthService authService;

    @Transactional
    public AdminAppUserResponseDto createUser(RegisterRequestDto request) {
        AuthResponse authResponse = authService.register(request);
        // fetch the application user details using the auth user id
        Optional<ApplicationUser> appUser = applicationUserDao.findByAuthUserId(authResponse.getUserId());
        if (appUser.isEmpty()) {
            throw new ResourceNotFoundException(
                    "User created but profile not found for auth id: " + authResponse.getUserId());
        }
        AdminAppUserResponseDto res = ApplicationUserBeanMapper.mapEntityToAdminDto(appUser.get());
        // do populate whatever required from auth response(afermath)
        res.setCreatedBy(authResponse.getAssignedBy());
        return res;
    }

    @Transactional(readOnly = true)
    public AdminAppUserResponseDto getAppUserById(Long id) {
        ApplicationUser appUser = applicationUserDao.findByIdWithAuthAndRoles(id)
                .orElseThrow(() -> new ResourceNotFoundException("No User exist given Id exist " + id));
        return ApplicationUserBeanMapper.mapEntityToAdminDto(appUser);
    }

    @Transactional(readOnly = true)
    public AdminAppUserResponseDto getAppUserByEmailId(String email) {
        Optional<ApplicationUser> appUser = applicationUserDao.findByEmailIgnoreCase(email);
        if (appUser.isEmpty()) {
            throw new ResourceNotFoundException("No User exist given email exist " + email);
        }
        return ApplicationUserBeanMapper.mapEntityToAdminDto(appUser.get());
    }

    @Transactional(readOnly = true)
    public List<AdminAppUserResponseDto> getAllAppUser() {
        // Use optimized query to fetch All Users + Auth + Roles
        List<ApplicationUser> applicationUsers = applicationUserDao.findAllWithAuthAndRoles();
        if (applicationUsers == null || applicationUsers.isEmpty()) {
            throw new ResourceNotFoundException("users doesn't exist");
        }
        return applicationUsers.stream()
                .map(ApplicationUserBeanMapper::mapEntityToAdminDto)
                .toList();
    }

    @Transactional
    public AdminAppUserResponseDto updateAppUser(Long userId,
                                                 UserProfileRequestDto userDto) {
        ApplicationUser existing = applicationUserDao.findById(userId);
        if (existing == null) {
            throw new ResourceNotFoundException("No such user for given Id exist " + userId);
        }
        // map only updatable fields
        ApplicationUserBeanMapper.mapDtoToEntity(userDto, existing);
        ApplicationUser res = applicationUserDao.update(existing);
        return ApplicationUserBeanMapper.mapEntityToAdminDto(res);
    }

    // TODO: we can also add soft delete functionality here instead of hard delete,
    // as per requirement
    /*
     * This method deletes the user account along with the associated authentication
     * details.
     */
    // @Transactional
    // public void deleteUserAccount(Long userId) {
    // AuthUser authUser = authUserDao.findById(userId);
    // if (authUser == null) {
    // logger.warn("Delete account failed: User not found - {}", userId);
    // throw new CustomAuthException("User not found for id: " + userId);
    // }
    // authUserDao.delete(authUser);
    // logger.info("User account deleted successfully for userId: {}", userId);
    // }

    @Transactional
    public void deleteUserProfile(Long userId) {
        ApplicationUser userProfile = applicationUserDao.findById(userId);
        if (userProfile == null) {
            logger.warn("Delete profile failed: AppUser not found - {}", userId);
            throw new CustomAuthException("AppUser not found for id: " + userId);
        }
        applicationUserDao.delete(userProfile);
        logger.info("User Profile deleted successfully for userId: {}", userId);
    }

    @Transactional
    public void deleteAccount(Long userId) {
        ApplicationUser userProfile = applicationUserDao.findById(userId);
        if (userProfile == null) {
            logger.warn("Delete Account failed: AppUser not found - {}", userId);
            throw new CustomAuthException("AppUser not found for id: " + userId);
        }
        applicationUserDao.deleteByAppUserId(userProfile.getAuthUser().getId());
        logger.info("User Account deleted successfully for userId: {}", userId);
    }

    @Transactional
    public List<RoleResponseDto> getUserRoles(Long userId) {
        List<AuthUserRole> authUserRoles = authUserRoleDao.findByAuthUserId(userId);
        if (authUserRoles == null || authUserRoles.isEmpty()) {
            throw new ResourceNotFoundException("No roles found for user id: " + userId);
        }
        List<RoleResponseDto> responseDtos = authUserRoles.stream()
                .map(authUserRole -> authUserRole.getRole())
                .map(RoleBeanMapper::mapEntityToDto).collect(Collectors.toList());

        return responseDtos;
    }
}
