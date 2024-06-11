package com.patientpal.backend.fixtures.auth;

import com.patientpal.backend.auth.dto.SignInRequest;

public class SignInRequestFixture {

    public static final String VALID_USERNAME = "validUser";
    public static final String VALID_PASSWORD = "validPassword1";

    public static final String INVALID_USERNAME = "invalidUser";
    public static final String INVALID_PASSWORD = "invalid";

    public static SignInRequest createValidSignInRequest() {
        return createSignInRequestBuilder().build();
    }

    public static SignInRequest createInvalidPasswordSignInRequest() {
        return createSignInRequestBuilder().password(INVALID_PASSWORD)
                .build();
    }

    private static SignInRequest.SignInRequestBuilder createSignInRequestBuilder() {
        return SignInRequest.builder()
                .username(VALID_USERNAME)
                .password(VALID_PASSWORD);
    }
}
