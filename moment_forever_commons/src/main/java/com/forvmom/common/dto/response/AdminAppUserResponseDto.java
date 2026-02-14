package com.forvmom.common.dto.response;

import java.util.List;

public class AdminAppUserResponseDto extends AppUserResponseDto {

    private List<String> roles;
    private Long authUserId;
    private Boolean enabled;
    private Boolean accountNonLocked;
    private String createdBy;

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Long getAuthUserId() {
        return authUserId;
    }

    public void setAuthUserId(Long authUserId) {
        this.authUserId = authUserId;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    private String username;
    private Boolean accountNonExpired;
    private Boolean credentialsNonExpired;
    private Long externalUserId;
    private java.time.LocalDateTime authCreatedAt;
    private java.time.LocalDateTime authLastLogin;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(Boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public Boolean getCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public Long getExternalUserId() {
        return externalUserId;
    }

    public void setExternalUserId(Long externalUserId) {
        this.externalUserId = externalUserId;
    }

    public java.time.LocalDateTime getAuthCreatedAt() {
        return authCreatedAt;
    }

    public void setAuthCreatedAt(java.time.LocalDateTime authCreatedAt) {
        this.authCreatedAt = authCreatedAt;
    }

    public java.time.LocalDateTime getAuthLastLogin() {
        return authLastLogin;
    }

    public void setAuthLastLogin(java.time.LocalDateTime authLastLogin) {
        this.authLastLogin = authLastLogin;
    }

    public Boolean getAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(Boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }


    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
