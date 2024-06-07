package com.patientpal.backend.matching.service;

import com.patientpal.backend.matching.dto.response.MatchListResponse;
import com.patientpal.backend.matching.dto.response.MatchResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;

public interface MatchService {

    MatchResponse createForPatient(User currentMember, Long responseMemberId);
    MatchResponse createForCaregiver(User currentMember, Long responseMemberId);

    MatchResponse getMatch(Long matchId, String username);

    MatchListResponse getMatchList(String username, Pageable pageable);

    void cancelMatch(Long matchId, String username);

    void acceptMatch(Long matchId, String username);
}
