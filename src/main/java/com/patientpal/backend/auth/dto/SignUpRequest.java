package com.patientpal.backend.auth.dto;

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

    @NotNull
    private Role role;

    @Builder
    public SignUpRequest(String username, String password, String passwordConfirm, Role role) {
        this.username = username;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
        this.role = role;
    }
}
