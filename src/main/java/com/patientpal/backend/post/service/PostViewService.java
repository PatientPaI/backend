package com.patientpal.backend.post.service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostViewService {

    private static final String HYPERLOGLOG_POST_VIEW_PREFIX = "post:views:";

    private final RedisTemplate<String, String> redisTemplate;

    @Async
    public void addPostView(Long postId) {
        String key = HYPERLOGLOG_POST_VIEW_PREFIX + postId;
        redisTemplate.opsForHyperLogLog().add(key, "");
    }

    @Nullable
    public Map<Long, Long> fetchAllPostViewCounts() {
        Set<String> keys = redisTemplate.keys(HYPERLOGLOG_POST_VIEW_PREFIX + "*");
        if (keys == null) {
            return null;
        }

        Map<Long, Long> postViewCountMap = new ConcurrentHashMap<>();
        keys.forEach(key -> {
            Long value = redisTemplate.opsForHyperLogLog().size(key);
            postViewCountMap.put(getPostIdFromKey(key), value);
        });

        return postViewCountMap;
    }

    public void deleteAllViewCounts(Set<Long> keySet) {
        for (Long postId : keySet) {
            String key = HYPERLOGLOG_POST_VIEW_PREFIX + postId;
            redisTemplate.delete(key);
        }
    }

    private Long getPostIdFromKey(String key) {
        return Long.valueOf(key.substring(HYPERLOGLOG_POST_VIEW_PREFIX.length()));
    }
}
