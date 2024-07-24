package com.patientpal.backend.member.service;

import com.patientpal.backend.auth.dto.SignUpRequest;
import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.caregiver.repository.CaregiverRepository;
import com.patientpal.backend.common.exception.AuthenticationException;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.domain.Provider;
import com.patientpal.backend.member.domain.Role;
import com.patientpal.backend.member.dto.MemberResponse;
import com.patientpal.backend.member.repository.MemberRepository;
import com.patientpal.backend.patient.domain.Patient;
import com.patientpal.backend.patient.repository.PatientRepository;
import com.patientpal.backend.security.oauth.dto.Oauth2SignUpRequest;
import java.util.List;
import java.util.regex.Pattern;
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
    private final CaregiverRepository caregiverRepository;
    private final PatientRepository patientRepository;

    public Long save(SignUpRequest request) {
        try {
            if (request.getRole() == Role.CAREGIVER) {
                Caregiver caregiver = Caregiver.builder().
                        username(request.getUsername())
                        .password(request.getPassword())
                        .role(request.getRole())
                        .provider(Provider.LOCAL)
                        .isCompleteProfile(false)
                        .isProfilePublic(false)
                        .build();
                caregiver.encodePassword(passwordEncoder);
                return caregiverRepository.save(caregiver).getId();
            }
            else if (request.getRole() == Role.USER) {
                Patient patient = Patient.builder().
                        username(request.getUsername())
                        .password(request.getPassword())
                        .role(request.getRole())
                        .provider(Provider.LOCAL)
                        .isCompleteProfile(false)
                        .isProfilePublic(false)
                        .build();
                patient.encodePassword(passwordEncoder);
                return patientRepository.save(patient).getId();
            } else {
                Member member = Member.builder()
                        .username(request.getUsername())
                        .password(request.getPassword())
                        .role(Role.ADMIN)
                        .provider(Provider.LOCAL)
                        .build();
                return memberRepository.save(member).getId();
            }
        } catch (DataIntegrityViolationException e) {
            throw new AuthenticationException(ErrorCode.MEMBER_ALREADY_EXIST, request.getUsername());
        }
    }

    @Transactional
    public Long saveSocialUser(Oauth2SignUpRequest request) {
        try {
            String username = request.getUsername();

            if (request.getRole() == Role.CAREGIVER) {
                Caregiver caregiver = Caregiver.builder()
                        .username(username)
                        .password(passwordEncoder.encode(request.getPassword()))
                        .role(request.getRole())
                        .provider(Provider.valueOf(request.getProvider().toUpperCase()))
                        .build();
                return caregiverRepository.save(caregiver).getId();
            } else if (request.getRole() == Role.USER) {
                Patient patient = Patient.builder()
                        .username(username)
                        .password(passwordEncoder.encode(request.getPassword()))
                        .role(request.getRole())
                        .provider(Provider.valueOf(request.getProvider().toUpperCase()))
                        .build();
                return patientRepository.save(patient).getId();
            } else {
                Member member = Member.builder()
                        .username(request.getUsername())
                        .password(request.getPassword())
                        .role(Role.ADMIN)
                        .provider(Provider.valueOf(request.getProvider().toUpperCase()))
                        .build();
                return memberRepository.save(member).getId();
            }
        } catch (DataIntegrityViolationException e) {
            throw new AuthenticationException(ErrorCode.MEMBER_ALREADY_EXIST, request.getEmail());
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

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return memberRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public List<String> findUsernamesStartingWith(String username) {
        validateUsername(username);
        return memberRepository.findUsernameStartingWith(username);
    }

    @Transactional(readOnly = true)
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_EXIST));
    }

    private void validateUsername(String username) {
        Pattern BASE_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9]*$");
        if (!BASE_NAME_PATTERN.matcher(username).matches()) {
            throw new IllegalArgumentException("Invalid base name: " + username);
        }
    }

}
