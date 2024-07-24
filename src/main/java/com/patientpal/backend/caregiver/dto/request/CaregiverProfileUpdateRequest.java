package com.patientpal.backend.caregiver.dto.request;

import com.patientpal.backend.member.domain.Address;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
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

    @NotNull
    private Integer age;

    private String specialization;

    private String caregiverSignificant;

    private LocalDateTime wantCareStartDate;

    private LocalDateTime wantCareEndDate;

    @Builder
    public CaregiverProfileUpdateRequest(Address address, float rating, int experienceYears, String specialization, Integer age,
                                         String caregiverSignificant, LocalDateTime wantCareStartDate, LocalDateTime wantCareEndDate) {
        this.address = address;
        this.rating = rating;
        this.experienceYears = experienceYears;
        this.specialization = specialization;
        this.age = age;
        this.caregiverSignificant = caregiverSignificant;
        this.wantCareStartDate = wantCareStartDate;
        this.wantCareEndDate = wantCareEndDate;
    }
}
