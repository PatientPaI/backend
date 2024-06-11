package com.patientpal.backend.patient.dto.request;

import com.patientpal.backend.member.domain.Address;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.patient.domain.Patient;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PatientProfileCreateRequest {

    @NotNull
    private String name;

    @NotNull
    private String residentRegistrationNumber;

    //TODO 인증
    @NotNull
    private String phoneNumber;

    @NotNull
    private Address address;

    private String nokName;

    private String nokContact;

    @NotNull
    private String patientSignificant;

    private String careRequirements;

    public Patient toEntity(Member member) {
        return Patient.builder()
                .name(this.name)
                .residentRegistrationNumber(this.residentRegistrationNumber)
                .member(member)
                .phoneNumber(this.phoneNumber)
                .address(this.address)
                .nokName(this.nokName)
                .nokContact(this.nokContact)
                .patientSignificant(this.patientSignificant)
                .careRequirements(this.careRequirements)
                .isInMatchList(false)
                .build();
    }
}
