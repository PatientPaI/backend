package com.patientpal.backend.patient.dto.response;

import com.patientpal.backend.member.domain.Address;
import com.patientpal.backend.member.domain.Gender;
import com.patientpal.backend.patient.domain.Patient;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PatientProfileDetailResponse {

    private Long memberId;

    private String name;

    private String residentRegistrationNumber;

    private int age;

    private String phoneNumber;

    private Gender gender;

    private Address address;

    private String nokName;

    private String nokContact;

    private String patientSignificant;

    private String careRequirements;

    private Boolean isInMatchList;

    private String image;

    @Builder
    public PatientProfileDetailResponse(Long memberId, String name, String residentRegistrationNumber, String phoneNumber, Gender gender, int age, Address address, String nokName, String nokContact,
                                        String patientSignificant, String careRequirements, Boolean isInMatchList, String image) {
        this.memberId = memberId;
        this.name = name;
        this.residentRegistrationNumber = residentRegistrationNumber;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.age = age;
        this.address = address;
        this.nokName = nokName;
        this.nokContact = nokContact;
        this.patientSignificant = patientSignificant;
        this.careRequirements = careRequirements;
        this.isInMatchList = isInMatchList;
        this.image = image;
    }

    public static PatientProfileDetailResponse of(Patient patient) {
        return PatientProfileDetailResponse.builder()
                .memberId(patient.getMember().getId())
                .name(patient.getName())
                .residentRegistrationNumber(patient.getResidentRegistrationNumber())
                .age(patient.getAge())
                .phoneNumber(patient.getPhoneNumber())
                .gender(patient.getGender())
                .address(patient.getAddress())
                .nokName(patient.getNokName())
                .nokContact(patient.getNokContact())
                .patientSignificant(patient.getPatientSignificant())
                .careRequirements(patient.getCareRequirements())
                .isInMatchList(patient.getIsInMatchList())
                .image(patient.getProfileImageUrl())
                .build();
    }
}
