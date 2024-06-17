package com.patientpal.backend.patient.dto.request;

import com.patientpal.backend.member.domain.Address;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PatientProfileUpdateRequest {

    @NotNull
    private Address address;

    private String nokName;

    private String nokContact;

    @NotNull
    private String patientSignificant;

    private String careRequirements;

    @Builder
    public PatientProfileUpdateRequest(Address address, String nokName, String nokContact, String patientSignificant, String careRequirements) {
        this.address = address;
        this.nokName = nokName;
        this.nokContact = nokContact;
        this.patientSignificant = patientSignificant;
        this.careRequirements = careRequirements;
    }
}
