package com.patientpal.backend.auth.service;

import static com.patientpal.backend.common.exception.ErrorCode.*;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.patientpal.backend.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SocialDataService {

    private final RestTemplate restTemplate;

    public String getUserData(String accessToken, String provider) {
        if(accessToken == null || accessToken.isEmpty()) {
            throw new IllegalArgumentException("Access token cannot be null or empty");
        }
        if(provider == null || provider.isEmpty()) {
            throw new IllegalArgumentException("Provider cannot be null or empty");
        }

        String apiUrl = getApiUrl(provider);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            throw new BusinessException(USER_DATA_FETCH_FAILED ,provider);
        }
    }

    private String getApiUrl(String provider) {
        return switch (provider) {
            case "google" -> "https://www.googleapis.com/oauth2/v3/userinfo";
            case "kakao" -> "https://kapi.kakao.com/v2/user/me";
            case "naver" -> "https://openapi.naver.com/v1/nid/me";
            default -> throw new IllegalArgumentException("Unsupported provider: " + provider);
        };
    }
}
