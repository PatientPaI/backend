package com.patientpal.backend.matching.dto.response;

import com.patientpal.backend.matching.domain.MatchStatus;
import com.patientpal.backend.matching.domain.ReadStatus;
import com.patientpal.backend.member.domain.Caregiver;
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
    private Patient patient;
    private Caregiver caregiver;
    private LocalDateTime createdDate;
    private MatchStatus matchStatus;
    private ReadStatus readStatus;
    private String patientProfileSnapshot;
    private String caregiverProfileSnapshot;

    @Builder
    public MatchResponse(Long id, Patient patient, Caregiver caregiver, LocalDateTime createdDate, MatchStatus matchStatus, ReadStatus readStatus,
                         String patientProfileSnapshot, String caregiverProfileSnapshot) {
        this.id = id;
        this.patient = patient;
        this.caregiver = caregiver;
        this.createdDate = createdDate;
        this.matchStatus = matchStatus;
        this.readStatus = readStatus;
        this.patientProfileSnapshot = patientProfileSnapshot;
        this.caregiverProfileSnapshot = caregiverProfileSnapshot;
    }
}
