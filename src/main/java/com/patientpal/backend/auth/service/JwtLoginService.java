package com.patientpal.backend.auth.service;

import com.patientpal.backend.auth.dto.SignInRequest;
import com.patientpal.backend.auth.dto.TokenDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtLoginService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenService tokenService;
    private final LoginService loginService;

    public TokenDto authenticateUser(SignInRequest request) {
        UserDetails userDetails = loginService.loadUserByUsername(request.getUsername());
        var authenticationToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword(), userDetails.getAuthorities());
        var authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        return tokenService.generateJwtTokens(request.getUsername(), authentication);
    }
}
