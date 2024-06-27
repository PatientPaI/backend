package com.patientpal.backend.matching.service;

import com.patientpal.backend.matching.dto.response.MatchListResponse;
import com.patientpal.backend.matching.dto.response.MatchResponse;
import org.springframework.data.domain.Pageable;

public interface MatchService {

    MatchResponse createMatch(String username, Long responseMemberId);

    MatchResponse getMatch(Long matchId, String username);

    MatchListResponse getRequestMatches(String username, Long memberId, Pageable pageable);

    MatchListResponse getReceivedMatches(String username, Long memberId, Pageable pageable);

    void cancelMatch(Long matchId, String username);

    void acceptMatch(Long matchId, String username);
}
