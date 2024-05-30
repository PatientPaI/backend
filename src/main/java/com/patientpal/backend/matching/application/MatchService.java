package com.patientpal.backend.matching.application;

import com.patientpal.backend.matching.domain.Match;
import com.patientpal.backend.matching.dto.response.MatchListResponse;
import com.patientpal.backend.matching.dto.response.MatchResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MatchService {

    MatchResponse create(Long requestMemberId, Long responseMemberId);

    MatchResponse getMatch(Long matchId);

    MatchListResponse getMatchList(String username, Pageable pageable);

}
