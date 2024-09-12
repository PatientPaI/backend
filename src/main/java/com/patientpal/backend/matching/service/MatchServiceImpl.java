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
import static com.patientpal.backend.matching.service.MatchValidation.validateIsNotExistFirstRequestMember;
import static com.patientpal.backend.matching.service.MatchValidation.validateMatchAuthorization;
import static com.patientpal.backend.member.domain.Role.CAREGIVER;
import static com.patientpal.backend.member.domain.Role.USER;

import com.patientpal.backend.caregiver.repository.CaregiverRepository;
import com.patientpal.backend.common.exception.AuthorizationException;
import com.patientpal.backend.common.exception.BusinessException;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.common.exception.ErrorCode;
import com.patientpal.backend.matching.domain.Match;
import com.patientpal.backend.matching.domain.MatchRepository;
import com.patientpal.backend.matching.domain.MatchStatus;
import com.patientpal.backend.matching.dto.request.CreateMatchCaregiverRequest;
import com.patientpal.backend.matching.dto.request.CreateMatchPatientRequest;
import com.patientpal.backend.matching.dto.response.CreateMatchResponse;
import com.patientpal.backend.matching.dto.response.ReceivedMatchListResponse;
import com.patientpal.backend.matching.dto.response.ReceivedMatchResponse;
import com.patientpal.backend.matching.dto.response.RequestMatchListResponse;
import com.patientpal.backend.matching.dto.response.MatchResponse;
import com.patientpal.backend.matching.dto.response.RequestMatchResponse;
import com.patientpal.backend.matching.exception.DuplicateRequestException;
import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.notification.annotation.NeedNotification;
import com.patientpal.backend.patient.domain.Patient;
import com.patientpal.backend.member.repository.MemberRepository;
import com.patientpal.backend.patient.repository.PatientRepository;
import io.micrometer.core.annotation.Timed;
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
@Timed("matches")
@Transactional(readOnly = true)
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final MemberRepository memberRepository;
    private final PatientRepository patientRepository;
    private final CaregiverRepository caregiverRepository;

    @Override
    public CreateMatchResponse getCreateMatchRequest(String username, Long responseMemberId) {
        Member requestMember = getMemberByUsername(username);
        Member responseMember = getMemberById(responseMemberId);
        if (requestMember.getRole() == USER) {
            if (responseMember.getRole() == USER) {
                throw new BusinessException(ErrorCode.CAN_NOT_REQUEST_TO);
            }
            Patient patient = getPatientByMemberId(requestMember.getId());
            Caregiver caregiver = getCaregiverByMemberId(responseMember.getId());
            return CreateMatchResponse.of(patient, caregiver);
        } else if (requestMember.getRole() == CAREGIVER) {
            if (responseMember.getRole() == CAREGIVER) {
                throw new BusinessException(ErrorCode.CAN_NOT_REQUEST_TO);
            }
            Caregiver caregiver = getCaregiverByMemberId(requestMember.getId());
            Patient patient = getPatientByMemberId(responseMember.getId());
            return CreateMatchResponse.of(patient, caregiver);
        } else {
            throw new AuthorizationException(AUTHORIZATION_FAILED);
        }
    }

    @Transactional
    @Override
    @NeedNotification
    public MatchResponse createMatchPatient(String username, Long responseMemberId,
                                            CreateMatchPatientRequest createMatchRequest) {
        Patient patient = getPatientByUsername(username);
        Caregiver caregiver = getCaregiverByMemberId(responseMemberId);
        validateIsInMatchList(patient);
        validateIsInMatchList(caregiver);

        if (matchRepository.existsPendingMatch(patient.getId(), caregiver.getId())) {
            throw new DuplicateRequestException(DUPLICATED_REQUEST);
        }

        return createPatientMatch(patient, caregiver, createMatchRequest);
    }

    @Transactional
    @Override
    @NeedNotification
    public MatchResponse createMatchCaregiver(String username, Long responseMemberId,
                                              CreateMatchCaregiverRequest createMatchRequest) {
        Caregiver caregiver = getCaregiverByUsername(username);
        Patient patient = getPatientByMemberId(responseMemberId);
        validateIsInMatchList(caregiver);
        validateIsInMatchList(patient);

        if (matchRepository.existsPendingMatch(caregiver.getId(), patient.getId())) {
            throw new DuplicateRequestException(DUPLICATED_REQUEST);
        }

        return createCaregiverMatch(caregiver, patient, createMatchRequest);
    }

    private MatchResponse createPatientMatch(Patient patient, Caregiver caregiver,
                                             CreateMatchPatientRequest createMatchRequest) {
        Match match = matchRepository.save(
                MatchResponse.toEntityFirstPatient(patient, caregiver, createMatchRequest));
        log.info("매칭 신청 성공 ! 요청 : {}, 수락 : {}", patient.getName(), caregiver.getName());
        MatchResponse matchResponse = MatchResponse.of(match);
        return new MatchNotificationProxy(matchResponse, MatchNotificationMemberResponse.from(caregiver));
    }

    private MatchResponse createCaregiverMatch(Caregiver caregiver, Patient patient,
                                               CreateMatchCaregiverRequest createMatchRequest) {
        Match match = matchRepository.save(
                MatchResponse.toEntityFirstCaregiver(caregiver, patient, createMatchRequest));
        log.info("매칭 신청 성공 ! 요청 : {}, 수락 : {}", caregiver.getName(), patient.getName());
        MatchResponse matchResponse = MatchResponse.of(match);
        return new MatchNotificationProxy(matchResponse, MatchNotificationMemberResponse.from(patient));
    }

    @Transactional
    @Override
    public MatchResponse getMatch(Long matchId, String username) {
        Match findMatch = getMatchById(matchId);
        Member currentMember = getMemberByUsername(username);
        validateMatchAuthorization(findMatch, currentMember);
        validateIsCanceled(findMatch);
        setMatchReadStatus(findMatch, currentMember);
        return MatchResponse.of(findMatch);
    }

    @Transactional(readOnly = true)
    @Override
    public MatchResponse getMatchWithMember(Long matchId, String username) {
        Match findMatch = getMatchById(matchId);
        Member currentMember = getMemberByUsername(username);

        MatchResponse matchResponse = MatchResponse.of(findMatch);

        validateMatchAuthorization(findMatch, currentMember);
        validateIsCanceled(findMatch);
        setMatchReadStatus(findMatch, currentMember);

        return matchResponse;
    }

    private Match getMatchById(Long matchId) {
        return matchRepository.findById(matchId).orElseThrow(() -> new EntityNotFoundException(MATCH_NOT_EXIST));
    }

    @Override
    public RequestMatchListResponse getRequestMatches(String username, Long memberId, Pageable pageable) {
        Member currentMember = getMemberById(memberId);
        Page<Match> matchPage = matchRepository.findAllRequest(currentMember.getId(), pageable);
        List<RequestMatchResponse> requestMatchListResponse = matchPage.stream()
                .map(RequestMatchResponse::of)
                .toList();
        return RequestMatchListResponse.from(matchPage, requestMatchListResponse);
    }

    @Override
    public ReceivedMatchListResponse getReceivedMatches(String username, Long memberId, Pageable pageable) {
        Member currentMember = getMemberById(memberId);
        Page<Match> matchPage = matchRepository.findAllReceived(currentMember.getId(), pageable);
        List<ReceivedMatchResponse> matchListResponse = matchPage.stream()
                .map(ReceivedMatchResponse::of)
                .toList();
        return ReceivedMatchListResponse.from(matchPage, matchListResponse);
    }

    @Transactional
    @Override
    public void cancelMatch(Long matchId, String username) {
        Match match = getMatchById(matchId);
        if (!match.getRequestMember().getUsername().equals(username)) {
            throw new AuthorizationException(AUTHORIZATION_FAILED);
        }
        validateCancellation(match);
        match.setMatchStatus(CANCELED);
    }

    @Transactional
    @Override
    public void acceptMatch(Long matchId, String username) {
        Match match = getMatchById(matchId);
        if (!match.getReceivedMember().getUsername().equals(username)) {
            throw new AuthorizationException(AUTHORIZATION_FAILED);
        }
        validateAcceptance(match);
        validateIsNotExistFirstRequestMember(match.getRequestMember());
        match.setMatchStatus(ACCEPTED);
        match.setReadStatus(READ);
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

    private Patient getPatientByMemberId(Long memberId) {
        return patientRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PATIENT_NOT_EXIST));
    }

    private Caregiver getCaregiverByMemberId(Long memberId) {
        return caregiverRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CAREGIVER_NOT_EXIST));
    }

    private Member getMemberByUsername(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_EXIST, username));
    }

    private Caregiver getCaregiverByUsername(String username) {
        return caregiverRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_EXIST, username));
    }

    private Patient getPatientByUsername(String username) {
        return patientRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_EXIST, username));
    }

    private Member getMemberById(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_EXIST));
    }

}
