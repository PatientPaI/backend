package com.patientpal.backend.auth.controller;

import com.patientpal.backend.auth.dto.AccessTokenResponse;
import com.patientpal.backend.auth.dto.SignInRequest;
import com.patientpal.backend.auth.dto.SignUpRequest;
import com.patientpal.backend.auth.dto.TokenDto;
import com.patientpal.backend.auth.service.JwtLoginService;
import com.patientpal.backend.auth.service.JwtTokenService;
import com.patientpal.backend.auth.service.TokenBlacklistService;
import com.patientpal.backend.common.exception.AuthenticationException;
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
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.patientpal.backend.common.exception.ErrorCode.INVALID_USERNAME;
import static com.patientpal.backend.common.exception.ErrorCode.MEMBER_NOT_EXIST;

@Tag(name = "인증", description = "인증 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationApiV1Controller {
    private final JwtLoginService loginService;
    private final JwtTokenService tokenService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "로그인을 한다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공",
            content = @Content(schema = @Schema(implementation = AccessTokenResponse.class)),
            headers = @Header(name = "Set-Cookie", description = "리프레시 토큰 쿠키 설정", schema = @Schema(type = "string")))
    public ResponseEntity<AccessTokenResponse> authorize(@Valid @RequestBody SignInRequest request, HttpServletResponse response) {
        TokenDto tokenDto = loginService.authenticateUser(request);
        setRefreshTokenCookie(response, tokenDto.refreshToken());
        return ResponseEntity.ok().body(new AccessTokenResponse(tokenDto.accessToken()));
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 재발급", description = "리프레시 토큰을 통해 액세스 토큰과 리프레시 토큰을 재발급 받는다.")
    @ApiResponse(responseCode = "200", description = "재발급 성공",
            content = @Content(schema = @Schema(implementation = AccessTokenResponse.class)),
            headers = @Header(name = "Set-Cookie", description = "리프레시 토큰 쿠키 설정", schema = @Schema(type = "string")))
    public ResponseEntity<AccessTokenResponse> refresh(@CookieValue("refresh_token") String refreshToken, HttpServletResponse response) {
        if (tokenBlacklistService.isTokenBlacklisted(refreshToken)) {
            throw new AuthenticationException(ErrorCode.INVALID_TOKEN);
        }

        TokenDto tokenDto = tokenService.refreshJwtTokens(refreshToken);
        setRefreshTokenCookie(response, tokenDto.refreshToken());
        return ResponseEntity.ok().body(new AccessTokenResponse(tokenDto.accessToken()));
    }

    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "회원가입을 한다.")
    @ApiResponse(responseCode = "201", description = "회원가입 성공")
    public ResponseEntity<Void> registerMember(@Valid @RequestBody SignUpRequest request) {
        return ResponseEntity.created(URI.create("/api/v1/members/" + memberService.save(request))).build();
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
    @Operation(summary = "소셜 회원가입", description = "소셜 로그인 후 추가 정보를 입력받아 회원가입을 한다.")
    @ApiResponse(responseCode = "201", description = "회원가입 성공",
            content = @Content(schema = @Schema(implementation = Oauth2SignUpResponse.class)))
    public ResponseEntity<Oauth2SignUpResponse> processOauth2Signup(@Valid @RequestBody Oauth2SignUpRequest signupForm,
                                                                    HttpSession session) {
        String username = getUsername(signupForm, session);

        memberService.saveSocialUser(signupForm);

        Member member = getMember(username);
        String token = generateToken(member, signupForm.getProvider(), signupForm.getEmail(), signupForm.getName());

        session.setAttribute("token", token);

        Oauth2SignUpResponse response = Oauth2SignUpResponse.fromMember(member, signupForm.getEmail(), signupForm.getName(), signupForm.getRole().name(), signupForm.getProvider(), token);

        return ResponseEntity.created(URI.create("/api/v1/members/" + member.getId())).body(response);
    }

    @GetMapping("/logout")
    @Operation(summary = "로그아웃", description = "사용자를 로그아웃한다.")
    public void logout(HttpServletRequest req, HttpServletResponse resp,
                         @CookieValue("refresh_token") String refreshToken) {
        if (refreshToken != null) {
            jwtTokenProvider.invalidateToken(refreshToken);
            setRefreshTokenCookie(resp);
        }
        new SecurityContextLogoutHandler()
                .logout(req, resp, SecurityContextHolder.getContext().getAuthentication());
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        // TODO: 서비스가 HTTPS로 배포된 후에 보안 강화를 위해 주석을 해제해야 함
        // refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge((int) (jwtTokenProvider.refreshTokenExpirationTime / 1000));
        response.addCookie(refreshTokenCookie);
    }

    private Oauth2SignUpResponse createOauth2SignUpResponse(HttpSession session) {
        String email = (String) session.getAttribute("email");
        String name = (String) session.getAttribute("name");
        Role role = (Role) session.getAttribute("role");
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

        return Oauth2SignUpResponse.fromMember(member, email, name, role.name(), provider, token);
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

    private static void setRefreshTokenCookie(HttpServletResponse resp) {
        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
    }
}

