package com.patientpal.backend.patient.service;

import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.repository.MemberRepository;
import com.patientpal.backend.patient.domain.Patient;
import com.patientpal.backend.patient.dto.request.PatientProfileCreateRequest;
import com.patientpal.backend.patient.dto.request.PatientProfileUpdateRequest;
import com.patientpal.backend.patient.dto.response.PatientProfileResponse;
import com.patientpal.backend.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientService {

    /**
     * TODO
     *  - 사진 업로드 (간병인만? 환자도? 선택? 필수?)
     */

    private final PatientRepository patientRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public PatientProfileResponse savePatientProfile(String username, PatientProfileCreateRequest patientProfileCreateRequest) {
        Member currentMember = getMember(username);
        //TODO 멤버가 간병인이면 throw
        //본인인증 해야함. 이미 등록된 환자면 throw

//        if (currentMember.getPatient() != null) {
//            throw new DuplicateRequestException("이미 등록된 환자입니다.");
//        }
        Patient savedPatient = patientRepository.save(patientProfileCreateRequest.toEntity(currentMember));
        currentMember.setIsCompletedProfile(true);
        return PatientProfileResponse.of(savedPatient);
    }

    public PatientProfileResponse getProfile(String username) {
        Member currentMember = getMember(username);
        Patient patient = patientRepository.findByMember(currentMember).orElseThrow(() -> new EntityNotFoundException(ErrorCode.PATIENT_NOT_EXIST));
        return PatientProfileResponse.of(patient);
    }

    @Transactional
    public void updatePatientProfile(String username, PatientProfileUpdateRequest patientProfileUpdateRequest) {
        Member currentMember = getMember(username);
        currentMember.setIsCompletedProfile(false);
        currentMember.getPatient().updateDetailProfile(patientProfileUpdateRequest);
        currentMember.setIsCompletedProfile(true);
    }

    @Transactional
    public void deletePatientProfile(String username) {
        Member currentMember = getMember(username);
        patientRepository.delete(currentMember.getPatient());
    }

    private Member getMember(String username) {
        return memberRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_EXIST, username));
    }
}
