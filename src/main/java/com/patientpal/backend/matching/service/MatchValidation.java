package com.patientpal.backend.matching.service;

import com.patientpal.backend.common.exception.AuthorizationException;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.matching.domain.Match;
import com.patientpal.backend.matching.exception.CanNotReadException;
import com.patientpal.backend.matching.exception.CanNotRequestException;
import com.patientpal.backend.matching.exception.DuplicateRequestException;
import com.patientpal.backend.matching.exception.NotCompleteProfileException;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.common.exception.ProfileNotCompletedException;

import static com.patientpal.backend.common.exception.ErrorCode.*;
import static com.patientpal.backend.matching.domain.FirstRequest.CAREGIVER_FIRST;
import static com.patientpal.backend.matching.domain.FirstRequest.PATIENT_FIRST;
import static com.patientpal.backend.matching.domain.MatchStatus.ACCEPTED;
import static com.patientpal.backend.matching.domain.MatchStatus.CANCELED;
import static com.patientpal.backend.member.domain.Role.CAREGIVER;
import static com.patientpal.backend.member.domain.Role.USER;

/**
 * TODO
 *  - 매칭 리스트에 등록하기 선택하면 -> isInMatchList 를 true 로 변경해야 함.
 *  이후 밑 주석 해제
 */

public final class MatchValidation {

    public static void validatePatientRequest(Member requestMember, Member responseMember) {
        if (requestMember.getPatient() == null) {
            throw new EntityNotFoundException(PATIENT_NOT_EXIST, requestMember.getUsername());
        }
        if (responseMember.getCaregiver() == null) {
            throw new EntityNotFoundException(CAREGIVER_NOT_EXIST);
        }
        validateIsCompletedProfile(requestMember);
//        상대 세부 프로필은 등록되어 있음을 보장 (리스트에 올리려면 세부 프로필 등록 해야 하기 때문)
//        validateIsInMatchList(responseMember);
    }

    public static void validateCaregiverRequest(Member requestMember, Member responseMember) {
        if (requestMember.getCaregiver() == null) {
            throw new EntityNotFoundException(CAREGIVER_NOT_EXIST, requestMember.getUsername());
        }
        if (responseMember.getPatient() == null) {
            throw new EntityNotFoundException(PATIENT_NOT_EXIST);
        }
        validateIsCompletedProfile(requestMember);
//        상대 세부 프로필은 등록되어 있음을 보장 (리스트에 올리려면 세부 프로필 등록 해야 하기 때문)
//        validateIsInMatchList(responseMember);
    }

    public static void validateIsCompletedProfile(Member member) {
        if (!member.getIsCompletedProfile()) {
            throw new NotCompleteProfileException(NOT_COMPLETE_PROFILE);
        }
    }
    private static void validateIsInMatchList(Member member) {
        if (!member.getIsInMatchList()) {
            throw new CanNotRequestException(CAN_NOT_REQUEST_TO);
        }
    }

    public static void validateIsCanceled(Match findMatch) {
        if (findMatch.getMatchStatus() == CANCELED) throw new CanNotReadException(CAN_NOT_READ);
    }

    public static void validateMatchAuthorization(Match findMatch, String username) {
        if (!findMatch.getPatient().getMember().getUsername().equals(username) &&
                !findMatch.getCaregiver().getMember().getUsername().equals(username)) {
            throw new AuthorizationException(AUTHORIZATION_FAILED);
        }
    }

    public static void validateCancellation(Match match, Member currentMember) {
        if (match.getMatchStatus() == CANCELED) {
            throw new DuplicateRequestException(MATCH_ALREADY_CANCELED);
        }
        if (match.getMatchStatus() == ACCEPTED) {
            throw new DuplicateRequestException(CAN_NOT_CANCEL_ALREADY_ACCEPTED_MATCH);
        }
        if (currentMember.getRole() == USER) {
            validatePatientCancellation(match, currentMember);
        } else if (currentMember.getRole() == CAREGIVER) {
            validateCaregiverCancellation(match, currentMember);
        }
    }

    private static void validatePatientCancellation(Match match, Member currentMember) {
        if (match.getFirstRequest() == CAREGIVER_FIRST || !match.getPatient().getId().equals(currentMember.getPatient().getId())) {
            throw new AuthorizationException(AUTHORIZATION_FAILED);
        }
    }

    private static void validateCaregiverCancellation(Match match, Member currentMember) {
        if (match.getFirstRequest() == PATIENT_FIRST || !match.getCaregiver().getId().equals(currentMember.getCaregiver().getId())) {
            throw new AuthorizationException(AUTHORIZATION_FAILED);
        }
    }

    public static void validateAcceptance(Match match) {
        if (match.getMatchStatus() == ACCEPTED) {
            throw new DuplicateRequestException(MATCH_ALREADY_ACCEPTED);
        }
        if (match.getMatchStatus() == CANCELED) {
            throw new DuplicateRequestException(MATCH_ALREADY_CANCELED);
        }
    }
}
