package com.example.moment_forever.security.dto;
import com.example.moment_forever.data.entities.auth.AuthUser;

public class AuthBeanMapper {

    public static AuthUser mapDtoToEntity(RegisterRequestDto registerRequest) {
        AuthUser authUser= new AuthUser();
        authUser.setUsername(registerRequest.getEmail());
        authUser.setPassword(registerRequest.getPassword());
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        return authUser;

    }

     public static void mapEntityToDto(AuthUser authUser) {
        RegisterRequestDto registerRequest = new RegisterRequestDto();
        registerRequest.setEmail(authUser.getUsername());
        registerRequest.setPassword(authUser.getPassword());

    }
}
