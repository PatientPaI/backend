package com.patientpal.backend.patient.dto.request;

import com.patientpal.backend.member.domain.Address;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
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

    @NotNull
    private String realCarePlace;

    @NotNull
    private Boolean isNok;

    private LocalDateTime wantCareStartDate;

    private LocalDateTime wantCareEndDate;

    @Builder
    public PatientProfileUpdateRequest(Address address, String nokName, String nokContact, String patientSignificant,
                                       String careRequirements, String realCarePlace, Boolean isNok,
                                       LocalDateTime wantCareStartDate, LocalDateTime wantCareEndDate) {
        this.address = address;
        this.nokName = nokName;
        this.nokContact = nokContact;
        this.patientSignificant = patientSignificant;
        this.careRequirements = careRequirements;
        this.realCarePlace = realCarePlace;
        this.isNok = isNok;
        this.wantCareStartDate = wantCareStartDate;
        this.wantCareEndDate = wantCareEndDate;
    }
}
