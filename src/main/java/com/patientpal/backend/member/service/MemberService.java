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
    private final PasswordEncoder passwordEncoder;

    public Long save(SignUpRequest request) {
        try {
            var member = SignUpRequest.of(request);
            member.encodePassword(passwordEncoder);
            return memberRepository.save(member).getId();
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
