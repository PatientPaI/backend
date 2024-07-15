package com.patientpal.backend.matching.dto.response;

import com.patientpal.backend.matching.domain.Match;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReceivedMatchListResponse {
    private List<ReceivedMatchResponse> matchList;
    private int currentPage;
    private int totalPages;
    private long totalItems;

    public ReceivedMatchListResponse(List<ReceivedMatchResponse> matchList, int currentPage, int totalPages, long totalItems) {
        this.matchList = matchList;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalItems = totalItems;
    }

    public static ReceivedMatchListResponse from(Page<Match> matchPage, List<ReceivedMatchResponse> matchList) {
        return new ReceivedMatchListResponse(matchList, matchPage.getNumber(), matchPage.getTotalPages(), matchPage.getTotalElements());
    }
}
