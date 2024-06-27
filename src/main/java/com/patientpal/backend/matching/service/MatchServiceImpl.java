package com.patientpal.backend.matching.service;

import static com.patientpal.backend.common.exception.ErrorCode.AUTHORIZATION_FAILED;
import static com.patientpal.backend.common.exception.ErrorCode.DUPLICATED_REQUEST;
import static com.patientpal.backend.common.exception.ErrorCode.MATCH_NOT_EXIST;
import static com.patientpal.backend.common.exception.ErrorCode.MEMBER_NOT_EXIST;
import static com.patientpal.backend.matching.domain.FirstRequest.CAREGIVER_FIRST;
import static com.patientpal.backend.matching.domain.FirstRequest.PATIENT_FIRST;
import static com.patientpal.backend.matching.domain.MatchStatus.ACCEPTED;
import static com.patientpal.backend.matching.domain.MatchStatus.CANCELED;
import static com.patientpal.backend.matching.domain.ReadStatus.READ;
import static com.patientpal.backend.matching.domain.ReadStatus.UNREAD;
import static com.patientpal.backend.matching.service.MatchValidation.validateAcceptance;
import static com.patientpal.backend.matching.service.MatchValidation.validateCancellation;
import static com.patientpal.backend.matching.service.MatchValidation.validateIsCanceled;
import static com.patientpal.backend.matching.service.MatchValidation.validateIsInMatchList;
import static com.patientpal.backend.matching.service.MatchValidation.validateIsNotExistCaregiver;
import static com.patientpal.backend.matching.service.MatchValidation.validateIsNotExistPatient;
import static com.patientpal.backend.matching.service.MatchValidation.validateMatchAuthorization;
import static com.patientpal.backend.member.domain.Role.CAREGIVER;
import static com.patientpal.backend.member.domain.Role.USER;

import com.patientpal.backend.caregiver.repository.CaregiverRepository;
import com.patientpal.backend.common.exception.AuthorizationException;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.matching.domain.Match;
import com.patientpal.backend.matching.domain.MatchRepository;
import com.patientpal.backend.matching.dto.response.MatchListResponse;
import com.patientpal.backend.matching.dto.response.MatchResponse;
import com.patientpal.backend.matching.exception.DuplicateRequestException;
import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.repository.MemberRepository;
import com.patientpal.backend.notification.annotation.NeedNotification;
import com.patientpal.backend.patient.domain.Patient;
import com.patientpal.backend.member.repository.MemberRepository;
import com.patientpal.backend.patient.repository.PatientRepository;
import java.util.List;
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
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final MemberRepository memberRepository;
    private final PatientRepository patientRepository;
    private final CaregiverRepository caregiverRepository;

    @Transactional
    @Override
    @NeedNotification
    public MatchResponse createMatch(String username, Long responseMemberId) {
        Member requestMember = getMemberByUsername(username);
        Member responseMember = getMemberById(responseMemberId);
        validateIsInMatchList(requestMember);
        validateIsInMatchList(responseMember);

        if (matchRepository.existsPendingMatch(requestMember.getId(), responseMember.getId())) {
            throw new DuplicateRequestException(DUPLICATED_REQUEST);
        }

        return createMatch(requestMember, responseMember);
    }

    private MatchResponse createMatch(Member requestMember, Member responseMember) {
        if (requestMember.getRole() == USER) {
            return createPatientMatch(requestMember, responseMember);
        } else if (requestMember.getRole() == CAREGIVER) {
            return createCaregiverMatch(requestMember, responseMember);
        } else {
            throw new AuthorizationException(AUTHORIZATION_FAILED);
        }
    }

    private MatchResponse createPatientMatch(Member requestMember, Member responseMember) {
        String generatedPatientProfileSnapshot = getPatientByMemberId(requestMember.getId()).generatePatientProfileSnapshot();
        Match match = matchRepository.save(MatchResponse.toEntityFirstPatient(requestMember, responseMember, generatedPatientProfileSnapshot));
        log.info("매칭 신청 성공 ! 요청 : {}, 수락 : {}", requestMember.getName(), responseMember.getName());
        return MatchResponse.of(match);
    }

    private MatchResponse createCaregiverMatch(Member requestMember, Member responseMember) {
        String generatedCaregiverProfileSnapshot = getCaregiverByMemberId(requestMember.getId()).generateCaregiverProfileSnapshot();
        Match match = matchRepository.save(MatchResponse.toEntityFirstCaregiver(requestMember, responseMember, generatedCaregiverProfileSnapshot));
        log.info("매칭 신청 성공 ! 요청 : {}, 수락 : {}", requestMember.getName(), responseMember.getName());
        return MatchResponse.of(match);
    }


    private Member getMemberByUsername(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_EXIST, username));
    }

    private Member getMemberById(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_EXIST));
    }

    @Transactional
    @Override
    public MatchResponse getMatch(Long matchId, String username) {
        Match findMatch = getMatchById(matchId);
        Member currentMember = getMemberByUsername(username);
        validateMatchAuthorization(findMatch, username);
        validateIsCanceled(findMatch);
        setMatchReadStatus(findMatch, currentMember);
        return MatchResponse.of(findMatch);
    }

    private Match getMatchById(Long matchId) {
        return matchRepository.findById(matchId).orElseThrow(() -> new EntityNotFoundException(MATCH_NOT_EXIST));
    }

    @Override
    public MatchListResponse getRequestMatches(String username, Long memberId, Pageable pageable) {
        Member currentMember = getMemberById(memberId);
        Page<Match> matchPage = matchRepository.findAllRequest(currentMember.getId(), pageable);
        List<MatchResponse> matchListResponse = matchPage.stream()
                .map(MatchResponse::of)
                .toList();
        return MatchListResponse.from(matchPage, matchListResponse);
    }

    @Override
    public MatchListResponse getReceivedMatches(String username, Long memberId, Pageable pageable) {
        Member currentMember = getMemberById(memberId);
        Page<Match> matchPage = matchRepository.findAllReceived(currentMember.getId(), pageable);
        List<MatchResponse> matchListResponse = matchPage.stream()
                .map(MatchResponse::of)
                .toList();
        return MatchListResponse.from(matchPage, matchListResponse);
    }

    @Transactional
    @Override
    public void cancelMatch(Long matchId, String username) {
        Match match = getMatchById(matchId);
        Member currentMember = getMemberByUsername(username);
        validateCancellation(match, currentMember);
        match.setMatchStatus(CANCELED);
    }

    @Transactional
    @Override
    public void acceptMatch(Long matchId, String username) {
        Match match = getMatchById(matchId);
        Member currentMember = getMemberByUsername(username);
        validateAcceptance(match);
        match.setReadStatus(READ);
        setMatchStatusAccepted(match, currentMember);
    }

    private void setMatchReadStatus(Match findMatch, Member currentMember) {
        if (findMatch.getReadStatus() == UNREAD) {
            if (currentMember.getRole() == USER && findMatch.getFirstRequest() == CAREGIVER_FIRST) {
                findMatch.setReadStatus(READ);
            } else if (currentMember.getRole() == CAREGIVER && findMatch.getFirstRequest() == PATIENT_FIRST) {
                findMatch.setReadStatus(READ);
            }
        }
    }

    private void setMatchStatusAccepted(Match match, Member currentMember) {
        if (currentMember.getRole() == USER) {
            Patient patient = getPatientByMemberId(currentMember.getId());
            if (match.getFirstRequest() == CAREGIVER_FIRST &&
                    match.getReceivedMember().getId().equals(currentMember.getId())) {
                validateIsNotExistCaregiver(match);
                match.setMatchStatus(ACCEPTED);
                match.setPatientProfileSnapshot(patient.generatePatientProfileSnapshot());
            } else {
                throw new AuthorizationException(AUTHORIZATION_FAILED);
            }
        } else if (currentMember.getRole() == CAREGIVER) {
            Caregiver caregiver = getCaregiverByMemberId(currentMember.getId());
            if (match.getFirstRequest() == PATIENT_FIRST &&
                    match.getReceivedMember().getId().equals(currentMember.getId())) {
                validateIsNotExistPatient(match);
                match.setMatchStatus(ACCEPTED);
                match.setCaregiverProfileSnapshot(caregiver.generateCaregiverProfileSnapshot());
            } else {
                throw new AuthorizationException(AUTHORIZATION_FAILED);
            }
        }
    }

    private Patient getPatientByMemberId(Long memberId) {
        return patientRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PATIENT_NOT_EXIST));
    }

    private Caregiver getCaregiverByMemberId(Long memberId) {
        return caregiverRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CAREGIVER_NOT_EXIST));
    }
}
