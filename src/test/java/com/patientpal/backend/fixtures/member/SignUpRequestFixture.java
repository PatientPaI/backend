package com.patientpal.backend.fixtures.member;

import com.patientpal.backend.auth.dto.SignUpRequest;
import com.patientpal.backend.member.domain.Role;

public class SignUpRequestFixture {
    public static SignUpRequest createValidSignUpRequest() {
        return SignUpRequest.builder()
                .username("validUser")
                .password("validPassword123")
                .passwordConfirm("validPassword123")
                .contact("validContact@example.com")
                .role(Role.USER)
                .build();
    }

    public static SignUpRequest createSignUpRequestWithMismatchedPasswords() {
        return SignUpRequest.builder()
                .username("validUser")
                .password("validPassword123")
                .passwordConfirm("differentPassword")
                .contact("validContact@example.com")
                .role(Role.USER)
                .build();
    }
}
