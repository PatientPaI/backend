package com.patientpal.backend.auth.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.patientpal.backend.fixtures.auth.SocialDataFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class SocialDataServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private SocialDataService socialDataService;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void 구글_유저_데이터를_성곡적으로_가져온다() {
        // given
        String mockUserData = SocialDataFixture.GOOGLE_USER_DATA;
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(ResponseEntity.ok(mockUserData));

        // when
        String userData = socialDataService.getUserData(SocialDataFixture.MOCK_ACCESS_TOKEN,
                SocialDataFixture.GOOGLE_PROVIDER);

        // then
        assertThat(userData).isEqualTo(SocialDataFixture.GOOGLE_USER_DATA);
    }

    @Test
    void 카카오_유저_데이터를_성공적으로_가져온다() {
        // given
        String mockUserData = SocialDataFixture.KAKAO_USER_DATA;
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(ResponseEntity.ok(mockUserData));

        // when
        String userData = socialDataService.getUserData(SocialDataFixture.MOCK_ACCESS_TOKEN, SocialDataFixture.KAKAO_PROVIDER);

        // then
        assertThat(userData).isEqualTo(SocialDataFixture.KAKAO_USER_DATA);
    }

    @Test
    void 네이버_유저_데이터를_성공적으로_가져온다() {
        // given
        String mockUserData = SocialDataFixture.NAVER_USER_DATA;
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(ResponseEntity.ok(mockUserData));

        // when
        String userData = socialDataService.getUserData(SocialDataFixture.MOCK_ACCESS_TOKEN, SocialDataFixture.NAVER_PROVIDER);

        // then
        assertThat(userData).isEqualTo(SocialDataFixture.NAVER_USER_DATA);
    }

    @Test
    void 유효하지_않은_액세스_토큰으로_예외를_던진다() {
        // given
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenThrow(new RuntimeException("Failed to fetch user data from google"));

        // when & then
        assertThrows(RuntimeException.class, () -> {
            socialDataService.getUserData(SocialDataFixture.MOCK_ACCESS_TOKEN, SocialDataFixture.GOOGLE_PROVIDER);
        });
    }
}
