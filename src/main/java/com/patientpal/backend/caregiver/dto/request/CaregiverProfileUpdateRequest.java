package com.patientpal.backend.caregiver.dto.request;

import com.patientpal.backend.member.domain.Address;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
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
}
