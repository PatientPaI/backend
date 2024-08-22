package com.patientpal.backend.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.patientpal.backend.auth.dto.SignInRequest;
import com.patientpal.backend.auth.dto.TokenDto;
import com.patientpal.backend.auth.service.JwtLoginService;
import com.patientpal.backend.auth.service.JwtTokenService;
import com.patientpal.backend.common.exception.AuthenticationException;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.fixtures.auth.SignInRequestFixture;
import com.patientpal.backend.fixtures.auth.TokenDtoFixture;
import com.patientpal.backend.security.jwt.JwtTokenProvider;
import com.patientpal.backend.test.CommonControllerSliceTest;
import com.patientpal.backend.test.annotation.AutoKoreanDisplayName;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;


@AutoKoreanDisplayName
@SuppressWarnings("NonAsciiCharacters")
class AuthenticationApiV1ControllerTest extends CommonControllerSliceTest {

    @Autowired
    private JwtLoginService loginService;

    @Autowired
    private JwtTokenService tokenService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Nested
    class 사용자가_로그인_시에 {

        @Test
        void 성공한다() throws Exception {
            // given
            SignInRequest request = SignInRequestFixture.createValidSignInRequest();
            TokenDto validTokens = TokenDtoFixture.createValidTokenDto();

            when(loginService.authenticateUser(any(SignInRequest.class))).thenReturn(validTokens);

            // when & then
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.access_token").value(TokenDtoFixture.VALID_ACCESS_TOKEN))
                    .andExpect(cookie().value("refresh_token", TokenDtoFixture.VALID_REFRESH_TOKEN));
        }

        @Test
        void 매치되는_정보가_없으면_예외가_발생한다() throws Exception {
            // given
            SignInRequest request = SignInRequestFixture.createInvalidPasswordSignInRequest();

            doThrow(new EntityNotFoundException(ErrorCode.MEMBER_NOT_EXIST)).when(loginService)
                    .authenticateUser(any(SignInRequest.class));

            // when & then
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.access_token").doesNotExist())
                    .andExpect(cookie().doesNotExist("refresh_token"));
        }
    }

    @Nested
    class 사용자가_리프레시_토큰_재발급_시에 {

        @Test
        @WithMockUser
        void 성공한다() throws Exception {
            // given
            TokenDto newTokens = TokenDtoFixture.createValidTokenDto();

            when(tokenService.refreshJwtTokens(TokenDtoFixture.VALID_REFRESH_TOKEN)).thenReturn(newTokens);

            // when & then
            mockMvc.perform(post("/api/v1/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .cookie(new Cookie("refresh_token", TokenDtoFixture.VALID_REFRESH_TOKEN)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.access_token").value(newTokens.accessToken()))
                    .andExpect(cookie().value("refresh_token", newTokens.refreshToken()));
        }

        @Test
        @WithMockUser
        void 유효하지_않으면_예외가_발생한다() throws Exception {
            // given
            doThrow(new AuthenticationException(ErrorCode.INVALID_TOKEN)).when(tokenService)
                    .refreshJwtTokens(TokenDtoFixture.INVALID_REFRESH_TOKEN);

            // when & then
            mockMvc.perform(post("/api/v1/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .cookie(new Cookie("refresh_token", TokenDtoFixture.INVALID_REFRESH_TOKEN)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.access_token").doesNotExist());
        }
    }

    @Nested
    class 사용자가_회원가입_시에 {
        // TODO: 회원가입 양식이 제대로 정해지면 테스트를 새로 작성해야 함
    }

    @Nested
    class 로그아웃 {

        @Test
        @WithMockUser
        void 성공한다() throws Exception {
            // given
            String validRefreshToken = "validRefreshToken";
            Cookie refreshTokenCookie = new Cookie("refresh_token", validRefreshToken);

            // when & then
            mockMvc.perform(get("/api/v1/auth/logout")
                            .cookie(refreshTokenCookie))
                    .andExpect(status().isNoContent()); // 302
                    // .andExpect(redirectedUrl("/")); // 아직 해당 페이지가 없으므로 주석 처리함

            // Verify if the token is invalidated
            verify(jwtTokenProvider, times(1)).invalidateToken(validRefreshToken);
        }

        @Test
        @WithMockUser
        void 리프레시_토큰이_없으면_예외가_발생한다() throws Exception {
            // when & then
            mockMvc.perform(get("/api/v1/auth/logout"))
                    .andExpect(status().isBadRequest());
        }
    }
}

