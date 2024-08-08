package com.patientpal.backend.caregiver.service;

import com.patientpal.backend.caregiver.dto.response.CaregiverProfileListResponse;
import com.patientpal.backend.caregiver.dto.response.CaregiverProfileResponse;
import com.patientpal.backend.caregiver.repository.CaregiverRepository;
import com.patientpal.backend.common.querydsl.ProfileSearchCondition;
import com.patientpal.backend.patient.dto.response.PatientProfileListResponse;
import com.patientpal.backend.patient.dto.response.PatientProfileResponse;
import io.micrometer.core.annotation.Timed;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Timed("caregiver.search")
@Transactional(readOnly = true)
@Slf4j
public class CaregiverSearchService {

    private final CaregiverRepository caregiverRepository;

    public PatientProfileListResponse searchPageOrderByViews(ProfileSearchCondition condition, Long lastIndex, Integer lastViewCounts, Pageable pageable) {
        Slice<PatientProfileResponse> searchWithViews = caregiverRepository.searchPatientProfilesByViewCounts(condition, lastIndex, lastViewCounts, pageable);
        return PatientProfileListResponse.from(searchWithViews);
    }

    public PatientProfileListResponse searchPageOrderByReviewCounts(ProfileSearchCondition condition, Long lastIndex, Integer lastReviewCounts, Pageable pageable) {
        Slice<PatientProfileResponse> searchWithReviews = caregiverRepository.searchPatientProfilesByReviewCounts(condition, lastIndex, lastReviewCounts, pageable);
        return PatientProfileListResponse.from(searchWithReviews);
    }

    public PatientProfileListResponse searchPageOrderByDefault(ProfileSearchCondition condition, Long lastIndex, LocalDateTime lastProfilePublicTime, Pageable pageable) {
        Slice<PatientProfileResponse> search = caregiverRepository.searchPageOrderByDefault(condition, lastIndex, lastProfilePublicTime, pageable);
        return PatientProfileListResponse.from(search);
    }
}
