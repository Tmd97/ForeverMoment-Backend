package com.forvmom.data.entities;

import com.forvmom.data.entities.auth.AuthUser;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "application_users")
public class ApplicationUser extends NamedEntity {

    @OneToOne(optional = false)
    @JoinColumn(name = "auth_user_id", unique = true, nullable = false)
    private AuthUser authUser;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "profile_picture_url", length = 500)
    private String profilePictureUrl;

    @Column(name = "date_of_birth")
    private LocalDateTime dateOfBirth;

    @Column(name = "preferred_city", length = 50)
    private String preferredCity;

    public ApplicationUser() {
    }

    public ApplicationUser(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    public void setAuthUser(AuthUser authUser) {
        this.authUser = authUser;
    }

    public AuthUser getAuthUser() {
        return authUser;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public LocalDateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPreferredCity() {
        return preferredCity;
    }

    public void setPreferredCity(String preferredCity) {
        this.preferredCity = preferredCity;
    }
}