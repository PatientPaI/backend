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

    private String contact;

    private Gender gender;

    private Address address;

    private Boolean isNok;

    private String nokName;

    private String nokContact;

    private String realCarePlace;

    private String patientSignificant;

    private String careRequirements;

    private Boolean isProfilePublic;

    private String image;

    private long viewCount;

    @Builder
    public PatientProfileDetailResponse(Long memberId, String name, String residentRegistrationNumber, int age,
                                        String contact, Gender gender, Address address, Boolean isNok, String nokName,
                                        String nokContact, String realCarePlace, String patientSignificant,
                                        String careRequirements, Boolean isProfilePublic, String image, long viewCount) {
        this.memberId = memberId;
        this.name = name;
        this.residentRegistrationNumber = residentRegistrationNumber;
        this.age = age;
        this.contact = contact;
        this.gender = gender;
        this.address = address;
        this.isNok = isNok;
        this.nokName = nokName;
        this.nokContact = nokContact;
        this.realCarePlace = realCarePlace;
        this.patientSignificant = patientSignificant;
        this.careRequirements = careRequirements;
        this.isProfilePublic = isProfilePublic;
        this.image = image;
        this.viewCount = viewCount;
    }

    public static PatientProfileDetailResponse of(Patient patient, long profileViewCount) {
        return PatientProfileDetailResponse.builder()
                .memberId(patient.getId())
                .name(patient.getName())
                .residentRegistrationNumber(patient.getResidentRegistrationNumber())
                .age(patient.getAge())
                .contact(patient.getContact())
                .gender(patient.getGender())
                .address(patient.getAddress())
                .isNok(patient.getIsNok())
                .realCarePlace(patient.getRealCarePlace())
                .nokName(patient.getNokName())
                .nokContact(patient.getNokContact())
                .patientSignificant(patient.getPatientSignificant())
                .careRequirements(patient.getCareRequirements())
                .isProfilePublic(patient.getIsProfilePublic())
                .image(patient.getProfileImageUrl())
                .viewCount(profileViewCount)
                .build();
    }
}
