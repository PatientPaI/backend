package com.patientpal.backend.view;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ViewService {
    public static final String HYPERLOGLOG_KEY_PREFIX = "profile:views:";

    private final RedisTemplate<String, String> redisTemplate;

    public ViewService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addProfileView(Long memberId, String viewerId) {
        String key = HYPERLOGLOG_KEY_PREFIX + memberId;
        redisTemplate.opsForHyperLogLog().add(key, viewerId);
    }

    public long getProfileViewCount(Long memberId) {
        String key = HYPERLOGLOG_KEY_PREFIX + memberId;
        return redisTemplate.opsForHyperLogLog().size(key);
    }
}
