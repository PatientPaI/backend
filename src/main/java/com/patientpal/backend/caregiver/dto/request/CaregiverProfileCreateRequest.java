package com.patientpal.backend.caregiver.dto.request;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.member.domain.Address;
import com.patientpal.backend.member.domain.Gender;
import com.patientpal.backend.member.domain.Member;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CaregiverProfileCreateRequest {

    @NotNull
    private String name;

    @NotNull
    private String residentRegistrationNumber;

    //TODO 인증
    @NotNull
    private String phoneNumber;

    @NotNull
    private Gender gender;

    @NotNull
    private Address address;

    private float rating;

    private int experienceYears;

    private String specialization;

    private String caregiverSignificant;

    @Builder
    public CaregiverProfileCreateRequest(String name, String residentRegistrationNumber, String phoneNumber, Gender gender,
                                         Address address, float rating, int experienceYears, String specialization,
                                         String caregiverSignificant) {
        this.name = name;
        this.residentRegistrationNumber = residentRegistrationNumber;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.address = address;
        this.rating = rating;
        this.experienceYears = experienceYears;
        this.specialization = specialization;
        this.caregiverSignificant = caregiverSignificant;
    }

    public Caregiver toEntity(Member member, String profileImageUrl) {
        return Caregiver.builder()
                .name(this.name)
                .residentRegistrationNumber(this.residentRegistrationNumber)
                .age(Caregiver.getAge(this.residentRegistrationNumber))
                .member(member)
                .gender(this.gender)
                .address(this.address)
                .phoneNumber(this.phoneNumber)
                .rating(this.rating)
                .experienceYears(this.experienceYears)
                .specialization(this.specialization)
                .caregiverSignificant(this.caregiverSignificant)
                .isInMatchList(false)
                .profileImageUrl(profileImageUrl)
                .build();
    }
}
