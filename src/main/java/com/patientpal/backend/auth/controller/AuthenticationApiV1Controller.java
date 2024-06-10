package com.patientpal.backend.auth.controller;

import com.patientpal.backend.auth.dto.AccessTokenResponse;
import com.patientpal.backend.auth.dto.SignInRequest;
import com.patientpal.backend.auth.dto.SignUpRequest;
import com.patientpal.backend.auth.dto.TokenDto;
import com.patientpal.backend.auth.service.JwtLoginService;
import com.patientpal.backend.auth.service.JwtTokenService;
import com.patientpal.backend.member.service.MemberService;
import com.patientpal.backend.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationApiV1Controller {
    private final JwtLoginService loginService;
    private final JwtTokenService tokenService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> authorize(@Valid @RequestBody SignInRequest request, HttpServletResponse response) {
        TokenDto tokenDto = loginService.authenticateUser(request);
        setRefreshTokenCookie(response, tokenDto.refreshToken());
        return ResponseEntity.ok().body(new AccessTokenResponse(tokenDto.accessToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refresh(@CookieValue("refresh_token") String refreshToken, HttpServletResponse response) {
        TokenDto tokenDto = tokenService.refreshJwtTokens(refreshToken);
        setRefreshTokenCookie(response, tokenDto.refreshToken());
        return ResponseEntity.ok().body(new AccessTokenResponse(tokenDto.accessToken()));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registerMember(@Valid @RequestBody SignUpRequest request) {
        return ResponseEntity.created(URI.create("/api/v1/members/" + memberService.save(request))).build();
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
}
