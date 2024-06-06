package com.patientpal.backend.caregiver.dto.request;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.member.domain.Address;
import com.patientpal.backend.member.domain.Member;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CaregiverProfileCreateRequest {

    @NotNull(message = "필수 입력 사항입니다.")
    private String name;

    @NotNull(message = "필수 입력 사항입니다.")
    private String residentRegistrationNumber;

    //TODO 인증
    @NotNull(message = "필수 입력 사항입니다.")
    private String phoneNumber;

    @NotNull(message = "필수 입력 사항입니다.")
    private Address address;

    private float rating;

    private int experienceYears;

    private String specialization;

    private String caregiverSignificant;

    public Caregiver toEntity(Member member) {
        return Caregiver.builder()
                .name(this.name)
                .residentRegistrationNumber(this.residentRegistrationNumber)
                .member(member)
                .address(this.address)
                .phoneNumber(this.phoneNumber)
                .rating(this.rating)
                .experienceYears(this.experienceYears)
                .specialization(this.specialization)
                .caregiverSignificant(this.caregiverSignificant)
                .build();
    }
}
