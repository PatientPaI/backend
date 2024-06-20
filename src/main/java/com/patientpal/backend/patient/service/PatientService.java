package com.patientpal.backend.patient.service;

import com.patientpal.backend.caregiver.dto.response.CaregiverProfileListResponse;
import com.patientpal.backend.caregiver.dto.response.CaregiverProfileResponse;
import com.patientpal.backend.common.exception.AuthorizationException;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.matching.domain.MatchRepository;
import com.patientpal.backend.matching.exception.CanNotDeleteException;
import com.patientpal.backend.matching.exception.DuplicateRequestException;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.domain.Role;
import com.patientpal.backend.member.repository.MemberRepository;
import com.patientpal.backend.patient.domain.Patient;
import com.patientpal.backend.patient.dto.request.PatientProfileCreateRequest;
import com.patientpal.backend.patient.dto.request.PatientProfileUpdateRequest;
import com.patientpal.backend.patient.dto.response.PatientProfileDetailResponse;
import com.patientpal.backend.patient.repository.PatientRepository;
import com.patientpal.backend.common.querydsl.ProfileSearchCondition;
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
    private final MatchRepository matchRepository;

    @Transactional
    public PatientProfileDetailResponse savePatientProfile(String username, PatientProfileCreateRequest patientProfileCreateRequest, String profileImageUrl) {
        Member currentMember = getMember(username);
        // TODO 본인 인증 시, 중복 가입이면 throw
        validateAuthorization(currentMember);
        validateDuplicateCaregiver(currentMember);
        Patient savedPatient = patientRepository.save(
                patientProfileCreateRequest.toEntity(currentMember, profileImageUrl));
        log.info("프로필 등록 성공: ID={}, NAME={}", savedPatient.getId(), savedPatient.getName());
        return PatientProfileDetailResponse.of(savedPatient);
    }

    private void validateAuthorization(Member currentMember) {
        if (currentMember.getRole() == Role.CAREGIVER) {
            throw new AuthorizationException(ErrorCode.AUTHORIZATION_FAILED, currentMember.getUsername());
        }
    }

    private void validateDuplicateCaregiver(Member currentMember) {
        patientRepository.findByMember(currentMember).ifPresent(patient -> {
            throw new DuplicateRequestException(ErrorCode.PATIENT_ALREADY_EXIST);
        });
    }

    public PatientProfileDetailResponse getProfile(String username) {
        Member currentMember = getMember(username);
        Patient patient = getPatient(currentMember);
        return PatientProfileDetailResponse.of(patient);
    }

    @Transactional
    public void updatePatientProfile(String username, PatientProfileUpdateRequest patientProfileUpdateRequest, String profileImageUrl) {
        Member currentMember = getMember(username);
        Patient patient = getPatient(currentMember);

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
    public void deletePatientProfile(String username) {
        Member currentMember = getMember(username);
        Patient patient = getPatient(currentMember);
        if (hasOnGoingMatches(patient.getId())) {
            throw new CanNotDeleteException(ErrorCode.CAN_NOT_DELETE_PROFILE);
        }
        patientRepository.delete(patient);
    }

    private boolean hasOnGoingMatches(Long patientId) {
        return matchRepository.existsInProgressMatchingForPatient(patientId);
    }

    @Transactional
    public void registerPatientProfileToMatchList(String username) {
        Member member = getMember(username);
        Patient patient = getPatient(member);
        patient.setIsInMatchList(true);
    }

    @Transactional
    public void unregisterPatientProfileToMatchList(String username) {
        Member member = getMember(username);
        Patient patient = getPatient(member);
        patient.setIsInMatchList(false);
    }

    @Transactional
    public void deletePatientProfileImage(String username) {
        Member member = getMember(username);
        Patient patient = getPatient(member);
        patient.deleteProfileImage();
    }

    private Member getMember(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_EXIST, username));
    }

    private Patient getPatient(Member currentMember) {
        return patientRepository.findByMember(currentMember)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PATIENT_NOT_EXIST));
    }

    public CaregiverProfileListResponse searchPageOrderBy(ProfileSearchCondition condition, Pageable pageable) {
        Page<CaregiverProfileResponse> search = patientRepository.searchCaregiverProfilesOrderBy(condition, pageable);
        return CaregiverProfileListResponse.from(search);
    }
}
