package com.example.moment_forever.data.entities.auth;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name; // "USER", "ADMIN", "SUPER_ADMIN"

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "permission_level")
    private Integer permissionLevel = 10; // Default: USER level

    @Column(name = "is_system_role")
    private boolean systemRole = false;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Role() {}

    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getPermissionLevel() {
        return permissionLevel;
    }
    public void setPermissionLevel(Integer permissionLevel) {
        this.permissionLevel = permissionLevel;
    }
    public boolean isSystemRole() {
        return systemRole;
    }
    public void setSystemRole(boolean systemRole) {
        this.systemRole = systemRole;
    }

    // Lifecycle callback
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public void setActive(boolean active) {
        isActive = active;
    }
    public boolean isActive() {
        return isActive;
    }
}