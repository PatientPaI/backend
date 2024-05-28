package com.patientpal.backend.auth.dto;

import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.domain.Provider;
import com.patientpal.backend.member.domain.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SignUpRequest {
    @NotBlank
    @Length(max = 20)
    private String username;

    @NotBlank
    @Length(min = 8, max = 20)
    private String password;

    private String passwordConfirm;

    @NotBlank
    private String contact;

    @NotNull
    private Role role;

    @Builder
    public SignUpRequest(String username, String password, String passwordConfirm, String contact, Role role) {
        this.username = username;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
        this.contact = contact;
        this.role = role;
    }

    public static Member of(SignUpRequest request) {
        return Member.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .contact(request.getContact())
                .provider(Provider.LOCAL)
                .role(request.getRole())
                .build();
    }
}
