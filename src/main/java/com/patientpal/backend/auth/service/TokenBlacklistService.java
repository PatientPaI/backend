package com.patientpal.backend.auth.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TokenBlacklistService {

    private static final String BLACKLIST = "blacklist";

    private final RedisTemplate<String, String> redisTemplate;

    public void blacklistToken(String token, long expirationTime) {
        redisTemplate.opsForValue().set(BLACKLIST + token, "blacklisted",
                expirationTime, TimeUnit.MICROSECONDS);
    }

    public boolean isTokenBlacklisted(String token) {
        return redisTemplate.hasKey(BLACKLIST + token);
    }
}
