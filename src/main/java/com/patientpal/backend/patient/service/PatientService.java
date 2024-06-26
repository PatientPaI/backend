package com.patientpal.backend.patient.service;

import com.patientpal.backend.caregiver.dto.response.CaregiverProfileListResponse;
import com.patientpal.backend.caregiver.dto.response.CaregiverProfileResponse;
import com.patientpal.backend.common.exception.AuthorizationException;
import com.patientpal.backend.common.exception.BusinessException;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.common.querydsl.ProfileSearchCondition;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.patient.domain.Patient;
import com.patientpal.backend.member.domain.Role;
import com.patientpal.backend.member.repository.MemberRepository;
import com.patientpal.backend.patient.dto.request.PatientProfileCreateRequest;
import com.patientpal.backend.patient.dto.request.PatientProfileUpdateRequest;
import com.patientpal.backend.patient.dto.response.PatientProfileDetailResponse;
import com.patientpal.backend.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PatientService {

    private final PatientRepository patientRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public PatientProfileDetailResponse savePatientProfile(String username, PatientProfileCreateRequest patientProfileCreateRequest, String profileImageUrl) {
        Member currentMember = getMember(username);
        Patient patient = patientRepository.findById(currentMember.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PATIENT_NOT_EXIST));
        // TODO 본인 인증 시, 중복 가입이면 throw
        validateAuthorization(currentMember);
        patient.registerDetailProfile(patientProfileCreateRequest.getName(),
                patientProfileCreateRequest.getAddress(),
                patientProfileCreateRequest.getContact(),
                patientProfileCreateRequest.getResidentRegistrationNumber(),
                patientProfileCreateRequest.getGender(),
                patientProfileCreateRequest.getNokName(),
                patientProfileCreateRequest.getNokContact(),
                patientProfileCreateRequest.getPatientSignificant(),
                patientProfileCreateRequest.getCareRequirements(),
                profileImageUrl);
        log.info("프로필 등록 성공: ID={}, NAME={}", patient.getId(), patient.getName());
        return PatientProfileDetailResponse.of(patient);
    }

    private void validateAuthorization(Member currentMember) {
        if (currentMember.getRole() == Role.CAREGIVER) {
            throw new AuthorizationException(ErrorCode.AUTHORIZATION_FAILED, currentMember.getUsername());
        }
    }

    public PatientProfileDetailResponse getProfile(String username, Long memberId) {
        Patient patient = getPatientByMemberId(memberId);
        if (!username.equals(patient.getUsername())) {
            throw new BusinessException(ErrorCode.AUTHORIZATION_FAILED);
        }
        return PatientProfileDetailResponse.of(patient);
    }

    @Transactional
    public void updatePatientProfile(String username, Long memberId, PatientProfileUpdateRequest patientProfileUpdateRequest, String profileImageUrl) {
        Patient patient = getPatientByMemberId(memberId);
        if (!username.equals(patient.getUsername())) {
            throw new BusinessException(ErrorCode.AUTHORIZATION_FAILED);
        }
        String currentProfileImageUrl = patient.getProfileImageUrl();

        patient.updateDetailProfile(
                patientProfileUpdateRequest.getAddress(),
                patientProfileUpdateRequest.getNokName(),
                patientProfileUpdateRequest.getNokContact(),
                patientProfileUpdateRequest.getPatientSignificant(),
                patientProfileUpdateRequest.getCareRequirements()
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
    public void registerPatientProfileToMatchList(String username, Long memberId) {
        Patient patient = getPatientByMemberId(memberId);
        if (!username.equals(patient.getUsername())) {
            throw new BusinessException(ErrorCode.AUTHORIZATION_FAILED);
        }
        if (!patient.getIsCompleteProfile()) {
            throw new BusinessException(ErrorCode.PROFILE_NOT_COMPLETED);
        }
        patient.setIsProfilePublic(true);
    }

    @Transactional
    public void unregisterPatientProfileToMatchList(String username, Long memberId) {
        Patient patient = getPatientByMemberId(memberId);
        if (!username.equals(patient.getUsername())) {
            throw new BusinessException(ErrorCode.AUTHORIZATION_FAILED);
        }
        patient.setIsProfilePublic(false);
    }

    @Transactional
    public void deletePatientProfileImage(String username, Long memberId) {
        Patient patient = getPatientByMemberId(memberId);
        if (!username.equals(patient.getUsername())) {
            throw new BusinessException(ErrorCode.AUTHORIZATION_FAILED);
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

    public CaregiverProfileListResponse searchPageOrderBy(ProfileSearchCondition condition, Pageable pageable) {
        Page<CaregiverProfileResponse> search = patientRepository.searchCaregiverProfilesOrderBy(condition, pageable);
        return CaregiverProfileListResponse.from(search);
    }
}
