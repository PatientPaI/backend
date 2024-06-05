package com.patientpal.backend.caregiver.service;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.caregiver.dto.request.CaregiverProfileCreateRequest;
import com.patientpal.backend.caregiver.dto.request.CaregiverProfileUpdateRequest;
import com.patientpal.backend.caregiver.dto.response.CaregiverProfileResponse;
import com.patientpal.backend.caregiver.repository.CaregiverRepository;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CaregiverService {

    /**
     * TODO
     *  - 사진 업로드 (간병인만? 환자도? 선택? 필수?)
     */

    private final CaregiverRepository caregiverRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public CaregiverProfileResponse saveCaregiverProfile(String username, CaregiverProfileCreateRequest caregiverProfileCreateRequest) {
        Member currentMember = getMember(username);
        //멤버가 간병인이면 throw

        //본인인증 해야함. 이미 등록된 환자면 throw
//        if (currentMember.getPatient() != null) {
//            currentMember.getPatient().registerDetailProfile(patientProfileCreateRequest);
//            return PatientProfileResponse.of(currentMember.getPatient());
//        }
        Caregiver savedCaregiver = caregiverRepository.save(caregiverProfileCreateRequest.toEntity(currentMember));
        currentMember.setIsCompletedProfile(true);
        return CaregiverProfileResponse.of(savedCaregiver);
    }

    public CaregiverProfileResponse getProfile(String username) {
        Member currentMember = getMember(username);
        Caregiver caregiver = caregiverRepository.findByMember(currentMember).orElseThrow(() -> new EntityNotFoundException(ErrorCode.CAREGIVER_NOT_EXIST));
        return CaregiverProfileResponse.of(caregiver);
    }

    //프로필 수정 -> 이름과 주민번호 바꿀 수 있도록? 아예 새로운 환자로 등록할 수 있게? ㄴㄴㄴ그게 되나? 새로운 환자로 등록하면 기존 매칭은 다 날라가나?ㄴ
    //사람인 처럼 환자 자체는 못바꾼다. 세부 정보는 변경가능하지만, 사람 자체는 못바꿔.
    //프로필이 변경되면 기존 매칭들은?
    @Transactional
    public void updateCaregiverProfile(String username, CaregiverProfileUpdateRequest caregiverProfileUpdateRequest) {
        Member currentMember = getMember(username);
        currentMember.setIsCompletedProfile(false);
        currentMember.getCaregiver().updateDetailProfile(caregiverProfileUpdateRequest);
        currentMember.setIsCompletedProfile(true);
    }

    @Transactional
    public void deleteCaregiverProfile(String username) {
        Member currentMember = getMember(username);
        caregiverRepository.delete(currentMember.getCaregiver());
    }

    private Member getMember(String username) {
        return memberRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_EXIST, username));
    }
}
