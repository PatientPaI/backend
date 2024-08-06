package com.patientpal.backend.matching.service;

import com.patientpal.backend.matching.domain.MatchRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchStatusUpdateService {

    private final MatchRepository matchRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateCompletedMatches() {
        try {
            LocalDateTime now = LocalDateTime.now();
            int updatedCount = matchRepository.updateCompletedMatches(now);
            log.info("총 {}개의 매칭이 COMPLETED 상태로 업데이트되었습니다.", updatedCount);
        } catch (Exception e) {
            log.error("매칭 상태 업데이트 중 오류 발생: ", e);
        }
    }
}
