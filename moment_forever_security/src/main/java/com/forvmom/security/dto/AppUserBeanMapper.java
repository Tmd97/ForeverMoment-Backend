package com.forvmom.security.dto;

import com.forvmom.data.entities.ApplicationUser;

public class AppUserBeanMapper {

    public static ApplicationUser mapDtoToEntity(RegisterRequestDto request) {
        ApplicationUser applicationUser=new ApplicationUser();
        applicationUser.setEmail(request.getEmail().toLowerCase().trim());
        applicationUser.setFullName(request.getFullName());
        applicationUser.setPhoneNumber(request.getPhoneNumber());
        applicationUser.setPreferredCity(request.getPreferredCity());
        return applicationUser;
    }

}
