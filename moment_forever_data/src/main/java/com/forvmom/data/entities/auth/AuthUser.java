package com.forvmom.data.entities.auth;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "auth_users")
public class AuthUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 150)
    private String username; // email

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @Column(name = "account_non_expired", nullable = false)
    private boolean accountNonExpired = true;

    @Column(name = "account_non_locked", nullable = false)
    private boolean accountNonLocked = true;

    @Column(name = "credentials_non_expired", nullable = false)
    private boolean credentialsNonExpired = true;

    @Column(name = "external_user_id", unique = true)
    private Long externalUserId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @OneToMany(mappedBy = "authUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<AuthUserRole> userRoles = new HashSet<>();

    public AuthUser() {
    }

    public AuthUser(String username, String encodedPassword) {
        this.username = username;
        this.password = encodedPassword;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public Long getExternalUserId() {
        return externalUserId;
    }

    public void setExternalUserId(Long externalUserId) {
        this.externalUserId = externalUserId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setUserRoles(Set<AuthUserRole> userRoles) {
        this.userRoles = userRoles;
    }

    public Set<AuthUserRole> getUserRoles() {
        return userRoles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (AuthUserRole userRole : userRoles) {
            Role role = userRole.getRole();
            if (role != null) {
                // Convert role name to Spring Security authority format
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            }
        }

        return authorities;
    }

    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    public boolean hasRole(String roleName) {
        for (AuthUserRole userRole : userRoles) {
            Role role = userRole.getRole();
            if (role != null && role.getName().equalsIgnoreCase(roleName)) {
                return true;
            }
        }
        return false;
    }

    // we prepopulated the ROLE (ADMIN, USER, etc.) inside ROLE entity via data seeder
    public void addRole(Role role) {
        AuthUserRole authUserRole = new AuthUserRole();
        authUserRole.setRole(role);
        authUserRole.setAuthUser(this);
        this.getUserRoles().add(authUserRole);
    }

    public void removeRole(Role role) {
        userRoles.removeIf(userRole ->
                userRole.getRole() != null &&
                        userRole.getRole().getId().equals(role.getId())
        );
    }


    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}