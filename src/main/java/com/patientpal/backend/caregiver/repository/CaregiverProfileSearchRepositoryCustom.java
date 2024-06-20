package com.patientpal.backend.caregiver.repository;

import com.patientpal.backend.patient.dto.response.PatientProfileResponse;
import com.patientpal.backend.common.querydsl.ProfileSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CaregiverProfileSearchRepositoryCustom {

    Page<PatientProfileResponse> searchPatientProfilesOrderBy(ProfileSearchCondition condition, Pageable pageable);
}
