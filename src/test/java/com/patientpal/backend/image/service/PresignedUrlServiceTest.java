package com.patientpal.backend.image.service;

import static com.patientpal.backend.fixtures.image.ImageFixture.BUCKET;
import static com.patientpal.backend.fixtures.image.ImageFixture.FILE_NAME;
import static com.patientpal.backend.fixtures.image.ImageFixture.PREFIX;
import static com.patientpal.backend.fixtures.image.ImageFixture.PRESIGNED_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.patientpal.backend.test.annotation.AutoKoreanDisplayName;
import java.net.URL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@AutoKoreanDisplayName
class PresignedUrlServiceTest {

    @Mock
    private AmazonS3 amazonS3;

    @InjectMocks
    private PresignedUrlService presignedUrlService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(presignedUrlService, "bucket", BUCKET);
    }

    @Test
    void PUT_MAPPING_진행할_AWS_S3_URL_전체_경로_반환에_성공한다() throws Exception {
        // given
        URL url = new URL(PRESIGNED_URL);
        System.out.println(url);
        given(amazonS3.generatePresignedUrl(any(GeneratePresignedUrlRequest.class))).willReturn(url);

        // when
        String presignedUrl = presignedUrlService.getPresignedUrl(PREFIX, FILE_NAME);

        // then
        assertThat(presignedUrl).isEqualTo(url.toString());
    }

    @Test
    void 프로필_이미지를_조회할_URL을_가져오기_성공한다() {
        // when
        String savedUrl = presignedUrlService.getSavedUrl(PRESIGNED_URL);

        // then
        String expectedUrl = PRESIGNED_URL.split("\\?")[0];
        assertThat(savedUrl).isEqualTo(expectedUrl);    }
}
