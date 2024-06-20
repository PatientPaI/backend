package com.patientpal.backend.patient.repository;

import com.patientpal.backend.caregiver.dto.response.CaregiverProfileResponse;
import com.patientpal.backend.common.querydsl.ProfileSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PatientProfileSearchRepositoryCustom {

    Page<CaregiverProfileResponse> searchCaregiverProfilesOrderBy(ProfileSearchCondition condition, Pageable pageable);
}
