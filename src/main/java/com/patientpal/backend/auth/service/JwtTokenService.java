package com.patientpal.backend.auth.service;

import com.patientpal.backend.auth.dto.TokenDto;
import com.patientpal.backend.common.exception.AuthenticationException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtTokenService {
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    public TokenDto refreshJwtTokens(String refreshToken) {
        validateRefreshToken(refreshToken);
        var authentication = tokenProvider.getAuthentication(refreshToken);
        return generateJwtTokens(authentication.getName(), authentication);
    }

    public TokenDto generateJwtTokens(String username, Authentication authentication) {
        String accessToken = tokenProvider.createAccessToken(authentication);
        String refreshToken = createAndSaveRefreshToken(username, authentication);
        return new TokenDto(accessToken, refreshToken);
    }

    private String createAndSaveRefreshToken(String username, Authentication authentication) {
        String refreshToken = tokenProvider.createRefreshToken(authentication);

        refreshTokenService.deleteTokenByUsername(username);
        refreshTokenService.save(username, refreshToken);

        return refreshToken;
    }

    private void validateRefreshToken(String token) {
        if (!refreshTokenService.validateToken(token)) {
            throw new AuthenticationException(ErrorCode.INVALID_TOKEN);
        }
    }
}
