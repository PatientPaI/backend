package com.patientpal.backend.recommend;

import com.patientpal.backend.caregiver.dto.response.CaregiverProfileResponse;
import com.patientpal.backend.patient.dto.response.PatientProfileResponse;
import java.util.List;

public interface RecommendStrategy {
    List<CaregiverProfileResponse> recommendCaregivers(String username);
    List<PatientProfileResponse> recommendPatients(String username);
}
