package com.patientpal.backend.patient.service;

import static com.patientpal.backend.member.domain.Member.isNotOwner;

import com.patientpal.backend.caregiver.dto.response.CaregiverProfileListResponse;
import com.patientpal.backend.caregiver.dto.response.CaregiverProfileResponse;
import com.patientpal.backend.common.exception.AuthorizationException;
import com.patientpal.backend.common.exception.BusinessException;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.common.querydsl.ProfileSearchCondition;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.patient.domain.Patient;
import com.patientpal.backend.member.repository.MemberRepository;
import com.patientpal.backend.patient.dto.request.PatientProfileCreateRequest;
import com.patientpal.backend.patient.dto.request.PatientProfileUpdateRequest;
import com.patientpal.backend.patient.dto.response.PatientProfileDetailResponse;
import com.patientpal.backend.patient.repository.PatientRepository;
import com.patientpal.backend.view.ViewService;
import io.micrometer.core.annotation.Timed;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Timed("patient.service")
@Transactional(readOnly = true)
public class PatientService {

    private final PatientRepository patientRepository;
    private final MemberRepository memberRepository;
    private final ViewService viewService;

    @Transactional
    public PatientProfileDetailResponse savePatientProfile(String username, PatientProfileCreateRequest patientProfileCreateRequest, String profileImageUrl) {
        Member currentMember = getMember(username);
        Patient patient = patientRepository.findById(currentMember.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PATIENT_NOT_EXIST));
        // TODO 본인 인증 시, 중복 가입이면 throw
        patient.registerDetailProfile(patientProfileCreateRequest.getName(),
                patientProfileCreateRequest.getAddress(),
                patientProfileCreateRequest.getContact(),
                patientProfileCreateRequest.getAge(),
                patientProfileCreateRequest.getGender(),
                patientProfileCreateRequest.getNokName(),
                patientProfileCreateRequest.getNokContact(),
                patientProfileCreateRequest.getPatientSignificant(),
                patientProfileCreateRequest.getCareRequirements(),
                patientProfileCreateRequest.getRealCarePlace(),
                patientProfileCreateRequest.getIsNok(),
                patientProfileCreateRequest.getWantCareStartDate(),
                patientProfileCreateRequest.getWantCareEndDate(),
                profileImageUrl);
        patient.setIsCompleteProfile(true);
        log.info("프로필 등록 성공: ID={}, NAME={}", patient.getId(), patient.getName());
        return PatientProfileDetailResponse.of(patient, 0);
    }

    public PatientProfileDetailResponse getProfile(String username, Long memberId) {
        Patient patient = getPatientByMemberId(memberId);
        if (!username.equals(patient.getUsername())) {
            viewService.addProfileView(memberId, username);
        }
        long profileViewCount = viewService.getProfileViewCount(memberId);
        return PatientProfileDetailResponse.of(patient, profileViewCount);
    }

    @Transactional
    public void updatePatientProfile(String username, Long memberId, PatientProfileUpdateRequest patientProfileUpdateRequest, String profileImageUrl) {
        Patient patient = getPatientByMemberId(memberId);
        if (isNotOwner(username, patient)) {
            throw new AuthorizationException(ErrorCode.AUTHORIZATION_FAILED);
        }
        String currentProfileImageUrl = patient.getProfileImageUrl();

        patient.updateDetailProfile(
                patientProfileUpdateRequest.getAddress(),
                patientProfileUpdateRequest.getNokName(),
                patientProfileUpdateRequest.getNokContact(),
                patientProfileUpdateRequest.getAge(),
                patientProfileUpdateRequest.getPatientSignificant(),
                patientProfileUpdateRequest.getCareRequirements(),
                patientProfileUpdateRequest.getRealCarePlace(),
                patientProfileUpdateRequest.getIsNok(),
                patientProfileUpdateRequest.getWantCareStartDate(),
                patientProfileUpdateRequest.getWantCareEndDate()
        );

        if (profileImageUrl == null) {
            patient.setProfileImageUrl(null);
            return;
        }

        if (!profileImageUrl.equals(currentProfileImageUrl)) {
            patient.updateProfileImage(profileImageUrl);
        }
    }

    @Transactional
    public void deletePatientProfile(String username, Long memberId) {
        Patient patient = getPatientByMemberId(memberId);
        if (isNotOwner(username, patient)) {
            throw new AuthorizationException(ErrorCode.AUTHORIZATION_FAILED);
        }
        patient.deleteProfile();
    }

    @Transactional
    public void registerPatientProfileToMatchList(String username, Long memberId) {
        Patient patient = getPatientByMemberId(memberId);
        if (isNotOwner(username, patient)) {
            throw new AuthorizationException(ErrorCode.AUTHORIZATION_FAILED);
        }
        if (!patient.getIsCompleteProfile()) {
            throw new BusinessException(ErrorCode.PROFILE_NOT_COMPLETED);
        }
        patient.setIsProfilePublic(true);
        patient.setProfilePublicTime(LocalDateTime.now());

    }

    @Transactional
    public void unregisterPatientProfileToMatchList(String username, Long memberId) {
        Patient patient = getPatientByMemberId(memberId);
        if (isNotOwner(username, patient)) {
            throw new AuthorizationException(ErrorCode.AUTHORIZATION_FAILED);
        }
        patient.setIsProfilePublic(false);
    }

    @Transactional
    public void deletePatientProfileImage(String username, Long memberId) {
        Patient patient = getPatientByMemberId(memberId);
        if (isNotOwner(username, patient)) {
            throw new AuthorizationException(ErrorCode.AUTHORIZATION_FAILED);
        }
        patient.deleteProfileImage();
    }

    private Member getMember(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_EXIST, username));
    }

    private Patient getPatientByMemberId(Long memberId) {
        return patientRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PATIENT_NOT_EXIST));
    }

}
