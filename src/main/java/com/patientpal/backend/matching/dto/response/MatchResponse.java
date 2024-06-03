package com.patientpal.backend.matching.dto.response;

import com.patientpal.backend.matching.domain.FirstRequest;
import com.patientpal.backend.matching.domain.Match;
import com.patientpal.backend.matching.domain.MatchStatus;
import com.patientpal.backend.matching.domain.ReadStatus;
import com.patientpal.backend.member.domain.Caregiver;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.domain.Patient;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MatchResponse {

    private Long id;
    private Long patientId;
    private Long caregiverId;
    private LocalDateTime createdDate;
    private MatchStatus matchStatus;
    private ReadStatus readStatus;
    private FirstRequest firstRequest;
    private String patientProfileSnapshot;
    private String caregiverProfileSnapshot;

    @Builder
    public MatchResponse(Long id, Long patientId, Long caregiverId, LocalDateTime createdDate, MatchStatus matchStatus, ReadStatus readStatus, FirstRequest firstRequest,
                         String patientProfileSnapshot, String caregiverProfileSnapshot) {
        this.id = id;
        this.patientId = patientId;
        this.caregiverId = caregiverId;
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
                .patientId(match.getPatient().getId())
                .caregiverId(match.getCaregiver().getId())
                .matchStatus(match.getMatchStatus())
                .readStatus(match.getReadStatus())
                .firstRequest(match.getFirstRequest())
                .caregiverProfileSnapshot(match.getCaregiverProfileSnapshot())
                .patientProfileSnapshot(match.getPatientProfileSnapshot())
                .createdDate(match.getCreatedDate())
                .build();
    }

    public Match toEntityFirstPatient(Patient patient, Caregiver caregiver, String generatedPatientProfileSnapshot) {
        return Match.builder()
                .patient(patient)
                .caregiver(caregiver)
                .matchStatus(MatchStatus.PENDING)
                .readStatus(ReadStatus.UNREAD)
                .firstRequest(FirstRequest.PATIENT_FIRST)
                .patientProfileSnapshot(generatedPatientProfileSnapshot)
                .build();
    }

    public Match toEntityFirstCaregiver(Caregiver caregiver, Patient patient, String generatedCaregiverProfileSnapshot) {
        return Match.builder()
                .patient(patient)
                .caregiver(caregiver)
                .matchStatus(MatchStatus.PENDING)
                .readStatus(ReadStatus.UNREAD)
                .firstRequest(FirstRequest.CAREGIVER_FIRST)
                .caregiverProfileSnapshot(generatedCaregiverProfileSnapshot)
                .build();
    }


}
