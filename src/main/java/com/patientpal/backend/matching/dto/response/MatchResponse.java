package com.patientpal.backend.matching.dto.response;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.matching.domain.FirstRequest;
import com.patientpal.backend.matching.domain.Match;
import com.patientpal.backend.matching.domain.MatchStatus;
import com.patientpal.backend.matching.domain.ReadStatus;
import com.patientpal.backend.matching.dto.request.CreateMatchCaregiverRequest;
import com.patientpal.backend.matching.dto.request.CreateMatchPatientRequest;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.patient.domain.Patient;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MatchResponse {

    private Long id;
    private String requestMemberName;
    private String receivedMemberName;
    private LocalDateTime createdDate;
    private MatchStatus matchStatus;
    private ReadStatus readStatus;
    private FirstRequest firstRequest;
    private LocalDateTime careStartDateTime;
    private LocalDateTime careEndDateTime;
    private Long totalAmount;
    private String requestMemberCurrentSignificant;
    private Boolean isNok;
    private String nokName;
    private String nokContact;

    @Builder
    public MatchResponse(Long id, String requestMemberName, String receivedMemberName, LocalDateTime createdDate,
                         MatchStatus matchStatus, ReadStatus readStatus, FirstRequest firstRequest,
                         LocalDateTime careStartDateTime, LocalDateTime careEndDateTime, Long totalAmount,
                         String requestMemberCurrentSignificant, Boolean isNok, String nokName, String nokContact) {
        this.id = id;
        this.requestMemberName = requestMemberName;
        this.receivedMemberName = receivedMemberName;
        this.createdDate = createdDate;
        this.matchStatus = matchStatus;
        this.readStatus = readStatus;
        this.firstRequest = firstRequest;
        this.careStartDateTime = careStartDateTime;
        this.careEndDateTime = careEndDateTime;
        this.totalAmount = totalAmount;
        this.requestMemberCurrentSignificant = requestMemberCurrentSignificant;
        this.isNok = isNok;
        this.nokName = nokName;
        this.nokContact = nokContact;
    }

    public static MatchResponse of(Match match) {
        return MatchResponse.builder()
                .id(match.getId())
                .requestMemberName(match.getRequestMemberName())
                .receivedMemberName(match.getReceivedMemberName())
                .matchStatus(match.getMatchStatus())
                .readStatus(match.getReadStatus())
                .firstRequest(match.getFirstRequest())
                .careStartDateTime(match.getCareStartDateTime())
                .careEndDateTime(match.getCareEndDateTime())
                .totalAmount(match.getTotalAmount())
                .requestMemberCurrentSignificant(match.getRequestMemberCurrentSignificant())
                .createdDate(match.getCreatedDate())
                .isNok(match.getIsNok())
                .nokName(match.getNokName())
                .nokContact(match.getNokContact())
                .build();
    }

    public static Match toEntityFirstPatient(Patient patient, Caregiver caregiver, CreateMatchPatientRequest createMatchRequest) {
        return Match.builder()
                .requestMember(patient)
                .receivedMember(caregiver)
                .matchStatus(MatchStatus.PENDING)
                .readStatus(ReadStatus.UNREAD)
                .firstRequest(FirstRequest.PATIENT_FIRST)
                .careStartDateTime(createMatchRequest.getCareStartDateTime())
                .careEndDateTime(createMatchRequest.getCareEndDateTime())
                .totalAmount(createMatchRequest.getTotalAmount())
                .requestMemberCurrentSignificant(createMatchRequest.getSignificant())
                .isNok(patient.getIsNok())
                .nokName(patient.getNokName())
                .nokContact(patient.getNokContact())
                .requestMemberName(patient.getName())
                .receivedMemberName(caregiver.getName())
                .build();
    }

    public static Match toEntityFirstCaregiver(Caregiver caregiver, Patient patient, CreateMatchCaregiverRequest createMatchRequest) {
        return Match.builder()
                .requestMember(caregiver)
                .receivedMember(patient)
                .matchStatus(MatchStatus.PENDING)
                .readStatus(ReadStatus.UNREAD)
                .firstRequest(FirstRequest.CAREGIVER_FIRST)
                .careStartDateTime(createMatchRequest.getCareStartDateTime())
                .careEndDateTime(createMatchRequest.getCareEndDateTime())
                .totalAmount(createMatchRequest.getTotalAmount())
                .requestMemberCurrentSignificant(createMatchRequest.getSignificant())
                .isNok(patient.getIsNok())
                .nokName(patient.getNokName())
                .nokContact(patient.getNokContact())
                .requestMemberName(caregiver.getName())
                .receivedMemberName(patient.getName())
                .build();
    }


}
