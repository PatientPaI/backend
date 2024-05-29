package com.patientpal.backend.common.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.patientpal.backend.auth.dto.SignUpRequest;
import com.patientpal.backend.common.validation.constraints.FieldMatch;
import com.patientpal.backend.fixtures.member.SignUpRequestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("@FieldMatch 유효성 검사")
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
    @DisplayName("서로 일치하면 유효성 검사가 성공한다.")
    void success() {
        // given
        SignUpRequest request = SignUpRequestFixture.createValidSignUpRequest();

        // when
        boolean result = validator.isValid(request, null);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("서로 일치하지 않으면 유효성 검사가 실패한다.")
    void fail() {
        // given
        SignUpRequest request = SignUpRequestFixture.createSignUpRequestWithMismatchedPasswords();

        // when
        boolean result = validator.isValid(request, null);

        // then
        assertThat(result).isFalse();
    }
}
