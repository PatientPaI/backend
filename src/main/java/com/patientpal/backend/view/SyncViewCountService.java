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

    /**
     * Redis에 저장된 조회수를 RDB에 저장한다.
     * 받아온 조회수 정보 Map을 그대로 Adapter에 넘겨서 배치 처리한다.
     */
    @Transactional
    public void syncViewCountsBatch() {
        Map<Long, Long> viewCounts = redisPort.fetchAllViewCounts();
        profileViewCountAdapter.batchUpdateViewCounts(viewCounts);
    }
}
