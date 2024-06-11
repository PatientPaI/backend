package com.patientpal.backend.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.patientpal.backend.auth.dto.SignInRequest;
import com.patientpal.backend.auth.dto.TokenDto;
import com.patientpal.backend.fixtures.auth.SignInRequestFixture;
import com.patientpal.backend.fixtures.auth.TokenDtoFixture;
import com.patientpal.backend.test.annotation.AutoKoreanDisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;

@AutoKoreanDisplayName
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
class JwtLoginServiceTest {

    @Mock
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenService tokenService;

    @InjectMocks
    private JwtLoginService loginService;

    @BeforeEach
    void setUp() {
        when(authenticationManagerBuilder.getObject()).thenReturn(authenticationManager);
    }

    @Test
    void 인증에_성공한다() {
        // given
        SignInRequest request = SignInRequestFixture.createValidSignInRequest();
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        Authentication authentication = mock(Authentication.class);
        TokenDto expectedTokenDto = TokenDtoFixture.createValidTokenDto();

        when(authenticationManager.authenticate(token)).thenReturn(authentication);
        when(tokenService.generateJwtTokens(request.getUsername(), authentication)).thenReturn(expectedTokenDto);

        // when
        TokenDto actualTokenDto = loginService.authenticateUser(request);

        // then
        assertThat(actualTokenDto).isNotNull();
        assertThat(expectedTokenDto.accessToken()).isEqualTo(actualTokenDto.accessToken());
        assertThat(expectedTokenDto.refreshToken()).isEqualTo(actualTokenDto.refreshToken());

        verify(authenticationManager).authenticate(token);
        verify(tokenService).generateJwtTokens(request.getUsername(), authentication);
    }

    @Test
    void 인증에_실패하면_예외가_발생한다() {
        // given
        SignInRequest request = SignInRequestFixture.createInvalidPasswordSignInRequest();
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());

        doThrow(new RuntimeException()).when(authenticationManager).authenticate(token);

        // when & then
        assertThatThrownBy(() -> loginService.authenticateUser(request))
                .isInstanceOf(RuntimeException.class);

        verify(authenticationManager).authenticate(token);
    }
}
