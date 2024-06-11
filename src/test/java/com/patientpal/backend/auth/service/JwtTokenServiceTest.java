package com.patientpal.backend.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.patientpal.backend.auth.dto.TokenDto;
import com.patientpal.backend.common.exception.AuthenticationException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.fixtures.auth.SignInRequestFixture;
import com.patientpal.backend.fixtures.auth.TokenDtoFixture;
import com.patientpal.backend.security.jwt.JwtTokenProvider;
import com.patientpal.backend.test.annotation.AutoKoreanDisplayName;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@AutoKoreanDisplayName
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
class JwtTokenServiceTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private JwtTokenService tokenService;

    @Test
    void 리프레시_토큰이_유효하면_새_리프레시_토큰을_생성한다() {
        // given
        String username = SignInRequestFixture.VALID_USERNAME;
        String validRefreshToken = TokenDtoFixture.VALID_REFRESH_TOKEN;
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, validRefreshToken, Collections.emptyList());
        TokenDto expectedTokenDto = TokenDtoFixture.createNewTokenDto();

        when(refreshTokenService.validateToken(validRefreshToken)).thenReturn(true);
        when(tokenProvider.getAuthentication(validRefreshToken)).thenReturn(authenticationToken);
        doNothing().when(refreshTokenService).deleteTokenByUsername(any(String.class));
        when(refreshTokenService.save(any(String.class), any(String.class))).thenReturn(1L);
        when(tokenProvider.createAccessToken(any(Authentication.class))).thenReturn(TokenDtoFixture.NEW_ACCESS_TOKEN);
        when(tokenProvider.createRefreshToken(any(Authentication.class))).thenReturn(TokenDtoFixture.NEW_REFRESH_TOKEN);

        // when
        TokenDto actualTokenDto = tokenService.refreshJwtTokens(validRefreshToken);

        // then
        assertThat(actualTokenDto).isEqualTo(expectedTokenDto);

        verify(refreshTokenService).deleteTokenByUsername(username);
        verify(refreshTokenService).save(username, expectedTokenDto.refreshToken());
    }

    @Test
    void 리프레시_토큰이_유효하지_않으면_예외가_발생한다() {
        // given
        String invalidRefreshToken = TokenDtoFixture.INVALID_REFRESH_TOKEN;

        doThrow(new AuthenticationException(ErrorCode.INVALID_TOKEN)).when(refreshTokenService).validateToken(invalidRefreshToken);

        // when & then
        assertThatThrownBy(() -> tokenService.refreshJwtTokens(invalidRefreshToken))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void 토큰을_새롭게_생성한다() {
        // given
        String username = SignInRequestFixture.VALID_USERNAME;
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, "password", Collections.emptyList());
        TokenDto expectedTokenDto = TokenDtoFixture.createNewTokenDto();

        doNothing().when(refreshTokenService).deleteTokenByUsername(any(String.class));
        when(refreshTokenService.save(any(String.class), any(String.class))).thenReturn(1L);
        when(tokenProvider.createAccessToken(any(Authentication.class))).thenReturn(TokenDtoFixture.NEW_ACCESS_TOKEN);
        when(tokenProvider.createRefreshToken(any(Authentication.class))).thenReturn(TokenDtoFixture.NEW_REFRESH_TOKEN);

        // when
        TokenDto actualTokenDto = tokenService.generateJwtTokens(username, authentication);

        // then
        assertThat(actualTokenDto).isEqualTo(expectedTokenDto);
    }
}
