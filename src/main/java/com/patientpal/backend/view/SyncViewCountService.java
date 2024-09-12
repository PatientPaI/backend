package com.patientpal.backend.view;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class SyncViewCountService {

    private final RedisPort redisPort;
    private final ProfileViewCountAdapter profileViewCountAdapter;

    @Transactional
    public void syncViewCountsBatch() {
        Map<Long, Long> viewCounts = redisPort.fetchAllViewCounts();
        profileViewCountAdapter.batchUpdateViewCounts(viewCounts);
        redisPort.deleteAllViewCounts(viewCounts.keySet());
    }
}
