package com.forvmom.security.dto;

import java.util.List;

public class AuthResponse {

    private String token;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn; // in seconds
    private Long userId;
    private String email;
    private String fullName;
    private String roles;
    private String message;
    private List<Long> roleIds;
    private List<String> roleNames;
    private String assignedBy; // Who granted this role
    private String assignedFrom; // by how (means during registration, by admin, etc.)

    public AuthResponse() {}

    public AuthResponse(String token, String refreshToken, Long userId, String email, String fullName) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public Long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRoles() { return roles; }
    public void setRoles(String roles) { this.roles = roles; }

    public void setMessage(String s) {
        this.message = s;
    }
    public String getMessage() {
        return message;
    }


    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }

    public List<String> getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(List<String> roleNames) {
        this.roleNames = roleNames;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    public String getAssignedFrom() {
        return assignedFrom;
    }

    public void setAssignedFrom(String assignedFrom) {
        this.assignedFrom = assignedFrom;
    }
}