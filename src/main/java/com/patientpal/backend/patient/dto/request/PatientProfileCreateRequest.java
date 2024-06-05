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

    @NotNull(message = "필수 입력 사항입니다.")
    private String name;

    @NotNull(message = "필수 입력 사항입니다.")
    private String residentRegistrationNumber;

    @NotNull(message = "필수 입력 사항입니다.")
    private Address address;

    private String nokName;

    private String nokContact;

    @NotNull(message = "필수 입력 사항입니다.")
    private String patientSignificant;

    private String careRequirements;

    public Patient toEntity(Member member) {
        return Patient.builder()
                .name(this.name)
                .residentRegistrationNumber(this.residentRegistrationNumber)
                .member(member)
                .address(this.address)
                .nokName(this.nokName)
                .nokContact(this.nokContact)
                .patientSignificant(this.patientSignificant)
                .careRequirements(this.careRequirements)
                .build();
    }
}
