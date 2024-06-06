package com.patientpal.backend.patient.service;

import com.patientpal.backend.common.exception.AuthorizationException;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.matching.domain.Match;
import com.patientpal.backend.matching.domain.MatchRepository;
import com.patientpal.backend.matching.domain.MatchStatus;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.domain.Role;
import com.patientpal.backend.member.repository.MemberRepository;
import com.patientpal.backend.patient.domain.Patient;
import com.patientpal.backend.patient.dto.request.PatientProfileCreateRequest;
import com.patientpal.backend.patient.dto.request.PatientProfileUpdateRequest;
import com.patientpal.backend.patient.dto.response.PatientProfileResponse;
import com.patientpal.backend.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PatientService {

    /**
     * TODO
     *  - 사진 업로드 (간병인만? 환자도? 선택? 필수?)
     *  - 프로필 등록시, 휴대폰 본인 인증 구현해야함.
     */

    private final PatientRepository patientRepository;
    private final MemberRepository memberRepository;
    private final MatchRepository matchRepository;

    @Transactional
    public PatientProfileResponse savePatientProfile(String username, PatientProfileCreateRequest patientProfileCreateRequest) {
        Member currentMember = getMember(username);
        validateAuthorization(currentMember);
        // TODO 본인 인증 시, 중복 가입이면 throw
        Patient savedPatient = patientRepository.save(patientProfileCreateRequest.toEntity(currentMember));
        log.info("프로필 등록 성공: ID={}, NAME={}", savedPatient.getId(), savedPatient.getName());
        return PatientProfileResponse.of(savedPatient);
    }

    private void validateAuthorization(Member currentMember) {
        if (currentMember.getRole() == Role.CAREGIVER) {
            throw new AuthorizationException(ErrorCode.AUTHORIZATION_FAILED, currentMember.getUsername());
        }
    }

    public PatientProfileResponse getProfile(String username) {
        Member currentMember = getMember(username);
        Patient patient = getPatient(currentMember);
        return PatientProfileResponse.of(patient);
    }

    @Transactional
    public void updatePatientProfile(String username, PatientProfileUpdateRequest patientProfileUpdateRequest) {
        Member currentMember = getMember(username);
        getPatient(currentMember).updateDetailProfile(patientProfileUpdateRequest);
    }

    @Transactional
    public void deletePatientProfile(String username) {
        Member currentMember = getMember(username);
        Patient patient = getPatient(currentMember);
//        if (hasOnGoingMatches(patient)) {
//            throw new IllegalStateException("진행 중인 매칭이 있어 프로필을 삭제할 수 없습니다.");
//        }
        patientRepository.delete(patient);
    }

    private Member getMember(String username) {
        return memberRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_EXIST, username));
    }

    private Patient getPatient(Member currentMember) {
        return patientRepository.findByMember(currentMember).orElseThrow(() -> new EntityNotFoundException(ErrorCode.PATIENT_NOT_EXIST));
    }
}
