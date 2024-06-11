package com.patientpal.backend.matching.service;

import static com.patientpal.backend.common.exception.ErrorCode.AUTHORIZATION_FAILED;
import static com.patientpal.backend.common.exception.ErrorCode.CAREGIVER_NOT_EXIST;
import static com.patientpal.backend.common.exception.ErrorCode.DUPLICATED_REQUEST;
import static com.patientpal.backend.common.exception.ErrorCode.MATCH_NOT_EXIST;
import static com.patientpal.backend.common.exception.ErrorCode.MEMBER_NOT_EXIST;
import static com.patientpal.backend.common.exception.ErrorCode.PATIENT_NOT_EXIST;
import static com.patientpal.backend.matching.domain.FirstRequest.CAREGIVER_FIRST;
import static com.patientpal.backend.matching.domain.FirstRequest.PATIENT_FIRST;
import static com.patientpal.backend.matching.domain.MatchStatus.ACCEPTED;
import static com.patientpal.backend.matching.domain.MatchStatus.CANCELED;
import static com.patientpal.backend.matching.domain.ReadStatus.READ;
import static com.patientpal.backend.matching.domain.ReadStatus.UNREAD;
import static com.patientpal.backend.matching.service.MatchValidation.validateAcceptance;
import static com.patientpal.backend.matching.service.MatchValidation.validateCancellation;
import static com.patientpal.backend.matching.service.MatchValidation.validateCaregiverRequest;
import static com.patientpal.backend.matching.service.MatchValidation.validateIsCanceled;
import static com.patientpal.backend.matching.service.MatchValidation.validateIsNotExistCaregiver;
import static com.patientpal.backend.matching.service.MatchValidation.validateIsNotExistPatient;
import static com.patientpal.backend.matching.service.MatchValidation.validateMatchAuthorization;
import static com.patientpal.backend.matching.service.MatchValidation.validatePatientRequest;
import static com.patientpal.backend.member.domain.Role.CAREGIVER;
import static com.patientpal.backend.member.domain.Role.USER;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.caregiver.repository.CaregiverRepository;
import com.patientpal.backend.common.exception.AuthorizationException;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.matching.domain.Match;
import com.patientpal.backend.matching.domain.MatchRepository;
import com.patientpal.backend.matching.dto.response.MatchListResponse;
import com.patientpal.backend.matching.dto.response.MatchResponse;
import com.patientpal.backend.matching.exception.DuplicateRequestException;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.repository.MemberRepository;
import com.patientpal.backend.patient.domain.Patient;
import com.patientpal.backend.patient.repository.PatientRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MatchServiceImpl implements MatchService {

    private final PatientRepository patientRepository;
    private final CaregiverRepository caregiverRepository;
    private final MatchRepository matchRepository;
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public MatchResponse createForPatient(User currentMember, Long responseMemberId) {
        Member requestMember = getMember(currentMember.getUsername());
        Member responseMember = getMemberById(responseMemberId);
        validatePatientRequest(requestMember, responseMember);
        if (matchRepository.existsPendingMatchForPatient(requestMember.getPatient().getId(),
                responseMember.getCaregiver().getId())) {
            throw new DuplicateRequestException(DUPLICATED_REQUEST);
        }
        return createMatchForPatient(requestMember, responseMember);
    }

    @Transactional
    @Override
    public MatchResponse createForCaregiver(User currentMember, Long responseMemberId) {
        Member requestMember = getMember(currentMember.getUsername());
        Member responseMember = getMemberById(responseMemberId);
        validateCaregiverRequest(requestMember, responseMember);
        if (matchRepository.existsPendingMatchForCaregiver(requestMember.getCaregiver().getId(),
                responseMember.getPatient().getId())) {
            throw new DuplicateRequestException(DUPLICATED_REQUEST);
        }
        return createMatchForCaregiver(requestMember, responseMember);
    }

    private MatchResponse createMatchForPatient(Member requestMember, Member responseMember) {
        Patient requestPatient = getPatientByMember(requestMember);
        Caregiver responseCaregiver = getCaregiverByMember(responseMember);
        String generatedPatientProfileSnapshot = generatePatientProfileSnapshot(requestPatient.getId());
        Match match = matchRepository.save(
                MatchResponse.toEntityFirstPatient(requestMember.getPatient(), responseMember.getCaregiver(), generatedPatientProfileSnapshot));
        log.info("매칭 성공 ! 신청 : {}, 수락 : {}", requestPatient.getName(), responseCaregiver.getName());
        return MatchResponse.of(match);
    }

    private MatchResponse createMatchForCaregiver(Member requestMember, Member responseMember) {
        Caregiver requestCaregiver = getCaregiverByMember(requestMember);
        Patient responsePatient = getPatientByMember(responseMember);
        String generatedCaregiverProfileSnapshot = generateCaregiverProfileSnapshot(requestCaregiver.getId());
        Match match = matchRepository.save(
                MatchResponse.toEntityFirstCaregiver(requestMember.getCaregiver(), responseMember.getPatient(), generatedCaregiverProfileSnapshot));
        log.info("매칭 성공 ! 신청 : {}, 수락 : {}", requestCaregiver.getName(), responsePatient.getName());
        return MatchResponse.of(match);
    }

    private Patient getPatientByMember(Member responseMember) {
        return patientRepository.findByMember(responseMember)
                .orElseThrow(() -> new EntityNotFoundException(PATIENT_NOT_EXIST, responseMember.getUsername()));
    }

    private Caregiver getCaregiverByMember(Member member) {
        return caregiverRepository.findByMember(member)
                .orElseThrow(() -> new EntityNotFoundException(CAREGIVER_NOT_EXIST, member.getUsername()));
    }

    private Member getMember(String username) {
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
        Member currentMember = getMember(username);
        validateMatchAuthorization(findMatch, username);
        validateIsCanceled(findMatch);
        setMatchReadStatus(findMatch, currentMember);
        return MatchResponse.of(findMatch); //여기 수정했음 다시 테스트해봐야함.
    }

    private Match getMatchById(Long matchId) {
        return matchRepository.findById(matchId).orElseThrow(() -> new EntityNotFoundException(MATCH_NOT_EXIST));
    }

    @Override
    public MatchListResponse getRequestMatches(String username, Pageable pageable) {
        Member currentMember = getMember(username);
        Page<Match> matchPage = getRequestMatchesWithPage(currentMember, pageable);
        List<MatchResponse> matchListResponse = matchPage.stream()
                .map(MatchResponse::of)
                .toList();
        return MatchListResponse.from(matchPage, matchListResponse);
    }

    @Override
    public MatchListResponse getReceivedMatches(String username, Pageable pageable) {
        Member currentMember = getMember(username);
        Page<Match> matchPage = getReceivedMatchesWithPage(currentMember, pageable);
        List<MatchResponse> matchListResponse = matchPage.stream()
                .map(MatchResponse::of)
                .toList();
        return MatchListResponse.from(matchPage, matchListResponse);
    }

    private Page<Match> getRequestMatchesWithPage(Member currentMember, Pageable pageable) {
        if (currentMember.getRole() == USER) {
            return matchRepository.findAllRequestByPatientId(currentMember.getPatient().getId(), pageable);
        } else if (currentMember.getRole() == CAREGIVER) {
            return matchRepository.findAllRequestByCaregiverId(currentMember.getCaregiver().getId(), pageable);
        } else {
            throw new AuthorizationException(AUTHORIZATION_FAILED, currentMember.getUsername());
        }
    }

    private Page<Match> getReceivedMatchesWithPage(Member currentMember, Pageable pageable) {
        if (currentMember.getRole() == USER) {
            return matchRepository.findAllReceivedByPatientId(currentMember.getPatient().getId(), pageable);
        } else if (currentMember.getRole() == CAREGIVER) {
            return matchRepository.findAllReceivedByCaregiverId(currentMember.getCaregiver().getId(), pageable);
        } else {
            throw new AuthorizationException(AUTHORIZATION_FAILED, currentMember.getUsername());
        }
    }

    @Transactional
    @Override
    public void cancelMatch(Long matchId, String username) {
        Match match = getMatchById(matchId);
        Member currentMember = getMember(username);
        validateCancellation(match, currentMember);
        match.setMatchStatus(CANCELED);
    }

    @Transactional
    @Override
    public void acceptMatch(Long matchId, String username) {
        Match match = getMatchById(matchId);
        Member currentMember = getMember(username);
        validateAcceptance(match);
        match.setReadStatus(READ);
        setMatchStatusAccepted(match, currentMember);
        matchRepository.save(match);
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
            if (match.getFirstRequest() == CAREGIVER_FIRST &&
                    match.getPatient().getId().equals(currentMember.getPatient().getId())) {
                validateIsNotExistCaregiver(match);
                match.setMatchStatus(ACCEPTED);
                match.setPatientProfileSnapshot(generatePatientProfileSnapshot(currentMember.getPatient().getId()));
            } else {
                throw new AuthorizationException(AUTHORIZATION_FAILED);
            }
        } else if (currentMember.getRole() == CAREGIVER) {
            if (match.getFirstRequest() == PATIENT_FIRST &&
                    match.getCaregiver().getId().equals(currentMember.getCaregiver().getId())) {
                validateIsNotExistPatient(match);
                match.setMatchStatus(ACCEPTED);
                match.setCaregiverProfileSnapshot(generateCaregiverProfileSnapshot(currentMember.getCaregiver().getId()));
            } else {
                throw new AuthorizationException(AUTHORIZATION_FAILED);
            }
        }
    }

    private String generatePatientProfileSnapshot(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException(PATIENT_NOT_EXIST));
        return String.format("Patient Snapshot - Name: %s, Address: %s, PatientSignificant: %s, CareRequirements : %s",
                patient.getName(),
                patient.getAddress(),
                patient.getPatientSignificant(),
                patient.getCareRequirements());
    }

    private String generateCaregiverProfileSnapshot(Long caregiverId) {
        Caregiver caregiver = caregiverRepository.findById(caregiverId)
                .orElseThrow(() -> new EntityNotFoundException(CAREGIVER_NOT_EXIST));
        return String.format("Caregiver Snapshot - Name: %s, Address: %s, Experience: %d years, CaregiverSignificant: %s, Specialization: %s",
                caregiver.getName(),
                caregiver.getAddress(),
                caregiver.getExperienceYears(),
                caregiver.getCaregiverSignificant(),
                caregiver.getSpecialization());
    }
}
