package com.patientpal.backend.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.patientpal.backend.auth.domain.RefreshToken;
import com.patientpal.backend.auth.repository.RefreshTokenRepository;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.fixtures.auth.RefreshTokenFixture;
import com.patientpal.backend.fixtures.member.MemberFixture;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.repository.MemberRepository;
import com.patientpal.backend.security.jwt.JwtTokenProvider;
import com.patientpal.backend.test.annotation.AutoKoreanDisplayName;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@AutoKoreanDisplayName
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void 아이디로_리프레시_토큰을_제거한다() {
        // given
        Member member = MemberFixture.createDefaultMember();

        when(memberRepository.findByUsernameOrThrow(member.getUsername())).thenReturn(member);

        // when
        refreshTokenService.deleteTokenByUsername(member.getUsername());

        // then
        verify(refreshTokenRepository).deleteByMember(member);
        verify(refreshTokenRepository).flush();
    }

    @Test
    void 리프레시_토큰을_저장한다() {
        // given
        Long expectedId = 1L;
        Member member = MemberFixture.createDefaultMember();
        RefreshToken validRefreshToken = RefreshTokenFixture.createRefreshTokenWithMemberAndId(member, expectedId);

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(validRefreshToken);

        // when
        Long actualId = refreshTokenService.save(member.getUsername(), validRefreshToken.getToken());

        // then
        assertThat(actualId).isEqualTo(expectedId);

        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void 토큰이_유효하지_않으면_검증에_실패한다() {
        // given
        boolean expectedResult = false;
        String invalidRefreshToken = RefreshTokenFixture.INVALID_REFRESH_TOKEN;

        when(tokenProvider.validateToken(invalidRefreshToken)).thenReturn(expectedResult);

        // when
        boolean actualResult = refreshTokenService.validateToken(invalidRefreshToken);

        // then
        assertThat(actualResult).isEqualTo(expectedResult);

        verifyNoMoreInteractions(refreshTokenRepository);
    }

    @Test
    void 유저가_존재하지_않는_경우_예외가_발생한다() {
        // given
        String nonExistentUsername = MemberFixture.DEFAULT_USERNAME;
        String validRefreshToken = RefreshTokenFixture.VALID_REFRESH_TOKEN;

        when(tokenProvider.validateToken(validRefreshToken)).thenReturn(true);
        when(tokenProvider.getUsernameFromToken(validRefreshToken)).thenReturn(nonExistentUsername);
        doThrow(new EntityNotFoundException(ErrorCode.MEMBER_NOT_EXIST)).when(memberRepository).findByUsernameOrThrow(nonExistentUsername);

        // when & then
        assertThatThrownBy(() -> refreshTokenService.validateToken(validRefreshToken))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void DB에_리프레시_토큰이_존재하지_않는_경우_검증에_실패한다() {
        // given
        boolean expectedResult = false;
        Member member = MemberFixture.createDefaultMember();
        String validRefreshToken = RefreshTokenFixture.VALID_REFRESH_TOKEN;

        when(tokenProvider.validateToken(validRefreshToken)).thenReturn(true);
        when(tokenProvider.getUsernameFromToken(validRefreshToken)).thenReturn(member.getUsername());
        when(memberRepository.findByUsernameOrThrow(member.getUsername())).thenReturn(member);
        when(refreshTokenRepository.findByMemberAndToken(eq(member), any(String.class))).thenReturn(Optional.empty());

        // when
        boolean actualResult = refreshTokenService.validateToken(validRefreshToken);

        // then
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void 만료된_토큰이면_참을_반환한다() {
        // given
        boolean expectedResult = true;
        RefreshToken expiredRefreshToken = RefreshTokenFixture.createExpiredRefreshToken();

        // when
        boolean actualResult = refreshTokenService.isTokenExpired(expiredRefreshToken);

        // then
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void 만료되지_않은_토큰이면_거짓을_반환한다() {
        // given
        boolean expectedResult = false;
        RefreshToken unexpiredRefreshToken = RefreshTokenFixture.createUnexpiredRefreshToken();

        // when
        boolean actualResult = refreshTokenService.isTokenExpired(unexpiredRefreshToken);

        // then
        assertThat(actualResult).isEqualTo(expectedResult);
    }
}
