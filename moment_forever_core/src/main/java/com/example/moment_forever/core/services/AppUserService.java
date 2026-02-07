package com.example.moment_forever.core.services;

import com.example.moment_forever.common.errorhandler.ResourceNotFoundException;
import com.example.moment_forever.common.utils.AppConstants;
import com.example.moment_forever.core.dto.ApplicationUserDto;
import com.example.moment_forever.core.dto.CategoryDto;
import com.example.moment_forever.core.dto.SubCategoryDto;
import com.example.moment_forever.core.mapper.ApplicationUserBeanMapper;
import com.example.moment_forever.core.mapper.CategoryBeanMapper;
import com.example.moment_forever.core.mapper.SubCategoryBeanMapper;
import com.example.moment_forever.data.dao.ApplicationUserDao;
import com.example.moment_forever.data.dao.CategoryDao;
import com.example.moment_forever.data.dao.auth.AuthUserDao;
import com.example.moment_forever.data.entities.ApplicationUser;
import com.example.moment_forever.data.entities.Category;
import com.example.moment_forever.data.entities.SubCategory;
import com.example.moment_forever.data.entities.auth.AuthUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AppUserService {

    @Autowired
    private ApplicationUserDao applicationUserDao;

    @Autowired
    private AuthUserDao authUserDao;


//    @Transactional
//    public ApplicationUserDto createUserProfile(ApplicationUserDto userDto) {
//        if (applicationUserDao.existsByUsername(userDto.getUsername())) {
//            throw new IllegalArgumentException("User with username '" + userDto.getUsername() + "' already exists");
//        }
//        ApplicationUser appUser = new ApplicationUser();
//        ApplicationUserBeanMapper.mapDtoToEntity(userDto, appUser);
//        applicationUserDao.save(appUser);
//        return ApplicationUserBeanMapper.mapEntityToDto(appUser);
//    }

    @Transactional(readOnly = true)
    public ApplicationUserDto getAppUserById(Long id) {
        ApplicationUser appUser = applicationUserDao.findById(id);
        if (appUser == null) {
            throw new ResourceNotFoundException("No User exist given Id exist " + id);
        }
        return ApplicationUserBeanMapper.mapEntityToDto(appUser);
    }

    @Transactional(readOnly = true)
    public ApplicationUserDto getAppUserByEmailId(String email) {
        Optional<ApplicationUser> appUser = applicationUserDao.findByEmailIgnoreCase(email);
        if (appUser.isEmpty()) {
            throw new ResourceNotFoundException("No User exist given email exist " + email);
        }
        return ApplicationUserBeanMapper.mapEntityToDto(appUser.get());
    }

    @Transactional(readOnly = true)
    public List<ApplicationUserDto> getAllProfiles() {
        List<ApplicationUser> applicationUsers = applicationUserDao.findAll();
        if (applicationUsers == null || applicationUsers.size() == 0) {
            throw new ResourceNotFoundException("users doesn't exist");
        }
        return applicationUsers.stream()
                .map(ApplicationUserBeanMapper::mapEntityToDto)
                .toList();
    }

    @Transactional
    public boolean deleteCategory(Long id) {
        ApplicationUser existing = applicationUserDao.findById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("No such user for given Id exist " + id);
        }
        applicationUserDao.delete(existing);
        return true;
    }

    public ApplicationUserDto updateUserProfile(Long userId, ApplicationUserDto userDto) {
        ApplicationUser existing = applicationUserDao.findById(userId);
        if (existing == null) {
            throw new ResourceNotFoundException("No such user for given Id exist " + userId);
        }
        // map only updatable fields
        ApplicationUserBeanMapper.mapDtoToEntity(userDto, existing);
        ApplicationUser res = applicationUserDao.update(existing);
        return ApplicationUserBeanMapper.mapEntityToDto(res);
    }

    public void deleteUser(Long userId) {
        ApplicationUser existing = applicationUserDao.findById(userId);
        if (existing == null) {
            throw new ResourceNotFoundException("No such user for given Id exist " + userId);
        }
        applicationUserDao.delete(existing);
    }


    public ApplicationUserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object o = authentication.getPrincipal();
        UserDetails authUser;
        if (o instanceof UserDetails) {
            authUser = (UserDetails) o;
            String email = authUser.getUsername();
            Optional<AuthUser> authUserOptional = authUserDao.findByUsername(email);
            if (authUserOptional.isEmpty()) {
                throw new ResourceNotFoundException("Current authenticated user not found in AuthUser table");
            }
            ApplicationUser applicationUser = applicationUserDao.findById(authUserOptional.get().getId());

            return ApplicationUserBeanMapper.mapEntityToDto(applicationUser);
        }
        throw new ResourceNotFoundException("Current authenticated user details not found");
    }
}
