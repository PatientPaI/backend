package com.patientpal.backend.security.oauth.dto;

import com.patientpal.backend.member.domain.Address;
import com.patientpal.backend.member.domain.Gender;
import com.patientpal.backend.member.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Oauth2SignUpRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String name;

    @NotNull
    private Role role;

    @NotBlank
    private String provider;

    @NotBlank
    private String username;

    private Boolean isProfilePublic;
    private String profileImageUrl;
    private String contact;
    private Address address;
    private Gender gender;
    private int age;

    @Builder(toBuilder = true)
    public Oauth2SignUpRequest(String email, String name, String provider, String username,
                               Role role, Boolean isProfilePublic, String profileImageUrl,
                               String contact, Address address, Gender gender, int age) {
        this.email = email;
        this.name = name;
        this.provider = provider;
        this.username = username;
        this.role = role != null ? role : Role.USER;
        this.isProfilePublic = isProfilePublic;
        this.profileImageUrl = profileImageUrl;
        this.contact = contact;
        this.address = address;
        this.gender = gender;
        this.age = age;
    }

}
