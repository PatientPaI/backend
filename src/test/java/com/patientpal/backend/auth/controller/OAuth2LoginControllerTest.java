package com.patientpal.backend.auth.controller;


import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import com.patientpal.backend.auth.dto.TokenDto;
import com.patientpal.backend.auth.service.JwtLoginService;
import com.patientpal.backend.auth.service.SocialDataService;
import com.patientpal.backend.common.exception.BusinessException;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.fixtures.auth.SocialDataFixture;
import com.patientpal.backend.fixtures.member.MemberFixture;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.domain.Role;
import com.patientpal.backend.member.repository.MemberRepository;
import com.patientpal.backend.member.service.MemberService;
import com.patientpal.backend.security.jwt.JwtTokenProvider;
import com.patientpal.backend.security.oauth.dto.Oauth2SignUpRequest;
import com.patientpal.backend.test.CommonControllerSliceTest;
import com.patientpal.backend.test.annotation.AutoKoreanDisplayName;

@TestPropertySource(properties = {
        "spring.security.oauth2.client.registration.google.client-id=test-google-client-id",
        "spring.security.oauth2.client.registration.kakao.client-id=test-kakao-client-id",
        "spring.security.oauth2.client.registration.naver.client-id=test-naver-client-id"
})
@AutoKoreanDisplayName
@SuppressWarnings("NonAsciiCharacters")
class OAuth2LoginControllerTest extends CommonControllerSliceTest {

    @Autowired
    private MemberService memberService;

    @MockBean
    private JwtLoginService  jwtLoginService;

    @MockBean
    private SocialDataService socialDataService;

    @MockBean
    private MemberRepository memberRepository;

    @Nested
    class 소셜_로그인_리다이렉트 {
        @Test
        void 구글_로그인_페이지로_리다이렉트된다() throws Exception {
            mockMvc.perform(get("/api/v1/auth/oauth2/authorize/google"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(result -> {
                        String redirectedUrl = result.getResponse().getRedirectedUrl();
                        assertThat(redirectedUrl).contains("https://accounts.google.com/o/oauth2/auth");
                        assertThat(redirectedUrl).contains("client_id=");
                        assertThat(redirectedUrl).contains("redirect_uri=");
                        assertThat(redirectedUrl).contains("response_type=code");
                        assertThat(redirectedUrl).contains("scope=profile email");
                    });
        }

        @Test
        void 카카오_로그인_페이지로_리다이렉트된다() throws Exception {
            mockMvc.perform(get("/api/v1/auth/oauth2/authorize/kakao"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(result -> {
                        String redirectedUrl = result.getResponse().getRedirectedUrl();
                        assertThat(redirectedUrl).startsWith("https://kauth.kakao.com/oauth/authorize");
                        assertThat(redirectedUrl).contains("client_id=");
                        assertThat(redirectedUrl).contains("redirect_uri=");
                        assertThat(redirectedUrl).contains("response_type=code");
                        assertThat(redirectedUrl).contains("scope=");
                    });
        }

        @Test
        void 네이버_로그인_페이지로_리다이렉트된다() throws Exception {
            mockMvc.perform(get("/api/v1/auth/oauth2/authorize/naver"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(result -> {
                        String redirectedUrl = result.getResponse().getRedirectedUrl();
                        assertThat(redirectedUrl).startsWith("https://nid.naver.com/oauth2.0/authorize");
                        assertThat(redirectedUrl).contains("client_id=");
                        assertThat(redirectedUrl).contains("redirect_uri=");
                        assertThat(redirectedUrl).contains("response_type=code");
                        assertThat(redirectedUrl).contains("state=");
                    });
        }
    }

    @Nested
    class 소셜_엑세스_토큰_요청 {

        @Test
        void 구글_엑세스_토큰_요청_성공() throws Exception {
            String mockAuthorizationCode = "mockAuthorizationCode";
            TokenDto mockTokenDto = new TokenDto("mockAccessToken", "mockRefreshToken");

            when(jwtLoginService.getGoogleAccessToken(mockAuthorizationCode)).thenReturn(mockTokenDto);

            mockMvc.perform(post("/api/v1/auth/oauth2/token/google")
                            .param("code", mockAuthorizationCode)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("mockAccessToken"))
                    .andExpect(jsonPath("$.refreshToken").value("mockRefreshToken"));
        }

        @Test
        void 카카오_엑세스_토큰_요청_성공() throws Exception {
            String mockAuthorizationCode = "mockAuthorizationCode";
            TokenDto mockTokenDto = new TokenDto("mockAccessToken", "mockRefreshToken");

            when(jwtLoginService.getKakaoAccessTokenFromCode(mockAuthorizationCode)).thenReturn(mockTokenDto);

            mockMvc.perform(post("/api/v1/auth/oauth2/token/kakao")
                            .param("code", mockAuthorizationCode)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("mockAccessToken"))
                    .andExpect(jsonPath("$.refreshToken").value("mockRefreshToken"));
        }

        @Test
        void 네이버_엑세스_토큰_요청_성공() throws Exception {
            String mockAuthorizationCode = "mockAuthorizationCode";
            String mockState = "mockState";
            TokenDto mockTokenDto = new TokenDto("mockAccessToken", "mockRefreshToken");

            when(jwtLoginService.getNaverAccessTokenFromCode(mockAuthorizationCode, mockState)).thenReturn(mockTokenDto);

            mockMvc.perform(post("/api/v1/auth/oauth2/token/naver")
                            .param("code", mockAuthorizationCode)
                            .param("state", mockState)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("mockAccessToken"))
                    .andExpect(jsonPath("$.refreshToken").value("mockRefreshToken"));
        }
    }

    @Nested
    class 소셜_데이터_요청 {

        @Test
        void 구글_액세스_토큰으로_우저_데이터를_성공적으로_가져온다() throws Exception {
            when(socialDataService.getUserData(SocialDataFixture.MOCK_ACCESS_TOKEN, SocialDataFixture.GOOGLE_PROVIDER))
                    .thenReturn(SocialDataFixture.GOOGLE_USER_DATA);

            mockMvc.perform(get("/api/v1/auth/user/data")
                            .param("accessToken", SocialDataFixture.MOCK_ACCESS_TOKEN)
                            .param("provider", SocialDataFixture.GOOGLE_PROVIDER)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("test@example.com"))
                    .andExpect(jsonPath("$.name").value("Test User"));
        }

        @Test
        void 카카오_액세스_토큰으로_유저_데이터를_성공적으로_가져온다() throws Exception {
            when(socialDataService.getUserData(SocialDataFixture.MOCK_ACCESS_TOKEN, SocialDataFixture.KAKAO_PROVIDER))
                    .thenReturn(SocialDataFixture.KAKAO_USER_DATA);

            mockMvc.perform(get("/api/v1/auth/user/data")
                            .param("accessToken", SocialDataFixture.MOCK_ACCESS_TOKEN)
                            .param("provider", SocialDataFixture.KAKAO_PROVIDER)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("test@example.com"))
                    .andExpect(jsonPath("$.nickname").value("Test User"));
        }

        @Test
        void 네이버_액세스_토큰으로_유저_데이터를_성공적으로_가져온다() throws Exception {
            when(socialDataService.getUserData(SocialDataFixture.MOCK_ACCESS_TOKEN, SocialDataFixture.NAVER_PROVIDER))
                    .thenReturn(SocialDataFixture.NAVER_USER_DATA);

            mockMvc.perform(get("/api/v1/auth/user/data")
                            .param("accessToken", SocialDataFixture.MOCK_ACCESS_TOKEN)
                            .param("provider", SocialDataFixture.NAVER_PROVIDER)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("test@example.com"))
                    .andExpect(jsonPath("$.name").value("Test User"));
        }

        @Test
        void 유효하지_않은_액세스_토큰_또는_프로바이더일_때_예외가_발생한다() throws Exception {
            when(socialDataService.getUserData(SocialDataFixture.MOCK_ACCESS_TOKEN, SocialDataFixture.GOOGLE_PROVIDER))
                    .thenThrow(new RuntimeException("Failed to fetch user data from " + SocialDataFixture.GOOGLE_PROVIDER));

            mockMvc.perform(get("/api/v1/auth/user/data")
                            .param("accessToken", SocialDataFixture.MOCK_ACCESS_TOKEN)
                            .param("provider", SocialDataFixture.GOOGLE_PROVIDER)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
        }
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
            session.setAttribute("age", 70);
            session.setAttribute("provider", "google");
            session.setAttribute("username", "testuser");

            Member member = Member.builder()
                    .id(1L)
                    .username("testuser")
                    .age(80)
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
    class 소셜_회원가입_또는_로그인 {

        private MockHttpSession session;

        @BeforeEach
        void setUp() {
            this.session = new MockHttpSession();
            session.setAttribute("username", MemberFixture.DEFAULT_USERNAME);
        }

        @Test
        void 회원이_존재하면_로그인에_성공한다() throws Exception {
            //given
            Member existingMember = MemberFixture.createDefaultMember();

            when(memberService.getUserByUsername(MemberFixture.DEFAULT_USERNAME)).thenReturn(existingMember);


            String validToken = "validToken";
            when(jwtTokenProvider.createAccessToken(any())).thenReturn(validToken);

            Oauth2SignUpRequest validSignUpRequest = new Oauth2SignUpRequest(
                    "test@example.com",
                    "Test User",
                    "password",
                    Role.USER,
                    "google",
                    MemberFixture.DEFAULT_USERNAME
            );

            // when & then
            mockMvc.perform(post("/api/v1/auth/oauth2/register-or-login")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validSignUpRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("test@example.com"))
                    .andExpect(jsonPath("$.name").value("Test User"))
                    .andExpect(jsonPath("$.role").value("USER"))
                    .andExpect(jsonPath("$.provider").value("google"))
                    .andExpect(jsonPath("$.memberId").value(MemberFixture.DEFAULT_ID));
        }


        @Test
        void 회원이_존재하지_않으면_회원가입에_성공한다() throws Exception {
            // given
            when(memberService.getUserByUsername(MemberFixture.DEFAULT_USERNAME)).thenReturn(null);

            Long newMemberId = MemberFixture.DEFAULT_ID;
            when(memberService.saveSocialUser(any())).thenReturn(newMemberId);

            Member newMember = MemberFixture.defaultRolePatient();
            when(memberService.getUserById(newMemberId)).thenReturn(newMember);

            String validToken = "validToken";
            when(jwtTokenProvider.createAccessToken(any())).thenReturn(validToken);

            Oauth2SignUpRequest validSignUpRequest = new Oauth2SignUpRequest(
                    "test@example.com",
                    "Test User",
                    "password",
                    Role.USER,
                    "google",
                    MemberFixture.DEFAULT_USERNAME
            );

            // when & then
            mockMvc.perform(post("/api/v1/auth/oauth2/register-or-login")
                            .session(session)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validSignUpRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.email").value("test@example.com"))
                    .andExpect(jsonPath("$.name").value("Test User"))
                    .andExpect(jsonPath("$.role").value("USER"))
                    .andExpect(jsonPath("$.provider").value("google"))
                    .andExpect(jsonPath("$.memberId").value(MemberFixture.DEFAULT_ID));
        }
    }

}
