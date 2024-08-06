package com.patientpal.backend.caregiver.service;

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

    @Cacheable(value = "patientProfiles", key = "'patient_search_views' + #condition.addr ?: 'all' + '_' + #condition.gender ?: 'all' + '_' + #condition.ageLoe ?: 'all' + '_page_' + #pageable.pageNumber", unless = "#result == null", cacheManager = "cacheManager")
    public PatientProfileListResponse searchPageOrderByViews(ProfileSearchCondition condition, Long lastIndex, Integer lastViewCounts, Pageable pageable) {
        Slice<PatientProfileResponse> searchWithViews = caregiverRepository.searchPatientProfilesByViewCounts(condition, lastIndex, lastViewCounts, pageable);
        return PatientProfileListResponse.from(searchWithViews);
    }
    //
    // public CaregiverProfileListResponse searchPageOrderByReviewCounts(ProfileSearchCondition condition, Long lastIndex, Integer reviewCounts, Pageable pageable) {
    //
    //     Slice<CaregiverProfileResponse> searchByReviewCounts = patientRepository.searchCaregiverProfilesByReviewCounts(condition, lastIndex, reviewCounts, pageable);
    //     return CaregiverProfileListResponse.from(searchByReviewCounts);
    // }

    @Cacheable(value = "patientProfiles", key = "'patient_search_default' + #condition.addr ?: 'all' + '_' + #condition.gender ?: 'all' + '_' + #condition.ageLoe ?: 'all' + '_page_' + #pageable.pageNumber", unless = "#result == null", cacheManager = "cacheManager")
    public PatientProfileListResponse searchPageOrderByDefault(ProfileSearchCondition condition, Long lastIndex, LocalDateTime lastProfilePublicTime, Pageable pageable) {
        Slice<PatientProfileResponse> search = caregiverRepository.searchPageOrderByDefault(condition, lastIndex, lastProfilePublicTime, pageable);
        return PatientProfileListResponse.from(search);
    }
}
