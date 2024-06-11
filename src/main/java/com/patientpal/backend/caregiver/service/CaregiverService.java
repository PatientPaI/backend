package com.patientpal.backend.caregiver.service;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.caregiver.dto.request.CaregiverProfileCreateRequest;
import com.patientpal.backend.caregiver.dto.request.CaregiverProfileUpdateRequest;
import com.patientpal.backend.caregiver.dto.response.CaregiverProfileResponse;
import com.patientpal.backend.caregiver.repository.CaregiverRepository;
import com.patientpal.backend.common.exception.AuthorizationException;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.matching.domain.MatchRepository;
import com.patientpal.backend.matching.exception.CanNotDeleteException;
import com.patientpal.backend.matching.exception.DuplicateRequestException;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.domain.Role;
import com.patientpal.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CaregiverService {

    private final CaregiverRepository caregiverRepository;
    private final MemberRepository memberRepository;
    private final MatchRepository matchRepository;

    @Transactional
    public CaregiverProfileResponse saveCaregiverProfile(String username, CaregiverProfileCreateRequest caregiverProfileCreateRequest) {
        Member currentMember = getMember(username);
        // TODO 본인 인증 진행, 중복 가입이면 throw
        validateAuthorization(currentMember);
        validateDuplicateCaregiver(currentMember);
        Caregiver savedCaregiver = caregiverRepository.save(caregiverProfileCreateRequest.toEntity(currentMember));
        log.info("프로필 등록 성공: ID={}, NAME={}", savedCaregiver.getId(), savedCaregiver.getName());
        return CaregiverProfileResponse.of(savedCaregiver);
    }

    private void validateAuthorization(Member currentMember) {
        if (currentMember.getRole() == Role.USER) {
            throw new AuthorizationException(ErrorCode.AUTHORIZATION_FAILED, currentMember.getUsername());
        }
    }

    private void validateDuplicateCaregiver(Member currentMember) {
        caregiverRepository.findByMember(currentMember).ifPresent(caregiver -> {
            throw new DuplicateRequestException(ErrorCode.CAREGIVER_ALREADY_EXIST);
        });
    }

    public CaregiverProfileResponse getProfile(String username) {
        Member currentMember = getMember(username);
        Caregiver caregiver = getCaregiver(currentMember);
        return CaregiverProfileResponse.of(caregiver);
    }

    @Transactional
    public void updateCaregiverProfile(String username, CaregiverProfileUpdateRequest caregiverProfileUpdateRequest) {
        Member currentMember = getMember(username);
        getCaregiver(currentMember).updateDetailProfile(
                caregiverProfileUpdateRequest.getAddress(),
                caregiverProfileUpdateRequest.getRating(),
                caregiverProfileUpdateRequest.getExperienceYears(),
                caregiverProfileUpdateRequest.getSpecialization(),
                caregiverProfileUpdateRequest.getCaregiverSignificant()
        );
    }

    @Transactional
    public void deleteCaregiverProfile(String username) {
        Member currentMember = getMember(username);
        Caregiver caregiver = getCaregiver(currentMember);
        if (hasOnGoingMatches(caregiver.getId())) {
            throw new CanNotDeleteException(ErrorCode.CAN_NOT_DELETE_PROFILE);
        }
        caregiverRepository.delete(caregiver);
    }

    private boolean hasOnGoingMatches(Long caregiverId) {
        if (matchRepository.existsInProgressMatchingForCaregiver(caregiverId)) {
            return true;
        }
        return false;
    }

    @Transactional
    public void registerCaregiverProfileToMatchList(String username) {
        Member member = getMember(username);
        Caregiver caregiver = getCaregiver(member);
        caregiver.setIsInMatchList(true);
    }

    @Transactional
    public void unregisterCaregiverProfileToMatchList(String username) {
        Member member = getMember(username);
        Caregiver caregiver = getCaregiver(member);
        caregiver.setIsInMatchList(false);
    }

    private Member getMember(String username) {
        return memberRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_EXIST, username));
    }

    private Caregiver getCaregiver(Member currentMember) {
        return caregiverRepository.findByMember(currentMember).orElseThrow(() -> new EntityNotFoundException(ErrorCode.CAREGIVER_NOT_EXIST));
    }
}
