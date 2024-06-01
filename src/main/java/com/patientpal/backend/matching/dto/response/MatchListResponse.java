package com.patientpal.backend.matching.dto.response;

import com.patientpal.backend.matching.domain.Match;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MatchListResponse {
    private List<MatchResponse> matchList;
    private int currentPage;
    private int totalPages;
    private long totalItems;

    public MatchListResponse(List<MatchResponse> matchList, int currentPage, int totalPages, long totalItems) {
        this.matchList = matchList;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalItems = totalItems;
    }

    public static MatchListResponse from(Page<Match> matchPage, List<MatchResponse> matchList) {
        return new MatchListResponse(matchList, matchPage.getNumber(), matchPage.getTotalPages(), matchPage.getTotalElements());
    }
}
