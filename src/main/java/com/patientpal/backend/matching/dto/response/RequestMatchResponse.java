package com.patientpal.backend.matching.dto.response;

import com.patientpal.backend.matching.domain.FirstRequest;
import com.patientpal.backend.matching.domain.Match;
import com.patientpal.backend.matching.domain.MatchStatus;
import com.patientpal.backend.matching.domain.ReadStatus;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMatchResponse {
    private Long matchId;
    private String receivedMemberName;
    private LocalDateTime createdDate;
    private MatchStatus matchStatus;
    private ReadStatus readStatus;
    private LocalDateTime careStartDateTime;
    private LocalDateTime careEndDateTime;
    private Long totalAmount;

    @Builder
    public RequestMatchResponse(Long matchId, String receivedMemberName, LocalDateTime createdDate, MatchStatus matchStatus,
                                ReadStatus readStatus, LocalDateTime careStartDateTime, LocalDateTime careEndDateTime,
                                Long totalAmount) {
        this.matchId = matchId;
        this.receivedMemberName = receivedMemberName;
        this.createdDate = createdDate;
        this.matchStatus = matchStatus;
        this.readStatus = readStatus;
        this.careStartDateTime = careStartDateTime;
        this.careEndDateTime = careEndDateTime;
        this.totalAmount = totalAmount;
    }

    public static RequestMatchResponse of(Match match) {
        return RequestMatchResponse.builder()
                .matchId(match.getId())
                .receivedMemberName(match.getReceivedMember().getName())
                .matchStatus(match.getMatchStatus())
                .readStatus(match.getReadStatus())
                .careStartDateTime(match.getCareStartDateTime())
                .careEndDateTime(match.getCareEndDateTime())
                .totalAmount(match.getTotalAmount())
                .createdDate(match.getCreatedDate())
                .build();
    }
}
