package com.patientpal.backend.matching.dto.response;

import com.patientpal.backend.matching.domain.Match;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequestMatchListResponse {
    private List<RequestMatchResponse> matchList;
    private int currentPage;
    private int totalPages;
    private long totalItems;

    public RequestMatchListResponse(List<RequestMatchResponse> matchList, int currentPage, int totalPages, long totalItems) {
        this.matchList = matchList;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalItems = totalItems;
    }

    public static RequestMatchListResponse from(Page<Match> matchPage, List<RequestMatchResponse> matchList) {
        return new RequestMatchListResponse(matchList, matchPage.getNumber(), matchPage.getTotalPages(), matchPage.getTotalElements());
    }
}
