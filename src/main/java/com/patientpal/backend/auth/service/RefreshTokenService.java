package com.patientpal.backend.auth.service;

import com.patientpal.backend.auth.domain.RefreshToken;
import com.patientpal.backend.auth.repository.RefreshTokenRepository;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.common.exception.InvalidValueException;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.repository.MemberRepository;
import com.patientpal.backend.security.jwt.JwtTokenProvider;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    @Transactional
    public void deleteTokenByUsername(String username) {
        refreshTokenRepository.deleteByMember(memberRepository.findByUsernameOrThrow(username));
        refreshTokenRepository.flush();
    }

    @Transactional
    public Long save(String username, String token) {
        var refreshToken = RefreshToken.builder()
                .member(memberRepository.findByUsernameOrThrow(username))
                .token(hashToken(token))
                .expiryDate(Instant.now().plusMillis(tokenProvider.refreshTokenExpirationTime))
                .build();
        return refreshTokenRepository.save(refreshToken).getId();
    }

    private String hashToken(String token) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new InvalidValueException(ErrorCode.TOKEN_HASHING_ERROR);
        }
    }

    public boolean validateToken(String token) {
        if (!tokenProvider.validateToken(token)) {
            return false;
        }

        Optional<RefreshToken> storedRefreshToken = getStoredRefreshToken(token);
        return storedRefreshToken.isPresent() && !isTokenExpired(storedRefreshToken.get());
    }

    private Optional<RefreshToken> getStoredRefreshToken(String token) {
        String username = tokenProvider.getUsernameFromToken(token);
        Member member = memberRepository.findByUsernameOrThrow(username);
        return refreshTokenRepository.findByMemberAndToken(member, hashToken(token));
    }

    public boolean isTokenExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }
}
