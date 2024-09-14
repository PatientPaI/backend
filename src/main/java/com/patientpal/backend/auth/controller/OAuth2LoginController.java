package com.patientpal.backend.auth.controller;

import static com.patientpal.backend.common.exception.ErrorCode.INVALID_USERNAME;
import static com.patientpal.backend.common.exception.ErrorCode.MEMBER_NOT_EXIST;

import com.patientpal.backend.auth.dto.TokenDto;
import com.patientpal.backend.auth.service.JwtLoginService;
import com.patientpal.backend.auth.service.SocialDataService;
import com.patientpal.backend.common.exception.BusinessException;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.domain.Role;
import com.patientpal.backend.member.service.MemberService;
import com.patientpal.backend.security.jwt.JwtTokenProvider;
import com.patientpal.backend.security.oauth.CustomOauth2UserPrincipal;
import com.patientpal.backend.security.oauth.dto.Oauth2SignUpRequest;
import com.patientpal.backend.security.oauth.dto.Oauth2SignUpResponse;
import com.patientpal.backend.security.oauth.userinfo.CustomOauth2UserInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@Tag(name = "인증", description = "인증 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class OAuth2LoginController {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtLoginService jwtLoginService;
    private final MemberService memberService;
    private final SocialDataService socialDataService;

    private static final String RESPONSE_TYPE = "code";
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


    @Operation(summary = "소셜 로그인 URL 가져오기", description = "소셜 로그인 페이지 URL을 JSON으로 응답합니다.")
    @ApiResponse(responseCode = "200", description = "소셜 로그인 URL 반환 성공")
    @GetMapping("/oauth2/authorize/{provider}")
    public ResponseEntity<Map<String, String>> getSocialLoginUrl(@PathVariable String provider) {
        String loginUrl = switch (provider) {
            case "google" -> buildLoginUrl(googleAuthorizationUri, googleClientId, googleRedirectUri, GOOGLE_SCOPE);
            case "kakao" -> buildLoginUrl(kakaoAuthorizationUri, kakaoClientId, kakaoRedirectUri, KAKAO_SCOPE);
            case "naver" -> buildLoginUrl(naverAuthorizationUri, naverClientId, naverRedirectUri, NAVER_SCOPE);
            default -> throw new IllegalArgumentException("Unsupported provider: " + provider);
        };

        Map<String, String> response = new HashMap<>();
        response.put("loginUrl", loginUrl);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "구글 엑세스 토큰 요청", description = "구글 인가 코드를 사용하여 엑세스 토큰을 요청합니다.")
    @ApiResponse(responseCode = "200", description = "엑세스 토큰 요청 성공",
            content = @Content(schema = @Schema(implementation = TokenDto.class)))
    @PostMapping("/oauth2/token/google")
    public ResponseEntity<TokenDto> getGoogleToken(@RequestParam("code") String code) {
        TokenDto tokenDto = jwtLoginService.getGoogleAccessToken(code);
        return ResponseEntity.ok(tokenDto);
    }

    @Operation(summary = "카카오 엑세스 토큰 요청", description = "카카오 인가 코드를 사용하여 엑세스 토큰을 요청합니다.")
    @ApiResponse(responseCode = "200", description = "엑세스 토큰 요청 성공",
            content = @Content(schema = @Schema(implementation = TokenDto.class)))
    @PostMapping("/oauth2/token/kakao")
    public ResponseEntity<TokenDto> getKakaoToken(@RequestParam("code") String code) {
        TokenDto tokenDto = jwtLoginService.getKakaoAccessTokenFromCode(code);
        return ResponseEntity.ok(tokenDto);
    }

    @Operation(summary = "네이버 엑세스 토큰 요청", description = "네이버 인가 코드를 사용하여 엑세스 토큰을 요청합니다.")
    @ApiResponse(responseCode = "200", description = "엑세스 토큰 요청 성공",
            content = @Content(schema = @Schema(implementation = TokenDto.class)))
    @PostMapping("/oauth2/token/naver")
    public ResponseEntity<TokenDto> getNaverToken(@RequestParam("code") String code, @RequestParam("state") String state) {
        TokenDto tokenDto = jwtLoginService.getNaverAccessTokenFromCode(code, state);
        return ResponseEntity.ok(tokenDto);
    }

    @Operation(summary = "액세스 토큰으로 소셜 데이터 요청", description = "액세스 토큰을 사용하여 소셜 플랫폼에서 사용자 데이터를 요청합니다.")
    @ApiResponse(responseCode = "200", description = "소셜 데이터 요청 성공",
            content = @Content(schema = @Schema(implementation = String.class))) 
    @GetMapping("/user/data")
    public ResponseEntity<String> getUserData(@RequestParam String accessToken, @RequestParam String provider) {
        String userData = socialDataService.getUserData(accessToken, provider);
        return ResponseEntity.ok(userData);
    }

    @PostMapping("/oauth2/register-or-login")
    @Operation(summary = "소셜 회원가입 또는 로그인", description = "소셜 로그인 후 회원가입 또는 로그인 절차를 수행한다.")
    @ApiResponse(responseCode = "200", description = "회원가입 또는 로그인 성공",
            content = @Content(schema = @Schema(implementation = Oauth2SignUpResponse.class)))
    public ResponseEntity<?> processOauth2Signup(HttpSession session) {
        String username = (String) session.getAttribute("username");

        Optional<Member> optionalMember = memberService.findOptionalByUsername(username);
        if(optionalMember.isPresent()) {
            Member member = optionalMember.get();
            return createLoginResponse(session, member);
        }

        Map<String, String> response = new HashMap<>();
        response.put("redirectUrl", "/oauth2/register");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/oauth2/register")
    @Operation(summary = "소셜 회원가입 정보 가져오기", description = "소셜 로그인 후 회원가입 페이지에서 필요한 정보를 가져온다.")
    @ApiResponse(responseCode = "200", description = "소셜 회원가입 정보 가져오기 성공",
            content = @Content(schema = @Schema(implementation = Oauth2SignUpResponse.class)))
    public ResponseEntity<Oauth2SignUpResponse> getOauth2UserInfo(HttpSession session) {
        Oauth2SignUpResponse response = createOauth2SignUpResponse(session);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/oauth2/register")
    @Operation(summary = "소셜 회원가입", description = "소셜 로그인 후 추가 정보를 입력하여 회원가입을 처리한다.")
    @ApiResponse(responseCode = "201", description = "회원가입 성공",
            content = @Content(schema = @Schema(implementation = Oauth2SignUpResponse.class)))
    public ResponseEntity<Oauth2SignUpResponse> registerUser(@RequestBody Oauth2SignUpRequest signupForm, HttpSession session) {

        Long newMemberId = memberService.saveSocialUser(signupForm);
        Member newMember = memberService.getUserById(newMemberId);

        return createSignupResponse(signupForm, session, newMember);
    }

    private ResponseEntity<Oauth2SignUpResponse> createSignupResponse(Oauth2SignUpRequest signupForm,
                                                                                       HttpSession session,
                                                                                       Member newMember) {
        String token = generateToken(newMember, signupForm.getProvider(), signupForm.getEmail(), signupForm.getName());

        session.setAttribute("token", token);

        Oauth2SignUpResponse response = Oauth2SignUpResponse.fromMember(newMember, signupForm.getUsername(), signupForm.getEmail(), signupForm.getName(), signupForm.getRole().name(), signupForm.getProvider(), token);

        return ResponseEntity.created(URI.create("/api/v1/members/" + newMember.getId())).body(response);
    }

    private ResponseEntity<Oauth2SignUpResponse> createLoginResponse(HttpSession session, Member member) {
        String email = (String) session.getAttribute("email");
        String username = (String) session.getAttribute("username");
        String name = (String) session.getAttribute("name");
        String provider = (String) session.getAttribute("provider");

        String token = generateToken(member, provider, email, name);
        session.setAttribute("token", token);

        Oauth2SignUpResponse response = Oauth2SignUpResponse.fromMember(member, username,email, name, member.getRole().name(), provider, token);
        return ResponseEntity.ok(response);
    }

    private Oauth2SignUpResponse createOauth2SignUpResponse(HttpSession session) {
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

    private String generateToken(Member member, String provider, String email, String name) {
        CustomOauth2UserInfo userInfo = CustomOauth2UserInfo.of(provider, Map.of("email", email, "name", name));
        String password = member.getPassword() != null ? member.getPassword() : "DUMMY_PASSWORD";
        CustomOauth2UserPrincipal principal = new CustomOauth2UserPrincipal(member.getUsername(), password, Collections.singletonList(new SimpleGrantedAuthority(member.getRole().name())));
        principal.setUserInfo(userInfo);

        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        return jwtTokenProvider.createAccessToken(authentication);
    }

    private String getUsername(Oauth2SignUpRequest signupForm, HttpSession session) {
        String username = (String) session.getAttribute("username");
        signupForm
                .toBuilder()
                .username(username)
                .build();
        return username;
    }

    private Member getMember(String username) {
        Member member = memberService.getUserByUsername(username);
        if (member == null) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_EXIST, "Member not found with username: " + username);
        }
        return member;
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

    private String generateState() {
        return UUID.randomUUID().toString();
    }
}
