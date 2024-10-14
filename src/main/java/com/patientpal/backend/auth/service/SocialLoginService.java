package com.patientpal.backend.auth.service;

import static com.patientpal.backend.common.exception.ErrorCode.INVALID_USERNAME;
import static com.patientpal.backend.common.exception.ErrorCode.MEMBER_NOT_EXIST;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import com.patientpal.backend.auth.domain.SocialProvider;
import com.patientpal.backend.common.exception.BusinessException;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.domain.Role;
import com.patientpal.backend.member.service.MemberService;
import com.patientpal.backend.security.jwt.JwtTokenProvider;
import com.patientpal.backend.security.oauth.CustomOauth2UserPrincipal;
import com.patientpal.backend.security.oauth.dto.Oauth2SignUpRequest;
import com.patientpal.backend.security.oauth.dto.Oauth2SignUpResponse;
import com.patientpal.backend.security.oauth.userinfo.CustomOauth2UserInfo;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class SocialLoginService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;

    private static final String GOOGLE_SCOPE = "profile email";
    private static final String KAKAO_SCOPE = "profile_nickname account_email";
    private static final String NAVER_SCOPE = "name email";

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    @Value("${spring.security.oauth2.client.provider.google.authorization-uri}")
    private String googleAuthorizationUri;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${spring.security.oauth2.client.provider.kakao.authorization-uri}")
    private String kakaoAuthorizationUri;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String naverRedirectUri;

    @Value("${spring.security.oauth2.client.provider.naver.authorization-uri}")
    private String naverAuthorizationUri;

    public String getLoginUrl(SocialProvider provider) {
        return switch (provider) {
            case GOOGLE -> buildLoginUrl(googleAuthorizationUri, googleClientId, googleRedirectUri, GOOGLE_SCOPE);
            case KAKAO -> buildLoginUrl(kakaoAuthorizationUri, kakaoClientId, kakaoRedirectUri, KAKAO_SCOPE);
            case NAVER -> buildLoginUrl(naverAuthorizationUri, naverClientId, naverRedirectUri, NAVER_SCOPE);
            default -> {
                log.error("Unsupported provider: {}", provider);
                throw new IllegalArgumentException("Unsupported provider: " + provider);
            }
        };
    }

    private String buildLoginUrl(String authorizationUri, String clientId, String redirectUri, String scope) {
        return UriComponentsBuilder.fromUriString(authorizationUri)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", URLEncoder.encode(redirectUri, StandardCharsets.UTF_8))
                .queryParam("response_type", "code")
                .queryParam("scope", scope)
                .queryParam("state", generateState())
                .build()
                .toUriString();
    }

    public String generateToken(Member member, String provider, String email, String name) {
        CustomOauth2UserInfo userInfo = CustomOauth2UserInfo.of(provider, Map.of("email", email, "name", name));
        String password = member.getPassword() != null ? member.getPassword() : "DUMMY_PASSWORD";
        CustomOauth2UserPrincipal principal = new CustomOauth2UserPrincipal(member.getUsername(), password, Collections.singletonList(new SimpleGrantedAuthority(member.getRole().name())));
        principal.setUserInfo(userInfo);

        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        return jwtTokenProvider.createAccessToken(authentication);
    }

    public ResponseEntity<Oauth2SignUpResponse> createSignupResponse(Oauth2SignUpRequest signupForm,
                                                                     HttpSession session,
                                                                     Member newMember) {
        String token = generateToken(newMember, signupForm.getProvider(), signupForm.getEmail(), signupForm.getName());

        session.setAttribute("token", token);

        Oauth2SignUpResponse response = Oauth2SignUpResponse.fromMember(newMember, signupForm.getUsername(), signupForm.getEmail(), signupForm.getName(), signupForm.getRole().name(), signupForm.getProvider(), token);

        return ResponseEntity.created(URI.create("/api/v1/members/" + newMember.getId())).body(response);
    }

    public ResponseEntity<Oauth2SignUpResponse> createLoginResponse(HttpSession session, Member member) {
        String email = (String) session.getAttribute("email");
        String username = (String) session.getAttribute("username");
        String name = (String) session.getAttribute("name");
        String provider = (String) session.getAttribute("provider");

        String token = generateToken(member, provider, email, name);
        session.setAttribute("token", token);

        Oauth2SignUpResponse response = Oauth2SignUpResponse.fromMember(member, username,email, name, member.getRole().name(), provider, token);
        return ResponseEntity.ok(response);
    }

    public Oauth2SignUpResponse createOauth2SignUpResponse(HttpSession session) {
        String email = (String) session.getAttribute("email");
        String name = (String) session.getAttribute("name");
        String provider = (String) session.getAttribute("provider");
        String username = (String) session.getAttribute("username");
        String token = (String) session.getAttribute("token");

        if (username == null || username.isEmpty()) {
            throw new BusinessException(INVALID_USERNAME, "Username is not set in session");
        }

        Member member = memberService.getUserByUsername(username);

        if (member == null) {
            throw new EntityNotFoundException(MEMBER_NOT_EXIST, "Member not found with username: " + username);
        }

        return Oauth2SignUpResponse.fromMember(member,username, email, name, String.valueOf(Role.USER), provider, token);
    }

    private String generateState() {
        return UUID.randomUUID().toString();
    }
}
