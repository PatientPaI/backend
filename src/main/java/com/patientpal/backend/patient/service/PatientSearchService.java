package com.patientpal.backend.patient.service;

import com.patientpal.backend.caregiver.dto.response.CaregiverProfileListResponse;
import com.patientpal.backend.caregiver.dto.response.CaregiverProfileResponse;
import com.patientpal.backend.common.querydsl.ProfileSearchCondition;
import com.patientpal.backend.patient.repository.PatientRepository;
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
@Timed("patient.search")
@Transactional(readOnly = true)
@Slf4j
public class PatientSearchService {

    private final PatientRepository patientRepository;

    @Cacheable(value = "caregiverProfiles", key = "'caregiver_search_views' + #condition.addr ?: 'all' + '_' + #condition.gender ?: 'all' + '_' + #condition.ageLoe ?: 'all' + '_page_' + #pageable.pageNumber", unless = "#result == null", cacheManager = "cacheManager")
    public CaregiverProfileListResponse searchPageOrderByViews(ProfileSearchCondition condition, Long lastIndex, Integer lastViewCounts, Pageable pageable) {
        Slice<CaregiverProfileResponse> searchWithViews = patientRepository.searchCaregiverProfilesByViewCounts(condition, lastIndex, lastViewCounts, pageable);
        return CaregiverProfileListResponse.from(searchWithViews);
    }
    //
    // public CaregiverProfileListResponse searchPageOrderByReviewCounts(ProfileSearchCondition condition, Long lastIndex, Integer reviewCounts, Pageable pageable) {
    //
    //     Slice<CaregiverProfileResponse> searchByReviewCounts = patientRepository.searchCaregiverProfilesByReviewCounts(condition, lastIndex, reviewCounts, pageable);
    //     return CaregiverProfileListResponse.from(searchByReviewCounts);
    // }

    @Cacheable(value = "caregiverProfiles", key = "'caregiver_search_default' + #condition.addr ?: 'all' + '_' + #condition.gender ?: 'all' + '_' + #condition.ageLoe ?: 'all' + '_page_' + #pageable.pageNumber", unless = "#result == null", cacheManager = "cacheManager")
    public CaregiverProfileListResponse searchPageOrderByDefault(ProfileSearchCondition condition, Long lastIndex, LocalDateTime lastProfilePublicTime, Pageable pageable) {
        Slice<CaregiverProfileResponse> search = patientRepository.searchPageOrderByDefault(condition, lastIndex, lastProfilePublicTime, pageable);
        return CaregiverProfileListResponse.from(search);
    }
}
