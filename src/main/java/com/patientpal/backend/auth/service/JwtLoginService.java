package com.patientpal.backend.auth.service;

import com.patientpal.backend.auth.dto.SignInRequest;
import com.patientpal.backend.auth.dto.TokenDto;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class JwtLoginService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenService tokenService;
    private final LoginService loginService;

    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    @Value("${spring.security.oauth2.client.provider.google.token-uri}")
    private String googleTokenUri;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String kakaoTokenUri;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String naverRedirectUri;

    @Value("${spring.security.oauth2.client.provider.naver.token-uri}")
    private String naverTokenUri;

    public TokenDto authenticateUser(SignInRequest request) {
        UserDetails userDetails = loginService.loadUserByUsername(request.getUsername());
        var authenticationToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword(), userDetails.getAuthorities());
        var authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        return tokenService.generateJwtTokens(request.getUsername(), authentication);
    }

    public TokenDto getGoogleAccessToken(String code) {
        return getAccessTokenFromCode(code, googleClientId, googleClientSecret, googleRedirectUri, googleTokenUri);
    }

    public TokenDto getKakaoAccessTokenFromCode(String code) {
        return getAccessTokenFromCode(code, kakaoClientId, kakaoClientSecret, kakaoRedirectUri, kakaoTokenUri);
    }

    public TokenDto getNaverAccessTokenFromCode(String code, String state) {
        return getAccessTokenFromCode(code, naverClientId, naverClientSecret, naverRedirectUri, naverTokenUri, state);
    }


    private TokenDto getAccessTokenFromCode(String code, String clientId, String clientSecret, String redirectUri,
                                            String tokenUri) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "code=" + code
                + "&client_id=" + clientId
                + "&client_secret=" + clientSecret
                + "&redirect_uri=" + redirectUri
                + "&grant_type=authorization_code";

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(tokenUri, HttpMethod.POST, request, Map.class);

        return parseTokenResponse(response.getBody());
    }

    private TokenDto getAccessTokenFromCode(String code, String clientId, String clientSecret, String redirectUri,
                                            String tokenUri, String state) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "code=" + code
                + "&client_id=" + clientId
                + "&client_secret=" + clientSecret
                + "&redirect_uri=" + redirectUri
                + "&grant_type=authorization_code"
                + "&state=" + state;

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(tokenUri, HttpMethod.POST, request, Map.class);

        return parseTokenResponse(response.getBody());
    }

    private TokenDto parseTokenResponse(Map<String, Object> responseBody) {
        String accessToken = (String) responseBody.get("access_token");
        String refreshToken = (String) responseBody.get("refresh_token");

        return new TokenDto(accessToken, refreshToken);
    }
}
