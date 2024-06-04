package com.patientpal.backend.member.service;

import com.patientpal.backend.auth.dto.SignUpRequest;
import com.patientpal.backend.common.exception.AuthenticationException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.member.domain.*;
import com.patientpal.backend.member.dto.MemberResponse;
import com.patientpal.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PatientRepository patientRepository;
    private final CaregiverRepository caregiverRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * TODO
     *  - Member 가입 시, ROLE에 따라 Patient 또는 Caregiver 엔티티 자동 생성. -> 이후 프로필 설정에서 추가 설정 필요.
     *
     */
    public Long save(SignUpRequest request) {
        try {
            var member = SignUpRequest.of(request);
            member.encodePassword(passwordEncoder);
            Long id = memberRepository.save(member).getId();
            if (member.getRole() == Role.USER) {
                Patient patient = Patient.builder()
                        .member(member)
                        .build();
                patientRepository.save(patient);
            } else if (member.getRole() == Role.CAREGIVER) {
                Caregiver caregiver = Caregiver.builder()
                        .member(member)
                        .build();
                caregiverRepository.save(caregiver);
            }
            return id;
        } catch (DataIntegrityViolationException e) {
            throw new AuthenticationException(ErrorCode.MEMBER_ALREADY_EXIST, request.getUsername());
        }
    }

    @Transactional(readOnly = true)
    public MemberResponse findByUsername(String username) {
        Member foundMember = memberRepository.findByUsernameOrThrow(username);
        return MemberResponse.of(foundMember);
    }

    @Transactional(readOnly = true)
    public Member getUserByUsername(String username) {
        return memberRepository.findByUsernameOrThrow(username);
    }

    public void deleteByUsername(String username) {
        memberRepository.deleteByUsername(username);
    }
}
