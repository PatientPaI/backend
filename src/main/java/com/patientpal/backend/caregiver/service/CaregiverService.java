package com.patientpal.backend.caregiver.service;

import static com.patientpal.backend.member.domain.Member.*;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.caregiver.dto.request.CaregiverProfileCreateRequest;
import com.patientpal.backend.caregiver.dto.request.CaregiverProfileUpdateRequest;
import com.patientpal.backend.caregiver.dto.response.CaregiverProfileDetailResponse;
import com.patientpal.backend.caregiver.dto.response.CaregiverProfileListResponse;
import com.patientpal.backend.caregiver.dto.response.CaregiverProfileResponse;
import com.patientpal.backend.caregiver.repository.CaregiverRepository;
import com.patientpal.backend.common.exception.AuthorizationException;
import com.patientpal.backend.common.exception.BusinessException;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.common.querydsl.ProfileSearchCondition;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.repository.MemberRepository;
import com.patientpal.backend.patient.dto.response.PatientProfileListResponse;
import com.patientpal.backend.patient.dto.response.PatientProfileResponse;
import com.patientpal.backend.view.ViewService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CaregiverService {

    private final CaregiverRepository caregiverRepository;
    private final MemberRepository memberRepository;
    private final ViewService viewService;

    @Transactional
    public CaregiverProfileDetailResponse saveCaregiverProfile(String username, CaregiverProfileCreateRequest caregiverProfileCreateRequest, String profileImageUrl) {
        Member currentMember = getMember(username);
        // TODO 본인 인증 진행, 중복 가입이면 throw
        Caregiver caregiver = caregiverRepository.findById(currentMember.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CAREGIVER_NOT_EXIST));
        caregiver.registerDetailProfile(caregiverProfileCreateRequest.getName(),
                caregiverProfileCreateRequest.getAddress(),
                caregiverProfileCreateRequest.getContact(),
                caregiverProfileCreateRequest.getResidentRegistrationNumber(),
                caregiverProfileCreateRequest.getGender(),
                caregiverProfileCreateRequest.getExperienceYears(),
                caregiverProfileCreateRequest.getSpecialization(),
                caregiverProfileCreateRequest.getCaregiverSignificant(),
                caregiverProfileCreateRequest.getWantCareStartDate(),
                caregiverProfileCreateRequest.getWantCareEndDate(),
                profileImageUrl);
        caregiver.setIsCompleteProfile(true);
        log.info("프로필 등록 성공: ID={}, NAME={}", caregiver.getId(), caregiver.getName());
        return CaregiverProfileDetailResponse.of(caregiver, 0);
    }

    public CaregiverProfileDetailResponse getProfile(String username, Long memberId) {
        Caregiver caregiver = getCaregiverByMemberId(memberId);
        // if (!member.getIsCompleteProfile()) {
        //     throw new IllegalArgumentException("프로필 작성을 먼저 완료해야 다른 회원의 프로필을 볼 수 있습니다.");
        // }
        if (!username.equals(caregiver.getUsername())) {
            viewService.addProfileView(memberId, username);
        }
        long profileViewCount = viewService.getProfileViewCount(memberId);
        return CaregiverProfileDetailResponse.of(caregiver, profileViewCount);
    }

    @Transactional
    public void updateCaregiverProfile(String username, Long memberId, CaregiverProfileUpdateRequest caregiverProfileUpdateRequest, String profileImageUrl) {
        Caregiver caregiver = getCaregiverByMemberId(memberId);
        if (isNotOwner(username, caregiver)) {
            throw new AuthorizationException(ErrorCode.AUTHORIZATION_FAILED);
        }
        String currentProfileImageUrl = caregiver.getProfileImageUrl();

        caregiver.updateDetailProfile(
                caregiverProfileUpdateRequest.getAddress(),
                caregiverProfileUpdateRequest.getRating(),
                caregiverProfileUpdateRequest.getExperienceYears(),
                caregiverProfileUpdateRequest.getSpecialization(),
                caregiverProfileUpdateRequest.getCaregiverSignificant(),
                caregiverProfileUpdateRequest.getWantCareStartDate(),
                caregiverProfileUpdateRequest.getWantCareEndDate()
        );

        if (profileImageUrl == null) {
            caregiver.setProfileImageUrl(null);
            return;
        }

        if (!profileImageUrl.equals(currentProfileImageUrl)) {
            caregiver.updateProfileImage(profileImageUrl);
        }
    }

    @Transactional
    public void registerCaregiverProfileToMatchList(String username, Long memberId) {
        Caregiver caregiver = getCaregiverByMemberId(memberId);
        if (isNotOwner(username, caregiver)) {
            throw new AuthorizationException(ErrorCode.AUTHORIZATION_FAILED);
        }
        if (!caregiver.getIsCompleteProfile()) {
            throw new BusinessException(ErrorCode.PROFILE_NOT_COMPLETED);
        }
        caregiver.setIsProfilePublic(true);
        caregiver.setProfilePublicTime(LocalDateTime.now());
    }

    @Transactional
    public void unregisterCaregiverProfileToMatchList(String username, Long memberId) {
        Caregiver caregiver = getCaregiverByMemberId(memberId);
        if (isNotOwner(username, caregiver)) {
            throw new AuthorizationException(ErrorCode.AUTHORIZATION_FAILED);
        }
        caregiver.setIsProfilePublic(false);
    }

    private Member getMember(String username) {
        return memberRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_EXIST, username));
    }

    private Caregiver getCaregiverByMemberId(Long memberId) {
        return caregiverRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException(ErrorCode.CAREGIVER_NOT_EXIST));
    }

    @Transactional
    public void deleteCaregiverProfileImage(String username, Long memberId) {
        Caregiver caregiver = getCaregiverByMemberId(memberId);
        if (isNotOwner(username, caregiver)) {
            throw new AuthorizationException(ErrorCode.AUTHORIZATION_FAILED);
        }
        caregiver.deleteProfileImage();
    }

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

    public PatientProfileListResponse searchPageOrderByDefault(ProfileSearchCondition condition, Long lastIndex, LocalDateTime lastProfilePublicTime, Pageable pageable) {

        Slice<PatientProfileResponse> search = caregiverRepository.searchPageOrderByDefault(condition, lastIndex, lastProfilePublicTime, pageable);
        return PatientProfileListResponse.from(search);
    }
}
