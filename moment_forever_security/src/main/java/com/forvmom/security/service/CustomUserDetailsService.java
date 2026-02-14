package com.forvmom.security.service;

import com.forvmom.data.dao.auth.AuthUserDao;
import com.forvmom.data.entities.auth.AuthUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    @Autowired
    private AuthUserDao authUserDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Use YOUR AuthUserDao (not Spring Data JPA)
        AuthUser authUser = authUserDao.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // AuthUser already implements UserDetails interface
        // So we just return it
        return authUser;
    }

    public UserDetails loadUserByUsernameWithRoles(String username) throws UsernameNotFoundException {
        // If your AuthUserDao has a method like findByUsernameWithRoles()
        // use it here for better performance
        return loadUserByUsername(username); // Default implementation
    }

    /**
     * Check if user exists (for registration validation)
     */
    public boolean userExists(String username) {
        return authUserDao.findByUsername(username).isPresent();
    }
}