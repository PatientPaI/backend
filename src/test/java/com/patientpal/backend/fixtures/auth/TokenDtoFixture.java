package com.patientpal.backend.fixtures.auth;

import com.patientpal.backend.auth.dto.TokenDto;

public class TokenDtoFixture {

    public static final String VALID_ACCESS_TOKEN = "validAccessToken";
    public static final String VALID_REFRESH_TOKEN = "validRefreshToken";

    public static final String INVALID_ACCESS_TOKEN = "invalidAccessToken";
    public static final String INVALID_REFRESH_TOKEN = "invalidRefreshToken";

    public static final String NEW_ACCESS_TOKEN = "newAccessToken";
    public static final String NEW_REFRESH_TOKEN = "newRefreshToken";

    public static TokenDto createValidTokenDto() {
        return new TokenDto(VALID_ACCESS_TOKEN, VALID_REFRESH_TOKEN);
    }

    public static TokenDto createNewTokenDto() {
        return new TokenDto(NEW_ACCESS_TOKEN, NEW_REFRESH_TOKEN);
    }
}
