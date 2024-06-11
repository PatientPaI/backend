package com.patientpal.backend.fixtures.auth;

import com.patientpal.backend.auth.domain.RefreshToken;
import com.patientpal.backend.fixtures.member.MemberFixture;
import com.patientpal.backend.member.domain.Member;
import java.time.Instant;
import org.springframework.test.util.ReflectionTestUtils;

public class RefreshTokenFixture {

    public static final Long DEFAULT_ID = 1L;
    public static final Member DEFAULT_MEMBER = MemberFixture.createDefaultMember();

    public static final String VALID_REFRESH_TOKEN = "validRefreshToken";
    public static final Instant VALID_EXPIRY_DATE = Instant.now().plusMillis(3600000L);

    public static final String INVALID_REFRESH_TOKEN = "invalidRefreshToken";
    public static final Instant INVALID_EXPIRY_DATE = Instant.now().minusMillis(3600000L);

    public static RefreshToken createDefaultRefreshToken() {
        return createRefreshTokenWithMember(DEFAULT_MEMBER);
    }

    public static RefreshToken createRefreshTokenWithMember(Member member) {
        return createRefreshTokenWithMemberAndId(member, DEFAULT_ID);
    }

    public static RefreshToken createRefreshTokenWithMemberAndId(Member member, Long refreshTokenId) {
        RefreshToken refreshToken = createRefreshTokenBuilder()
                .member(member)
                .build();
        ReflectionTestUtils.setField(refreshToken, "id", refreshTokenId);
        return refreshToken;
    }

    public static RefreshToken createExpiredRefreshToken() {
        return createRefreshTokenBuilder()
                .expiryDate(INVALID_EXPIRY_DATE)
                .build();
    }

    public static RefreshToken createUnexpiredRefreshToken() {
        return createRefreshTokenBuilder()
                .expiryDate(VALID_EXPIRY_DATE)
                .build();
    }

    private static RefreshToken.RefreshTokenBuilder createRefreshTokenBuilder() {
        return RefreshToken.builder()
                .member(DEFAULT_MEMBER)
                .token(VALID_REFRESH_TOKEN)
                .expiryDate(VALID_EXPIRY_DATE);
    }
}
