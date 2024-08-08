package com.patientpal.backend.caregiver.repository;

import com.patientpal.backend.caregiver.dto.response.CaregiverProfileResponse;
import com.patientpal.backend.patient.dto.response.PatientProfileResponse;
import com.patientpal.backend.common.querydsl.ProfileSearchCondition;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CaregiverProfileSearchRepositoryCustom {

    Slice<PatientProfileResponse> searchPatientProfilesByReviewCounts(ProfileSearchCondition condition, Long lastIndex, Integer lastReviewCounts, Pageable pageable);
    Slice<PatientProfileResponse> searchPatientProfilesByViewCounts(ProfileSearchCondition condition, Long lastIndex, Integer lastViews, Pageable pageable);
    Slice<PatientProfileResponse> searchPageOrderByDefault(ProfileSearchCondition condition, Long lastIndex, LocalDateTime lastProfilePublicTime, Pageable pageable);
}
