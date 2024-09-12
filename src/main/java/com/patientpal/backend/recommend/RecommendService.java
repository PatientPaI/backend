package com.patientpal.backend.recommend;

import com.patientpal.backend.caregiver.dto.response.CaregiverProfileResponse;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.patient.dto.response.PatientProfileResponse;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RecommendService {

    private final RecommendStrategy recommendStrategy;

    public List<CaregiverProfileResponse> getTopCaregiversByRecommend(String username) {
        return Optional.ofNullable(recommendStrategy.recommendCaregivers(username))
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CAREGIVER_NOT_EXIST));
    }

    public List<PatientProfileResponse> getTopPatientsByRecommend(String username) {
        return Optional.ofNullable(recommendStrategy.recommendPatients(username))
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PATIENT_NOT_EXIST));
    }
}
