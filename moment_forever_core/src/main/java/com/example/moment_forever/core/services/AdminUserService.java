package com.example.moment_forever.core.services;

import com.example.moment_forever.common.errorhandler.CustomAuthException;
import com.example.moment_forever.common.errorhandler.ResourceNotFoundException;
import com.example.moment_forever.common.dto.response.AppUserResponseDto;
import com.example.moment_forever.common.dto.request.UserProfileRequestDto;
import com.example.moment_forever.core.mapper.ApplicationUserBeanMapper;
import com.example.moment_forever.data.dao.ApplicationUserDao;
import com.example.moment_forever.data.dao.auth.AuthUserDao;
import com.example.moment_forever.data.entities.ApplicationUser;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AdminUserService {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(AdminUserService.class);

    @Autowired
    private ApplicationUserDao applicationUserDao;

    @Autowired
    private AuthUserDao authUserDao;

    @Transactional(readOnly = true)
    public AppUserResponseDto getAppUserById(Long id) {
        ApplicationUser appUser = applicationUserDao.findById(id);
        if (appUser == null) {
            throw new ResourceNotFoundException("No User exist given Id exist " + id);
        }
        return ApplicationUserBeanMapper.mapEntityToDto(appUser);
    }

    @Transactional(readOnly = true)
    public AppUserResponseDto getAppUserByEmailId(String email) {
        Optional<ApplicationUser> appUser = applicationUserDao.findByEmailIgnoreCase(email);
        if (appUser.isEmpty()) {
            throw new ResourceNotFoundException("No User exist given email exist " + email);
        }
        return ApplicationUserBeanMapper.mapEntityToDto(appUser.get());
    }

    @Transactional(readOnly = true)
    public List<AppUserResponseDto> getAllAppUser() {
        List<ApplicationUser> applicationUsers = applicationUserDao.findAll();
        if (applicationUsers == null || applicationUsers.size() == 0) {
            throw new ResourceNotFoundException("users doesn't exist");
        }
        return applicationUsers.stream()
                .map(ApplicationUserBeanMapper::mapEntityToDto)
                .toList();
    }

    @Transactional
    public AppUserResponseDto updateAppUser(Long userId, UserProfileRequestDto userDto) {
        ApplicationUser existing = applicationUserDao.findById(userId);
        if (existing == null) {
            throw new ResourceNotFoundException("No such user for given Id exist " + userId);
        }
        // map only updatable fields
        ApplicationUserBeanMapper.mapDtoToEntity(userDto, existing);
        ApplicationUser res = applicationUserDao.update(existing);
        return ApplicationUserBeanMapper.mapEntityToDto(res);
    }

    //TODO: we can also add soft delete functionality here instead of hard delete, as per requirement
    /*
        * This method deletes the user account along with the associated authentication details.
     */
//    @Transactional
//    public void deleteUserAccount(Long userId) {
//        AuthUser authUser = authUserDao.findById(userId);
//        if (authUser == null) {
//            logger.warn("Delete account failed: User not found - {}", userId);
//            throw new CustomAuthException("User not found for id: " + userId);
//        }
//        authUserDao.delete(authUser);
//        logger.info("User account deleted successfully for userId: {}", userId);
//    }

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
}
