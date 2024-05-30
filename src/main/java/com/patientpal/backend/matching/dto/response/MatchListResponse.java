package com.patientpal.backend.matching.dto.response;

import com.patientpal.backend.matching.domain.Match;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MatchListResponse {
    private List<MatchResponse> matchList;

    public MatchListResponse(List<MatchResponse> matchList) {
        this.matchList = matchList;
    }

    public static MatchListResponse from(List<MatchResponse> matchList) {
        return new MatchListResponse(matchList);
    }
}
