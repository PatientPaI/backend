package com.patientpal.backend.config;

import com.patientpal.backend.view.SyncViewCountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {

    private final SyncViewCountService syncViewCountService;

    @Scheduled(fixedRate = 21600000) //6시간마다
    public void syncViewCountsTask() {
        final int maxAttempts = 5;
        int attempts = 0;

        while (attempts < maxAttempts) {
            try {
                syncViewCountService.syncViewCountsBatch();
                break;
            } catch (Exception e) {
                attempts++;
                log.info("Redis 데이터 동기화 재시도: {}회, 오류: {}", attempts, e.getMessage());
                if (attempts >= maxAttempts) {
                    log.info("최대 재시도 횟수에 도달했습니다. Redis 데이터 동기화 작업에 실패했습니다.");
                }
            }
        }
    }
}
