package com.patientpal.backend.auth.controller;

import com.patientpal.backend.auth.dto.AccessTokenResponse;
import com.patientpal.backend.auth.dto.SignInRequest;
import com.patientpal.backend.auth.dto.SignUpRequest;
import com.patientpal.backend.auth.dto.TokenDto;
import com.patientpal.backend.auth.service.JwtLoginService;
import com.patientpal.backend.auth.service.JwtTokenService;
import com.patientpal.backend.auth.service.TokenBlacklistService;
import com.patientpal.backend.common.exception.AuthenticationException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.member.service.MemberService;
import com.patientpal.backend.security.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<AccessTokenResponse> refresh(@CookieValue("refresh_token") String refreshToken,
                                                       HttpServletResponse response) {

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
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge((int) (jwtTokenProvider.refreshTokenExpirationTime / 1000))
                .sameSite("None")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
    }

    private static void setRefreshTokenCookie(HttpServletResponse resp) {
        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
    }
}

