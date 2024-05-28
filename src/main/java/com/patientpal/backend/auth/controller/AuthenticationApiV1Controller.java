package com.patientpal.backend.auth.controller;

import com.patientpal.backend.auth.dto.RefreshTokenRequest;
import com.patientpal.backend.auth.dto.SignInRequest;
import com.patientpal.backend.auth.dto.SignUpRequest;
import com.patientpal.backend.auth.dto.TokenResponse;
import com.patientpal.backend.auth.service.JwtLoginService;
import com.patientpal.backend.auth.service.JwtTokenService;
import com.patientpal.backend.member.service.MemberService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> authorize(@Valid @RequestBody SignInRequest request) {
        return ResponseEntity.ok().body(loginService.authenticateUser(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok().body(tokenService.refreshJwtTokens(request));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> registerMember(@Valid @RequestBody SignUpRequest request) {
        return ResponseEntity.created(URI.create("/api/v1/members/" + memberService.save(request))).build();
    }
}
