package com.patientpal.backend.view;

import static com.patientpal.backend.view.ViewService.HYPERLOGLOG_KEY_PREFIX;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class RedisPort {
    private final RedisTemplate<String, String> redisTemplate;

    public Map<Long, Long> fetchAllViewCounts() {
        Set<String> keys = redisTemplate.keys(HYPERLOGLOG_KEY_PREFIX + "*");
        if (keys == null) {
            return new HashMap<>();
        }
        Map<Long, Long> map = new HashMap<>();
        for (String key : keys) {
            Long memberId = extractMemberIdFromKey(key);
            if (map.put(memberId, redisTemplate.opsForHyperLogLog().size(key)) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }
        return map;
    }

    private Long extractMemberIdFromKey(String key) {
        return Long.valueOf(key.substring(HYPERLOGLOG_KEY_PREFIX.length()));
    }

    public void deleteAllViewCounts(Set<Long> memberIds) {
        for (Long memberId : memberIds) {
            String key = HYPERLOGLOG_KEY_PREFIX + memberId;
            redisTemplate.delete(key);
        }
        log.info("동기화 후 Redis 데이터 삭제 완료");
    }
}
