package com.patientpal.backend.fixtures.auth;

import com.patientpal.backend.auth.dto.SignUpRequest;
import com.patientpal.backend.member.domain.Role;

public class SignUpRequestFixture {

    public static final String VALID_USERNAME = "validUser";
    public static final String VALID_PASSWORD = "validPassword123";
    public static final String VALID_CONTACT = "010-1234-5678";

    public static final String INVALID_PASSWORD = "invalid";

    public static SignUpRequest createSignUpRequestWithMismatchedPasswords() {
        return createSignUpRequestBuilder().passwordConfirm(INVALID_PASSWORD)
                .build();
    }

    public static SignUpRequest createUserSignUpRequest() {
        return createSignUpRequestBuilder()
                .role(Role.USER)
                .build();
    }

    public static SignUpRequest createCaregiverSignUpRequest() {
        return createSignUpRequestBuilder()
                .role(Role.CAREGIVER)
                .build();
    }

    public static SignUpRequest createAdminSignUpRequest() {
        return createSignUpRequestBuilder()
                .role(Role.ADMIN)
                .build();
    }

    private static SignUpRequest.SignUpRequestBuilder createSignUpRequestBuilder() {
        return SignUpRequest.builder()
                .username(VALID_USERNAME)
                .password(VALID_PASSWORD)
                .passwordConfirm(VALID_PASSWORD)
                .role(Role.USER);
    }
}
