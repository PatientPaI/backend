package com.patientpal.backend.matching.application;

import com.patientpal.backend.matching.domain.*;
import com.patientpal.backend.matching.dto.response.MatchListResponse;
import com.patientpal.backend.matching.dto.response.MatchResponse;
import com.patientpal.backend.member.domain.*;
import com.patientpal.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.patientpal.backend.matching.domain.FirstRequest.*;
import static com.patientpal.backend.member.domain.Role.*;

/**
 * TODO
 * 예외 처리 추가
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MatchServiceImpl implements MatchService {

    private final PatientRepository patientRepository;
    private final CaregiverRepository caregiverRepository;
    private final MatchRepository matchRepository;
    private final MemberRepository memberRepository;

    /**
     * TODO
     *  - 생성한 사람, 수정한 사람이 NULL로 들어감
     *  - 환자, 간병인 서로 매칭 생성을 양쪽에서 할 수 있게 한 메서드로 했는데,
     *  생각해보니 분리하는게 유지보수상 좋을 수도 있겠다.
     *  - 페이징 처리 (현재 list 사이즈가 다름. dto로 변환하는 과정에서 문제 있는 듯.
     */

    @Transactional
    @Override
    public MatchResponse create(Long requestMemberId, Long responseMemberId) {
        Member requestMember = memberRepository.findById(requestMemberId).orElseThrow(() -> new IllegalArgumentException("요청한 회원을 찾을 수 없습니다."));
        Member responseMember = memberRepository.findById(responseMemberId).orElseThrow(() -> new IllegalArgumentException("매칭을 보낼 상대를 찾을 수 없습니다."));
        MatchResponse matchResponse = null;
        if (requestMember.getRole() == USER) {
            matchResponse = createMatchForPatient(requestMember, responseMember);
            matchRepository.save(matchResponse.toEntityFirstPatient(requestMember.getPatient(), responseMember.getCaregiver()));
        } else if (requestMember.getRole() == CAREGIVER) { //else문 사용? - 코드 컨벤션 위배하는지, user 아니라면 caregiver라 판단해 우선 이렇게 짜긴 했습니다
            matchResponse = createMatchForCaregiver(requestMember, responseMember);
            matchRepository.save(matchResponse.toEntityFirstCaregiver(requestMember.getPatient(), responseMember.getCaregiver()));
        } else { //else 논의 필요
            //관리자가 매칭? 보낼 일이 있나?
        }
        if (matchResponse == null) throw new IllegalArgumentException("매칭 생성에 실패하였습니다.");
        return matchResponse;
    }

    @Override
    public MatchResponse getMatch(Long matchId) {
        Match findMatch = matchRepository.findById(matchId).orElseThrow(() -> new IllegalArgumentException("요청한 매칭을 찾을 수 없습니다."));
        return MatchResponse.builder()
                .id(findMatch.getId())
                .patientId(findMatch.getPatient().getId())
                .caregiverId(findMatch.getCaregiver().getId())
                .matchStatus(findMatch.getMatchStatus())
                .readStatus(findMatch.getReadStatus())
                .firstRequest(findMatch.getFirstRequest())
                .createdDate(findMatch.getCreatedDate())
                .patientProfileSnapshot(findMatch.getPatientProfileSnapshot())
                .caregiverProfileSnapshot(findMatch.getCaregiverProfileSnapshot())
                .build();
    }

    @Override
    public MatchListResponse getMatchList(String username, Pageable pageable) {
        Member findMember = memberRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("멤버를 찾을 수 없습니다."));
        Page<Match> matchList = matchRepository.findAllById(findMember.getId(), pageable);
        log.info("matchList.size={}", matchList.getSize());
        List<MatchResponse> matchListResponse = matchList.stream()
                .map(MatchResponse::of)
                .toList();

        log.info("matchResponseList.size={}", matchListResponse.size());
        return MatchListResponse.from(matchListResponse);
    }

    private MatchResponse createMatchForPatient(Member requestMember, Member responseMember) {
        Patient requestPatient = patientRepository.findByMember(requestMember).orElseThrow(() -> new IllegalArgumentException("환자를 찾을 수 없습니다. 등록된 환자가 아닙니다."));
        Caregiver responseCaregiver = caregiverRepository.findByMember(responseMember).orElseThrow(() -> new IllegalArgumentException("간병인을 찾을 수 없습니다. 등록된 간병인이 아닙니다."));
        return MatchResponse.builder()
                .patientId(requestPatient.getId())
                .caregiverId(responseCaregiver.getId())
                .matchStatus(MatchStatus.PENDING)
                .readStatus(ReadStatus.UNREAD)
                .firstRequest(PATIENT_FIRST)
                .patientProfileSnapshot(generatePatientProfileSnapshot(requestPatient))
                .build();
    }

    private MatchResponse createMatchForCaregiver(Member requestMember, Member responseMember) {
        Caregiver requestCaregiver = caregiverRepository.findByMember(requestMember).orElseThrow(() -> new IllegalArgumentException("간병인을 찾을 수 없습니다. 등록된 간병인이 아닙니다."));
        Patient responsePatient = patientRepository.findByMember(responseMember).orElseThrow(() -> new IllegalArgumentException("환자를 찾을 수 없습니다. 등록된 환자가 아닙니다."));
        return MatchResponse.builder()
                .caregiverId(requestCaregiver.getId())
                .patientId(responsePatient.getId())
                .matchStatus(MatchStatus.PENDING)
                .readStatus(ReadStatus.UNREAD)
                .firstRequest(CAREGIVER_FIRST)
                .caregiverProfileSnapshot(generateCaregiverProfileSnapshot(requestCaregiver))
                .build();
    }

    private String generatePatientProfileSnapshot(Patient patient) {
        return "환자 현재 프로필 스냅샷";
    }

    private String generateCaregiverProfileSnapshot(Caregiver caregiver) {
        return "간병인 현재 프로필 스냅샷";
    }
}
