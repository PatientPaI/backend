package com.patientpal.backend.caregiver.dto.request;

import com.patientpal.backend.member.domain.Address;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CaregiverProfileUpdateRequest {

    @NotNull
    private Address address;

    private float rating;

    private int experienceYears;

    private String specialization;

    private String caregiverSignificant;

    @Builder
    public CaregiverProfileUpdateRequest(Address address, float rating, int experienceYears, String specialization,
                                         String caregiverSignificant) {
        this.address = address;
        this.rating = rating;
        this.experienceYears = experienceYears;
        this.specialization = specialization;
        this.caregiverSignificant = caregiverSignificant;
    }
}
