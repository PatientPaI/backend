package com.patientpal.backend.security.oauth.dto;

import com.patientpal.backend.member.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
public class Oauth2SignUpRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String name;

    @NotBlank
    private String password;

    @NotNull
    private Role role;

    @NotBlank
    private String provider;

    @NotBlank
    private String username;

    @Builder(toBuilder = true)
    public Oauth2SignUpRequest(String email, String name, String password, Role role, String provider, String username) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
        this.provider = provider;
        this.username = username;
    }

}
