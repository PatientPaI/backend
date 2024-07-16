package com.patientpal.backend.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.patientpal.backend.auth.dto.SignInRequest;
import com.patientpal.backend.auth.dto.TokenDto;
import com.patientpal.backend.auth.service.JwtLoginService;
import com.patientpal.backend.auth.service.JwtTokenService;
import com.patientpal.backend.common.exception.AuthenticationException;
import com.patientpal.backend.common.exception.BusinessException;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.fixtures.auth.SignInRequestFixture;
import com.patientpal.backend.fixtures.auth.TokenDtoFixture;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.domain.Role;
import com.patientpal.backend.member.service.MemberService;
import com.patientpal.backend.security.jwt.JwtTokenProvider;
import com.patientpal.backend.security.oauth.dto.Oauth2SignUpRequest;
import com.patientpal.backend.test.CommonControllerSliceTest;
import com.patientpal.backend.test.annotation.AutoKoreanDisplayName;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@AutoKoreanDisplayName
@SuppressWarnings("NonAsciiCharacters")
class AuthenticationApiV1ControllerTest extends CommonControllerSliceTest {

    @Autowired
    private JwtLoginService loginService;

    @Autowired
    private JwtTokenService tokenService;

    @Autowired
    private MemberService memberService;

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
    class 사용자가_소셜_회원가입_정보_가져올_때 {
        @Test
        @WithMockUser
        void 성공한다() throws Exception {
            // given
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("email", "test@example.com");
            session.setAttribute("name", "Test User");
            session.setAttribute("role", Role.USER);
            session.setAttribute("provider", "google");
            session.setAttribute("username", "testuser");

            Member member = Member.builder()
                    .id(1L)
                    .username("testuser")
                    .isProfilePublic(true)
                    .isCompleteProfile(true)
                    .build();

            when(memberService.getUserByUsername("testuser")).thenReturn(member);

            // when & then
            mockMvc.perform(get("/api/v1/auth/oauth2/register")
                            .session(session))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("test@example.com"))
                    .andExpect(jsonPath("$.name").value("Test User"))
                    .andExpect(jsonPath("$.role").value("USER"))
                    .andExpect(jsonPath("$.provider").value("google"))
                    .andExpect(jsonPath("$.memberId").value(1L));
        }

        @Test
        @WithMockUser
        void 사용자가_존재하지_않으면_예외가_발생한다() throws Exception {
            // given
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("username", "nonexistentuser");

            when(memberService.getUserByUsername("nonexistentuser"))
                    .thenThrow(new EntityNotFoundException(ErrorCode.MEMBER_NOT_EXIST, "Member not found with username: nonexistentuser"));

            // when & then
            mockMvc.perform(get("/api/v1/auth/oauth2/register")
                            .session(session))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> {
                        Throwable resolvedException = result.getResolvedException();
                        assert resolvedException instanceof EntityNotFoundException;
                        assert ((EntityNotFoundException) resolvedException).getErrorCode().equals(ErrorCode.MEMBER_NOT_EXIST);
                    });
        }

        @Test
        @WithMockUser
        void 유효하지_않은_사용자명_예외가_발생한다() throws Exception {
            // given
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("username", "");

            // when & then
            mockMvc.perform(get("/api/v1/auth/oauth2/register")
                            .session(session))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> {
                        Throwable resolvedException = result.getResolvedException();
                        assert resolvedException instanceof BusinessException;
                        assert ((BusinessException) resolvedException).getErrorCode().equals(ErrorCode.INVALID_USERNAME);
                    });
        }
    }

    @Nested
    class 소셜_회원가입 {

        private Oauth2SignUpRequest validSignUpRequest;
        private MockHttpSession session;

        @BeforeEach
        void setUp() {
            validSignUpRequest = new Oauth2SignUpRequest(
                    "test@example.com",
                    "Test User",
                    "password",
                    Role.USER,
                    "google",
                    "testuser"
            );

            session = new MockHttpSession();
            session.setAttribute("username", "testuser");
        }

        @Test
        void 성공한다() throws Exception {
            // Mock memberService.getUserByUsername to return a valid member
            Member member = Member.builder()
                    .id(1L)
                    .username("testuser")
                    .isProfilePublic(true)
                    .isCompleteProfile(true)
                    .role(Role.USER) // role 설정
                    .build();

            when(memberService.getUserByUsername("testuser")).thenReturn(member);

            // Mock jwtTokenProvider.createAccessToken to return a valid token
            String validToken = "validToken";
            when(jwtTokenProvider.createAccessToken(any())).thenReturn(validToken);

            // Perform the request and verify the result
            mockMvc.perform(post("/api/v1/auth/oauth2/register")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validSignUpRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.email").value("test@example.com"))
                    .andExpect(jsonPath("$.name").value("Test User"))
                    .andExpect(jsonPath("$.role").value("USER"))
                    .andExpect(jsonPath("$.provider").value("google"))
                    .andExpect(jsonPath("$.memberId").value(1L));
        }


        @Test
        void 회원이_존재하지_않으면_예외가_발생한다() throws Exception {
            // Mock memberService.getUserByUsername to return null
            when(memberService.getUserByUsername(anyString())).thenReturn(null);

            mockMvc.perform(post("/api/v1/auth/oauth2/register")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validSignUpRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> {
                        Throwable resolvedException = result.getResolvedException();
                        assert resolvedException instanceof BusinessException;
                        assert ((BusinessException) resolvedException).getErrorCode()
                                .equals(ErrorCode.MEMBER_NOT_EXIST);
                    });
        }
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
                    .andExpect(status().isFound()); // 302
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

