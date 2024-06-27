package com.patientpal.backend.matching.service;

import static com.patientpal.backend.common.exception.ErrorCode.AUTHORIZATION_FAILED;
import static com.patientpal.backend.common.exception.ErrorCode.CAN_NOT_ACCEPT_ALREADY_DELETE_PROFILE;
import static com.patientpal.backend.common.exception.ErrorCode.CAN_NOT_CANCEL_ALREADY_ACCEPTED_MATCH;
import static com.patientpal.backend.common.exception.ErrorCode.CAN_NOT_READ;
import static com.patientpal.backend.common.exception.ErrorCode.CAN_NOT_REQUEST_TO;
import static com.patientpal.backend.common.exception.ErrorCode.MATCH_ALREADY_ACCEPTED;
import static com.patientpal.backend.common.exception.ErrorCode.MATCH_ALREADY_CANCELED;
import static com.patientpal.backend.matching.domain.FirstRequest.CAREGIVER_FIRST;
import static com.patientpal.backend.matching.domain.FirstRequest.PATIENT_FIRST;
import static com.patientpal.backend.matching.domain.MatchStatus.ACCEPTED;
import static com.patientpal.backend.matching.domain.MatchStatus.CANCELED;
import static com.patientpal.backend.member.domain.Role.CAREGIVER;
import static com.patientpal.backend.member.domain.Role.USER;

import com.patientpal.backend.common.exception.AuthorizationException;
import com.patientpal.backend.matching.domain.Match;
import com.patientpal.backend.matching.exception.CanNotAcceptException;
import com.patientpal.backend.matching.exception.CanNotReadException;
import com.patientpal.backend.matching.exception.CanNotRequestException;
import com.patientpal.backend.matching.exception.DuplicateRequestException;
import com.patientpal.backend.member.domain.Member;

public final class MatchValidation {

    public static void validateIsInMatchList(Member member) {
        if (!member.getIsProfilePublic()) {
            throw new CanNotRequestException(CAN_NOT_REQUEST_TO);
        }
    }

    public static void validateIsNotExistCaregiver(Match match) {
        if (match.getRequestMember() == null) {
            throw new CanNotAcceptException(CAN_NOT_ACCEPT_ALREADY_DELETE_PROFILE);
        }
    }

    public static void validateIsNotExistPatient(Match match) {
        if (match.getRequestMember() == null) {
            throw new CanNotAcceptException(CAN_NOT_ACCEPT_ALREADY_DELETE_PROFILE);
        }
    }

    public static void validateIsCanceled(Match findMatch) {
        if (findMatch.getMatchStatus() == CANCELED) {
            throw new CanNotReadException(CAN_NOT_READ);
        }
    }

    public static void validateMatchAuthorization(Match findMatch, String username) {
        if (!findMatch.getRequestMember().getUsername().equals(username) &&
                !findMatch.getReceivedMember().getUsername().equals(username)) {
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
        if (match.getFirstRequest() == CAREGIVER_FIRST || !match.getRequestMember().getId()
                .equals(currentMember.getId())) {
            throw new AuthorizationException(AUTHORIZATION_FAILED);
        }
    }

    private static void validateCaregiverCancellation(Match match, Member currentMember) {
        if (match.getFirstRequest() == PATIENT_FIRST || !match.getRequestMember().getId()
                .equals(currentMember.getId())) {
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
