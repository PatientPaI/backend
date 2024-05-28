package com.patientpal.backend.matching.application;

import com.patientpal.backend.matching.domain.Match;

import java.util.List;

public interface MatchService {

    Long create(Long requestMemberId, Long responseMemberId);

    List<Match> findAllByUserId(Long userId);
}
