package com.patientpal.backend.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.patientpal.backend.auth.dto.SignInRequest;
import com.patientpal.backend.auth.dto.TokenDto;
import com.patientpal.backend.fixtures.auth.SignInRequestFixture;
import com.patientpal.backend.fixtures.auth.TokenDtoFixture;
import com.patientpal.backend.test.annotation.AutoKoreanDisplayName;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;



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

    @Mock
    private LoginService loginService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private JwtLoginService jwtLoginService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtLoginService, "googleClientId", "google-client-id");
        ReflectionTestUtils.setField(jwtLoginService, "googleClientSecret", "google-client-secret");
        ReflectionTestUtils.setField(jwtLoginService, "googleRedirectUri",
                "http://localhost:8080/login/oauth2/code/google");
        ReflectionTestUtils.setField(jwtLoginService, "googleTokenUri", "https://oauth2.googleapis.com/token");

        ReflectionTestUtils.setField(jwtLoginService, "kakaoClientId", "kakao-client-id");
        ReflectionTestUtils.setField(jwtLoginService, "kakaoClientSecret", "kakao-client-secret");
        ReflectionTestUtils.setField(jwtLoginService, "kakaoRedirectUri", "http://localhost:8080/login/oauth2/code/kakao");
        ReflectionTestUtils.setField(jwtLoginService, "kakaoTokenUri", "https://kauth.kakao.com/oauth/token");

        ReflectionTestUtils.setField(jwtLoginService, "naverClientId", "naver-client-id");
        ReflectionTestUtils.setField(jwtLoginService, "naverClientSecret", "naver-client-secret");
        ReflectionTestUtils.setField(jwtLoginService, "naverRedirectUri", "http://localhost:8080/login/oauth2/code/naver");
        ReflectionTestUtils.setField(jwtLoginService, "naverTokenUri", "https://nid.naver.com/oauth2.0/token");
    }

    @Test
    void 인증에_성공한다() {
        // given
        when(authenticationManagerBuilder.getObject()).thenReturn(authenticationManager);
        SignInRequest request = SignInRequestFixture.createValidSignInRequest();
        UserDetails userDetails = new User(request.getUsername(), request.getPassword(),
                List.of(new SimpleGrantedAuthority("USER")));
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(request.getUsername(),
                request.getPassword(), userDetails.getAuthorities());
        Authentication authentication = mock(Authentication.class);
        TokenDto expectedTokenDto = TokenDtoFixture.createValidTokenDto();

        when(loginService.loadUserByUsername(request.getUsername())).thenReturn(userDetails);
        when(authenticationManager.authenticate(token)).thenReturn(authentication);
        when(tokenService.generateJwtTokens(request.getUsername(), authentication)).thenReturn(expectedTokenDto);

        // when
        TokenDto actualTokenDto = jwtLoginService.authenticateUser(request);

        // then
        assertThat(actualTokenDto).isNotNull();
        assertThat(expectedTokenDto.accessToken()).isEqualTo(actualTokenDto.accessToken());
        assertThat(expectedTokenDto.refreshToken()).isEqualTo(actualTokenDto.refreshToken());

        verify(loginService).loadUserByUsername(request.getUsername());
        verify(authenticationManager).authenticate(token);
        verify(tokenService).generateJwtTokens(request.getUsername(), authentication);
    }

    @Test
    void 인증에_실패하면_예외가_발생한다() {
        // given
        when(authenticationManagerBuilder.getObject()).thenReturn(authenticationManager);
        SignInRequest request = SignInRequestFixture.createInvalidPasswordSignInRequest();
        UserDetails userDetails = new User(request.getUsername(), request.getPassword(),
                List.of(new SimpleGrantedAuthority("USER")));
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(request.getUsername(),
                request.getPassword(), userDetails.getAuthorities());

        when(loginService.loadUserByUsername(request.getUsername())).thenReturn(userDetails);
        doThrow(new RuntimeException()).when(authenticationManager).authenticate(token);

        // when & then
        assertThatThrownBy(() -> jwtLoginService.authenticateUser(request))
                .isInstanceOf(RuntimeException.class);

        verify(loginService).loadUserByUsername(request.getUsername());
        verify(authenticationManager).authenticate(token);
    }

    @Nested
    class 엑세스_토큰 {
        @Test
        void 구글_엑세스_토큰을_정상적으로_가져온다() {
            // given
            String code = "google-auth-code";
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("access_token", "google-access-token");
            responseBody.put("refresh_token", "google-refresh-token");

            ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

            doReturn(responseEntity).when(restTemplate).exchange(
                    eq("https://oauth2.googleapis.com/token"),
                    eq(HttpMethod.POST),
                    argThat(httpEntity -> {
                        String body = (String) httpEntity.getBody();
                        return body != null && body.contains("code=" + code) &&
                                body.contains("client_id=google-client-id") &&
                                body.contains("client_secret=google-client-secret") &&
                                body.contains("redirect_uri=http://localhost:8080/login/oauth2/code/google") &&
                                body.contains("grant_type=authorization_code");
                    }),
                    eq(Map.class)
            );

            // When
            TokenDto result = jwtLoginService.getGoogleAccessToken(code);

            // Then
            assertEquals("google-access-token", result.accessToken());
            assertEquals("google-refresh-token", result.refreshToken());

            verify(restTemplate).exchange(
                    eq("https://oauth2.googleapis.com/token"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(Map.class));
        }

        @Test
        void 카카오_엑세스_토큰을_정상적으로_가져온다() {
            // Given
            String code = "kakao-auth-code";
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("access_token", "kakao-access-token");
            responseBody.put("refresh_token", "kakao-refresh-token");

            ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

            when(restTemplate.exchange(
                    eq("https://kauth.kakao.com/oauth/token"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(Map.class)
            )).thenReturn(responseEntity);

            // When
            TokenDto result = jwtLoginService.getKakaoAccessTokenFromCode(code);

            // Then
            assertEquals("kakao-access-token", result.accessToken());
            assertEquals("kakao-refresh-token", result.refreshToken());

            verify(restTemplate).exchange(
                    eq("https://kauth.kakao.com/oauth/token"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(Map.class)
            );
        }

        @Test
        void 네이버_엑세스_토큰을_정상적으로_가져온다() {
            // Given
            String code = "naver-auth-code";
            String state = "naver-state";
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("access_token", "naver-access-token");
            responseBody.put("refresh_token", "naver-refresh-token");

            ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

            when(restTemplate.exchange(
                    eq("https://nid.naver.com/oauth2.0/token"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(Map.class)
            )).thenReturn(responseEntity);

            // When
            TokenDto result = jwtLoginService.getNaverAccessTokenFromCode(code, state);

            // Then
            assertEquals("naver-access-token", result.accessToken());
            assertEquals("naver-refresh-token", result.refreshToken());

            verify(restTemplate).exchange(
                    eq("https://nid.naver.com/oauth2.0/token"),
                    eq(HttpMethod.POST),
                    any(HttpEntity.class),
                    eq(Map.class)
            );
        }
    }

}


