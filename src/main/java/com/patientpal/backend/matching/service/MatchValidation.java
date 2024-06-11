package com.patientpal.backend.matching.service;

import static com.patientpal.backend.common.exception.ErrorCode.AUTHORIZATION_FAILED;
import static com.patientpal.backend.common.exception.ErrorCode.CAN_NOT_ACCEPT_ALREADY_DELETE_PROFILE;
import static com.patientpal.backend.common.exception.ErrorCode.CAN_NOT_CANCEL_ALREADY_ACCEPTED_MATCH;
import static com.patientpal.backend.common.exception.ErrorCode.CAN_NOT_READ;
import static com.patientpal.backend.common.exception.ErrorCode.CAN_NOT_REQUEST_TO;
import static com.patientpal.backend.common.exception.ErrorCode.CAREGIVER_NOT_EXIST;
import static com.patientpal.backend.common.exception.ErrorCode.MATCH_ALREADY_ACCEPTED;
import static com.patientpal.backend.common.exception.ErrorCode.MATCH_ALREADY_CANCELED;
import static com.patientpal.backend.common.exception.ErrorCode.NOT_COMPLETE_PROFILE;
import static com.patientpal.backend.common.exception.ErrorCode.PATIENT_NOT_EXIST;
import static com.patientpal.backend.matching.domain.FirstRequest.CAREGIVER_FIRST;
import static com.patientpal.backend.matching.domain.FirstRequest.PATIENT_FIRST;
import static com.patientpal.backend.matching.domain.MatchStatus.ACCEPTED;
import static com.patientpal.backend.matching.domain.MatchStatus.CANCELED;
import static com.patientpal.backend.member.domain.Role.CAREGIVER;
import static com.patientpal.backend.member.domain.Role.USER;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.common.exception.AuthorizationException;
import com.patientpal.backend.common.exception.EntityNotFoundException;
import com.patientpal.backend.matching.domain.Match;
import com.patientpal.backend.matching.exception.CanNotAcceptException;
import com.patientpal.backend.matching.exception.CanNotReadException;
import com.patientpal.backend.matching.exception.CanNotRequestException;
import com.patientpal.backend.matching.exception.DuplicateRequestException;
import com.patientpal.backend.matching.exception.NotCompleteProfileException;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.patient.domain.Patient;

public final class MatchValidation {

    public static void validatePatientRequest(Member requestMember, Member responseMember) {
        if (requestMember.getPatient() == null) {
            throw new NotCompleteProfileException(NOT_COMPLETE_PROFILE, requestMember.getUsername());
        }
        if (responseMember.getCaregiver() == null) {
            throw new EntityNotFoundException(CAREGIVER_NOT_EXIST);
        }
        validateIsInMatchListCaregiver(responseMember.getCaregiver());
    }

    public static void validateCaregiverRequest(Member requestMember, Member responseMember) {
        if (requestMember.getCaregiver() == null) {
            throw new NotCompleteProfileException(NOT_COMPLETE_PROFILE, requestMember.getUsername());
        }
        if (responseMember.getPatient() == null) {
            throw new EntityNotFoundException(PATIENT_NOT_EXIST);
        }
        validateIsInMatchListPatient(responseMember.getPatient());
    }

    private static void validateIsInMatchListCaregiver(Caregiver caregiver) {
        if (!caregiver.getIsInMatchList()) {
            throw new CanNotRequestException(CAN_NOT_REQUEST_TO);
        }
    }

    private static void validateIsInMatchListPatient(Patient patient) {
        if (!patient.getIsInMatchList()) {
            throw new CanNotRequestException(CAN_NOT_REQUEST_TO);
        }
    }

    public static void validateIsNotExistCaregiver(Match match) {
        if (match.getCaregiver() == null) {
            throw new CanNotAcceptException(CAN_NOT_ACCEPT_ALREADY_DELETE_PROFILE);
        }
    }

    public static void validateIsNotExistPatient(Match match) {
        if (match.getPatient() == null) {
            throw new CanNotAcceptException(CAN_NOT_ACCEPT_ALREADY_DELETE_PROFILE);
        }
    }

    public static void validateIsCanceled(Match findMatch) {
        if (findMatch.getMatchStatus() == CANCELED) {
            throw new CanNotReadException(CAN_NOT_READ);
        }
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
        if (match.getFirstRequest() == CAREGIVER_FIRST || !match.getPatient().getId()
                .equals(currentMember.getPatient().getId())) {
            throw new AuthorizationException(AUTHORIZATION_FAILED);
        }
    }

    private static void validateCaregiverCancellation(Match match, Member currentMember) {
        if (match.getFirstRequest() == PATIENT_FIRST || !match.getCaregiver().getId()
                .equals(currentMember.getCaregiver().getId())) {
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
