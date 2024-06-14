package com.patientpal.backend.caregiver.dto.response;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.member.domain.Address;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CaregiverProfileResponse {

    private Long memberId;

    private String name;

    private String residentRegistrationNumber;

    private String phoneNumber;

    private Address address;

    private float rating;

    private int experienceYears;

    private String specialization;

    private String caregiverSignificant;

    private Boolean isInMatchList;

    private String image;

    @Builder
    public CaregiverProfileResponse(Long memberId, String name, String residentRegistrationNumber, String phoneNumber, Address address, float rating, int experienceYears,
                                    String specialization, String caregiverSignificant, Boolean isInMatchList, String image) {
        this.memberId = memberId;
        this.name = name;
        this.residentRegistrationNumber = residentRegistrationNumber;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.rating = rating;
        this.experienceYears = experienceYears;
        this.specialization = specialization;
        this.caregiverSignificant = caregiverSignificant;
        this.isInMatchList = isInMatchList;
        this.image = image;
    }

    public static CaregiverProfileResponse of(Caregiver caregiver) {
        return CaregiverProfileResponse.builder()
                .memberId(caregiver.getMember().getId())
                .name(caregiver.getName())
                .residentRegistrationNumber(caregiver.getResidentRegistrationNumber())
                .phoneNumber(caregiver.getPhoneNumber())
                .address(caregiver.getAddress())
                .rating(caregiver.getRating())
                .experienceYears(caregiver.getExperienceYears())
                .specialization(caregiver.getSpecialization())
                .caregiverSignificant(caregiver.getCaregiverSignificant())
                .isInMatchList(caregiver.getIsInMatchList())
                .image(caregiver.getProfileImageUrl())
                .build();
    }
}
