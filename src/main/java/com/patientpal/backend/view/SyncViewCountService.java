package com.patientpal.backend.view;

import java.util.Map;
import com.patientpal.backend.post.service.PostViewService;
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
    private final PostViewService postViewService;
    private final PostViewCountAdapter postViewCountAdapter;

    @Transactional
    public void syncViewCountsBatch() {
        Map<Long, Long> viewCounts = redisPort.fetchAllViewCounts();
        profileViewCountAdapter.batchUpdateViewCounts(viewCounts);
        redisPort.deleteAllViewCounts(viewCounts.keySet());
    }

    @Transactional
    public void syncPostViewCountsBatch() {
        final var viewCounts = postViewService.fetchAllPostViewCounts();
        if (viewCounts == null) {
            return;
        }
        postViewCountAdapter.batchUpdatePostViewCounts(viewCounts);
        postViewService.deleteAllViewCounts(viewCounts.keySet());
    }
}
