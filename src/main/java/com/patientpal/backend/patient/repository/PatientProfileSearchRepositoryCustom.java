package com.patientpal.backend.patient.repository;

import com.patientpal.backend.caregiver.dto.response.CaregiverProfileResponse;
import com.patientpal.backend.common.querydsl.ProfileSearchCondition;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface PatientProfileSearchRepositoryCustom {

    Slice<CaregiverProfileResponse> searchCaregiverProfilesByReviewCounts(ProfileSearchCondition condition, Long lastIndex, Integer lastReviewCounts, Pageable pageable);
    Slice<CaregiverProfileResponse> searchCaregiverProfilesByViewCounts(ProfileSearchCondition condition, Long lastIndex, Integer lastViews, Pageable pageable);
    Slice<CaregiverProfileResponse> searchPageOrderByDefault(ProfileSearchCondition condition, Long lastIndex, LocalDateTime lastProfilePublicTime, Pageable pageable);
}
