package com.patientpal.backend.matching.dto.response;

import com.patientpal.backend.matching.domain.Match;
import com.patientpal.backend.matching.domain.MatchStatus;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReceivedMatchResponse {
    private Long matchId;
    private String requestMemberName;
    private LocalDateTime createdDate;
    private MatchStatus matchStatus;
    private LocalDateTime careStartDateTime;
    private LocalDateTime careEndDateTime;
    private Long totalAmount;

    @Builder
    public ReceivedMatchResponse(Long matchId, String requestMemberName, LocalDateTime createdDate, MatchStatus matchStatus,
                                LocalDateTime careStartDateTime, LocalDateTime careEndDateTime,
                                Long totalAmount) {
        this.matchId = matchId;
        this.requestMemberName = requestMemberName;
        this.createdDate = createdDate;
        this.matchStatus = matchStatus;
        this.careStartDateTime = careStartDateTime;
        this.careEndDateTime = careEndDateTime;
        this.totalAmount = totalAmount;
    }

    public static ReceivedMatchResponse of(Match match) {
        return ReceivedMatchResponse.builder()
                .matchId(match.getId())
                .requestMemberName(match.getRequestMember().getName())
                .matchStatus(match.getMatchStatus())
                .careStartDateTime(match.getCareStartDateTime())
                .careEndDateTime(match.getCareEndDateTime())
                .totalAmount(match.getTotalAmount())
                .createdDate(match.getCreatedDate())
                .build();
    }
}
