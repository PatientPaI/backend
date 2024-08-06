package com.patientpal.backend.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistService {

    private static final String BLACKLIST_PREFIX = "blackList:";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void blacklistToken(String token, long expirationTime) {
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "blacklisted", expirationTime, TimeUnit.MILLISECONDS);
    }

    public boolean isTokenBlacklisted(String token) {
        return redisTemplate.hasKey(BLACKLIST_PREFIX + token);
    }
}
