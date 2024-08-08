package com.patientpal.backend.patient.dto.response;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PatientRecommendListResponse {
    private List<PatientProfileResponse> patients;

    public PatientRecommendListResponse(List<PatientProfileResponse> patients) {
        this.patients = patients;
    }
}
