package com.patientpal.backend.common.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.patientpal.backend.auth.dto.SignUpRequest;
import com.patientpal.backend.common.validation.constraints.FieldMatch;
import com.patientpal.backend.fixtures.auth.SignUpRequestFixture;
import com.patientpal.backend.test.annotation.AutoKoreanDisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@AutoKoreanDisplayName
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
class FieldMatchValidatorTest {
    private FieldMatchValidator validator;

    @BeforeEach
    void setUp() {
        validator = new FieldMatchValidator();

        FieldMatch annotation = mock(FieldMatch.class);
        when(annotation.first()).thenReturn("password");
        when(annotation.second()).thenReturn("passwordConfirm");

        validator.initialize(annotation);
    }

    @Test
    void 서로_일치하면_유효성_검사가_성공한다() {
        // given
        SignUpRequest request = SignUpRequestFixture.createUserSignUpRequest();

        // when
        boolean result = validator.isValid(request, null);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 서로_일치하지_않으면_유효성_검사가_실패한다() {
        // given
        SignUpRequest request = SignUpRequestFixture.createSignUpRequestWithMismatchedPasswords();

        // when
        boolean result = validator.isValid(request, null);

        // then
        assertThat(result).isFalse();
    }
}
