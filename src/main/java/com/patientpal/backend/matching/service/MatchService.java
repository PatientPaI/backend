package com.patientpal.backend.matching.service;

import com.patientpal.backend.matching.dto.request.CreateMatchCaregiverRequest;
import com.patientpal.backend.matching.dto.request.CreateMatchPatientRequest;
import com.patientpal.backend.matching.dto.response.CreateMatchResponse;
import com.patientpal.backend.matching.dto.response.ReceivedMatchListResponse;
import com.patientpal.backend.matching.dto.response.RequestMatchListResponse;
import com.patientpal.backend.matching.dto.response.MatchResponse;
import org.springframework.data.domain.Pageable;

public interface MatchService {

    MatchResponse createMatchPatient(String username, Long responseMemberId, CreateMatchPatientRequest createMatchRequest);

    MatchResponse createMatchCaregiver(String username, Long responseMemberId, CreateMatchCaregiverRequest createMatchRequest);

    MatchResponse getMatch(Long matchId, String username);

    RequestMatchListResponse getRequestMatches(String username, Long memberId, Pageable pageable);

    ReceivedMatchListResponse getReceivedMatches(String username, Long memberId, Pageable pageable);

    void cancelMatch(Long matchId, String username);

    void acceptMatch(Long matchId, String username);

    CreateMatchResponse getCreateMatchRequest(String username, Long responseMemberId);
}
