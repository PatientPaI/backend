package com.patientpal.backend.patient.dto.response;

import com.patientpal.backend.member.domain.Address;
import com.patientpal.backend.member.domain.Gender;
import com.patientpal.backend.patient.domain.Patient;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PatientProfileDetailResponse {

    private Long memberId;

    private String name;

    private Integer age;

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

    private LocalDateTime wantCareStartDate;

    private LocalDateTime wantCareEndDate;

    @Builder
    public PatientProfileDetailResponse(Long memberId, String name, Integer age,
                                        String contact, Gender gender, Address address, Boolean isNok, String nokName,
                                        String nokContact, String realCarePlace, String patientSignificant,
                                        String careRequirements, Boolean isProfilePublic, String image, long viewCount,
                                        LocalDateTime wantCareStartDate, LocalDateTime wantCareEndDate) {
        this.memberId = memberId;
        this.name = name;
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
        this.wantCareStartDate = wantCareStartDate;
        this.wantCareEndDate = wantCareEndDate;
    }

    public static PatientProfileDetailResponse of(Patient patient) {
        return PatientProfileDetailResponse.builder()
                .memberId(patient.getId())
                .name(patient.getName())
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
                .viewCount(patient.getViewCounts())
                .wantCareStartDate(patient.getWantCareStartDate())
                .wantCareEndDate(patient.getWantCareEndDate())
                .build();
    }
}
