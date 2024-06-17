package com.patientpal.backend.fixtures.auth;

import com.patientpal.backend.auth.dto.AccessTokenResponse;

public class AccessTokenResponseFixture {

    public static final String VALID_ACCESS_TOKEN = "validAccessToken";

    public static final String INVALID_ACCESS_TOKEN = "invalidAccessToken";

    public static AccessTokenResponse createValidAccessTokenResponse() {
        return new AccessTokenResponse(VALID_ACCESS_TOKEN);
    }

    public static AccessTokenResponse createInvalidAccessTokenResponse() {
        return new AccessTokenResponse(INVALID_ACCESS_TOKEN);
    }
}
