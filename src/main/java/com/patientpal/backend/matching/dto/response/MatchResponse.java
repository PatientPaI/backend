package com.patientpal.backend.matching.dto.response;

import com.patientpal.backend.matching.domain.FirstRequest;
import com.patientpal.backend.matching.domain.Match;
import com.patientpal.backend.matching.domain.MatchStatus;
import com.patientpal.backend.matching.domain.ReadStatus;
import com.patientpal.backend.member.domain.Member;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MatchResponse {

    private Long id;
    private Long requestMemberId;
    private Long receivedMemberId;
    private LocalDateTime createdDate;
    private MatchStatus matchStatus;
    private ReadStatus readStatus;
    private FirstRequest firstRequest;
    private String patientProfileSnapshot;
    private String caregiverProfileSnapshot;

    @Builder
    public MatchResponse(Long id, Long requestMemberId, Long receivedMemberId, LocalDateTime createdDate, MatchStatus matchStatus, ReadStatus readStatus, FirstRequest firstRequest,
                         String patientProfileSnapshot, String caregiverProfileSnapshot) {
        this.id = id;
        this.requestMemberId = requestMemberId;
        this.receivedMemberId = receivedMemberId;
        this.createdDate = createdDate;
        this.matchStatus = matchStatus;
        this.readStatus = readStatus;
        this.firstRequest = firstRequest;
        this.patientProfileSnapshot = patientProfileSnapshot;
        this.caregiverProfileSnapshot = caregiverProfileSnapshot;
    }

    public static MatchResponse of(Match match) {
        return MatchResponse.builder()
                .id(match.getId())
                .requestMemberId(match.getRequestMember().getId())
                .receivedMemberId(match.getReceivedMember().getId())
                .matchStatus(match.getMatchStatus())
                .readStatus(match.getReadStatus())
                .firstRequest(match.getFirstRequest())
                .caregiverProfileSnapshot(match.getCaregiverProfileSnapshot())
                .patientProfileSnapshot(match.getPatientProfileSnapshot())
                .createdDate(match.getCreatedDate())
                .build();
    }

    public static Match toEntityFirstPatient(Member patient, Member caregiver, String generatedPatientProfileSnapshot) {
        return Match.builder()
                .requestMember(patient)
                .receivedMember(caregiver)
                .matchStatus(MatchStatus.PENDING)
                .readStatus(ReadStatus.UNREAD)
                .firstRequest(FirstRequest.PATIENT_FIRST)
                .patientProfileSnapshot(generatedPatientProfileSnapshot)
                .build();
    }

    public static Match toEntityFirstCaregiver(Member caregiver, Member patient, String generatedCaregiverProfileSnapshot) {
        return Match.builder()
                .requestMember(caregiver)
                .receivedMember(patient)
                .matchStatus(MatchStatus.PENDING)
                .readStatus(ReadStatus.UNREAD)
                .firstRequest(FirstRequest.CAREGIVER_FIRST)
                .caregiverProfileSnapshot(generatedCaregiverProfileSnapshot)
                .build();
    }


}
