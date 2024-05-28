package com.patientpal.backend.auth.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SignInRequest {
    @Length(max = 20)
    private String username;

    @Length(min = 8, max = 20)
    private String password;

    @Builder
    public SignInRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
