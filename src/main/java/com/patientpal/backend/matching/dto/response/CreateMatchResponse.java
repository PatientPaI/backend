package com.patientpal.backend.matching.dto.response;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.member.domain.Address;
import com.patientpal.backend.patient.domain.Patient;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateMatchResponse {

    private String patientName;

    private Integer patientAge;

    private String patientContact;

    private Address patientAddress;

    private String patientSignificant;

    private String patientRealCarePlace;

    private Boolean isNok;

    private String nokName;

    private String nokContact;

    private String caregiverName;

    private Integer caregiverAge;

    private String caregiverContact;

    private Address caregiverAddress;

    private String caregiverSignificant;

    private int caregiverExperienceYears;

    @Builder
    public CreateMatchResponse(String patientName, Integer patientAge, String patientContact, Address patientAddress,
                               String patientSignificant, String patientRealCarePlace, Boolean isNok, String nokName, String nokContact,
                               String caregiverName, Integer caregiverAge, String caregiverContact, Address caregiverAddress,
                               String caregiverSignificant, int caregiverExperienceYears) {
        this.patientName = patientName;
        this.patientAge = patientAge;
        this.patientContact = patientContact;
        this.patientAddress = patientAddress;
        this.patientSignificant = patientSignificant;
        this.patientRealCarePlace = patientRealCarePlace;
        this.isNok = isNok;
        this.nokName = nokName;
        this.nokContact = nokContact;
        this.caregiverName = caregiverName;
        this.caregiverAge = caregiverAge;
        this.caregiverContact = caregiverContact;
        this.caregiverAddress = caregiverAddress;
        this.caregiverSignificant = caregiverSignificant;
        this.caregiverExperienceYears = caregiverExperienceYears;
    }



    public static CreateMatchResponse of(Patient patient, Caregiver caregiver) {
        return CreateMatchResponse.builder()
                .patientName(patient.getName())
                .patientAge(patient.getAge())
                .patientContact(patient.getContact())
                .patientAddress(patient.getAddress())
                .patientSignificant(patient.getPatientSignificant())
                .patientRealCarePlace(patient.getRealCarePlace())
                .isNok(patient.getIsNok())
                .nokName(patient.getNokName())
                .nokContact(patient.getNokContact())
                .caregiverName(caregiver.getName())
                .caregiverAge(caregiver.getAge())
                .caregiverContact(caregiver.getContact())
                .caregiverAddress(caregiver.getAddress())
                .caregiverSignificant(caregiver.getCaregiverSignificant())
                .caregiverExperienceYears(caregiver.getExperienceYears())
                .build();
    }
}
