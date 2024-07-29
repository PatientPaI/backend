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
    private String requestMemberContact;
    private String receivedMemberContact;
    private String requestMemberAddress;
    private String receivedMemberAddress;
    private LocalDateTime createdDate;
    private MatchStatus matchStatus;
    private ReadStatus readStatus;
    private FirstRequest firstRequest;
    private String realCarePlace;
    private LocalDateTime careStartDateTime;
    private LocalDateTime careEndDateTime;
    private Long totalAmount;
    private String requestMemberCurrentSignificant;
    private Boolean isNok;
    private String nokName;
    private String nokContact;

    @Builder
    public MatchResponse(Long id, String requestMemberName, String receivedMemberName, String requestMemberContact,
                         String receivedMemberContact, String requestMemberAddress, String receivedMemberAddress,
                         LocalDateTime createdDate, MatchStatus matchStatus, ReadStatus readStatus,
                         FirstRequest firstRequest, String realCarePlace, LocalDateTime careStartDateTime,
                         LocalDateTime careEndDateTime, Long totalAmount, String requestMemberCurrentSignificant,
                         Boolean isNok, String nokName, String nokContact) {
        this.id = id;
        this.requestMemberName = requestMemberName;
        this.receivedMemberName = receivedMemberName;
        this.requestMemberContact = requestMemberContact;
        this.receivedMemberContact = receivedMemberContact;
        this.requestMemberAddress = requestMemberAddress;
        this.receivedMemberAddress = receivedMemberAddress;
        this.createdDate = createdDate;
        this.matchStatus = matchStatus;
        this.readStatus = readStatus;
        this.firstRequest = firstRequest;
        this.realCarePlace = realCarePlace;
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
                .requestMemberName(match.getRequestMember().getName())
                .receivedMemberName(match.getReceivedMember().getName())
                .requestMemberContact(match.getRequestMember().getContact())
                .receivedMemberContact(match.getReceivedMember().getContact())
                .requestMemberAddress(match.getRequestMember().getAddress().toString())
                .receivedMemberAddress(match.getReceivedMember().getAddress().toString())
                .matchStatus(match.getMatchStatus())
                .readStatus(match.getReadStatus())
                .firstRequest(match.getFirstRequest())
                .realCarePlace(match.getRealCarePlace())
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
                .isNok(createMatchRequest.getIsNok())
                .realCarePlace(createMatchRequest.getRealCarePlace())
                .nokName(patient.getNokName())
                .nokContact(patient.getNokContact())
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
                .build();
    }


}
