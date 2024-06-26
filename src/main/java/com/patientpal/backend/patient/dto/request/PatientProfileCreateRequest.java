package com.patientpal.backend.patient.dto.request;

import com.patientpal.backend.member.domain.Address;
import com.patientpal.backend.member.domain.Gender;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PatientProfileCreateRequest {

    @NotNull
    private String name;

    @NotNull
    private String residentRegistrationNumber;

    @NotNull
    private Gender gender;

    //TODO 인증
    @NotNull
    private String contact;

    @NotNull
    private Address address;

    private String nokName;

    private String nokContact;

    @NotNull
    private String patientSignificant;

    private String careRequirements;

    @Builder
    public PatientProfileCreateRequest(String name, String residentRegistrationNumber, String contact, Address address,
                                       String nokName, String nokContact, String patientSignificant, String careRequirements, Gender gender) {
        this.name = name;
        this.residentRegistrationNumber = residentRegistrationNumber;
        this.gender = gender;
        this.contact = contact;
        this.address = address;
        this.nokName = nokName;
        this.nokContact = nokContact;
        this.patientSignificant = patientSignificant;
        this.careRequirements = careRequirements;
    }
}
