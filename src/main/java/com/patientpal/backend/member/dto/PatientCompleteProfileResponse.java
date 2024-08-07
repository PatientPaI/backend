package com.patientpal.backend.member.dto;

import com.patientpal.backend.member.domain.Address;
import com.patientpal.backend.member.domain.Gender;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PatientCompleteProfileResponse {

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

    private Boolean isCompleteProfile;

    private Boolean isProfilePublic;

    private String image;

    private long viewCount;

    private LocalDateTime wantCareStartDate;

    private LocalDateTime wantCareEndDate;

    @Builder
    public PatientCompleteProfileResponse(Long memberId, String name, Integer age, String contact, Gender gender,
                                          Address address, Boolean isNok, String nokName, String nokContact,
                                          String realCarePlace, String patientSignificant, String careRequirements,
                                          Boolean isCompleteProfile, Boolean isProfilePublic, String image, long viewCount,
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
        this.isCompleteProfile = isCompleteProfile;
        this.isProfilePublic = isProfilePublic;
        this.image = image;
        this.viewCount = viewCount;
        this.wantCareStartDate = wantCareStartDate;
        this.wantCareEndDate = wantCareEndDate;
    }


    public static PatientCompleteProfileResponse of(Long memberId, String name, Integer age, String contact, Gender gender,
                                                   Address address, Boolean isNok, String nokName, String nokContact,
                                                   String realCarePlace, String patientSignificant, String careRequirements,
                                                   Boolean isCompleteProfile, Boolean isProfilePublic, String image, long viewCount,
                                                   LocalDateTime wantCareStartDate, LocalDateTime wantCareEndDate) {
        return PatientCompleteProfileResponse.builder()
                .memberId(memberId)
                .name(name)
                .age(age)
                .contact(contact)
                .gender(gender)
                .address(address)
                .isNok(isNok)
                .nokName(nokName)
                .nokContact(nokContact)
                .realCarePlace(realCarePlace)
                .patientSignificant(patientSignificant)
                .careRequirements(careRequirements)
                .isCompleteProfile(isCompleteProfile)
                .isProfilePublic(isProfilePublic)
                .image(image)
                .viewCount(viewCount)
                .wantCareStartDate(wantCareStartDate)
                .wantCareEndDate(wantCareEndDate)
                .build();
    }
}
