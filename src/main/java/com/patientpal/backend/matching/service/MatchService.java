package com.patientpal.backend.matching.service;

import com.patientpal.backend.matching.dto.response.MatchListResponse;
import com.patientpal.backend.matching.dto.response.MatchResponse;
import org.springframework.data.domain.Pageable;

public interface MatchService {

    MatchResponse createForPatient(String username, Long responseMemberId);

    MatchResponse createForCaregiver(String username, Long responseMemberId);

    MatchResponse getMatch(Long matchId, String username);

    MatchListResponse getRequestMatches(String username, Pageable pageable);

    MatchListResponse getReceivedMatches(String username, Pageable pageable);

    void cancelMatch(Long matchId, String username);

    void acceptMatch(Long matchId, String username);
}
