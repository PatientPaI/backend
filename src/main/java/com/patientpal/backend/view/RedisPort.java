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
            //밑 if문이 true라는 것은 기존에 key가 존재한다는 뜻. 즉 중복 memberId가 있다는 뜻.
            if (map.put(memberId, redisTemplate.opsForHyperLogLog().size(key)) != null) { // 기존에 key 존재 시, key에 해당하는 value 반환, key 미존재 시, null 반환
                throw new IllegalStateException("Duplicate key");
            }
        }
        return map;
    }

    private Long extractMemberIdFromKey(String key) {
        return Long.valueOf(key.substring(HYPERLOGLOG_KEY_PREFIX.length()));
    }
}
