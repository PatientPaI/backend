package com.patientpal.backend.matching.application;

import com.patientpal.backend.matching.domain.*;
import com.patientpal.backend.member.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchServiceImpl implements MatchService {

    private final PatientRepository patientRepository;
    private final CaregiverRepository caregiverRepository;
    private final MatchRepository matchRepository;
    private final MemberRepository memberRepository;

    //create()를 환자 -> 간병인, 간병인 -> 환자 두 개로 만들지 않기 위해 memberId를 가져옴.
    @Override
    public Long create(Long requestMemberId, Long responseMemberId) {
        Member requestMember = memberRepository.findById(requestMemberId).orElseThrow(() -> new IllegalArgumentException("요청한 회원을 찾을 수 없습니다."));
        Member responseMember = memberRepository.findById(responseMemberId).orElseThrow(() -> new IllegalArgumentException("매칭을 보낼 상대를 찾을 수 없습니다."));
        Match match = null;
        if (responseMember.getRole() == Role.CAREGIVER) {
            match = createMatchForPatient(responseMember, requestMember);
        } else if (responseMember.getRole() == Role.USER) {
            match = createMatchForCaregiver(responseMember, requestMember);
        }
        if (match == null) throw new IllegalArgumentException("매칭 생성에 실패하였습니다.");
        return match.getId();
    }

    @Override
    public List<Match> findAllByUserId(Long userId) {
        return matchRepository.findAllById(userId);
    }

    private Match createMatchForPatient(Member responseMember, Member requestMember) {
        Patient requestPatient = patientRepository.findByMember(requestMember);
        Caregiver responseCaregiver = caregiverRepository.findByMember(responseMember);
        Match match = Match.builder()
                .patient(requestPatient)
                .caregiver(responseCaregiver)
                .matchStatus(MatchStatus.PENDING)
                .readStatus(ReadStatus.UNREAD)
                .patientProfileSnapshot(generatePatientProfileSnapshot(requestPatient))
                .build();
        return matchRepository.save(match);
    }

    private Match createMatchForCaregiver(Member responseMember, Member requestMember) {
        Caregiver requestCaregiver = caregiverRepository.findByMember(requestMember);
        Patient responsePatient = patientRepository.findByMember(responseMember);
        Match match = Match.builder()
                .patient(responsePatient)
                .caregiver(requestCaregiver)
                .matchStatus(MatchStatus.PENDING)
                .readStatus(ReadStatus.UNREAD)
                .caregiverProfileSnapshot(generateCaregiverProfileSnapshot(requestCaregiver))
                .build();
        return matchRepository.save(match);
    }

    private String generatePatientProfileSnapshot(Patient patient) {
        return "환자 현재 프로필 스냅샷";
    }

    private String generateCaregiverProfileSnapshot(Caregiver caregiver) {
        return "간병인 현재 프로필 스냅샷";
    }
}
