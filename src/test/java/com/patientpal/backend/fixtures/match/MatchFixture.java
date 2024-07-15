package com.patientpal.backend.fixtures.match;

import static com.patientpal.backend.fixtures.member.MemberFixture.DEFAULT_PASSWORD;
import static com.patientpal.backend.fixtures.member.MemberFixture.DEFAULT_USERNAME;
import static com.patientpal.backend.fixtures.patient.PatientFixture.NOK_CONTACT;
import static com.patientpal.backend.fixtures.patient.PatientFixture.NOK_NAME;
import static com.patientpal.backend.fixtures.patient.PatientFixture.PATIENT_ADDRESS;
import static com.patientpal.backend.fixtures.patient.PatientFixture.PATIENT_PHONE_NUMBER;
import static com.patientpal.backend.fixtures.patient.PatientFixture.PATIENT_REAL_CARE_PLACE;
import static com.patientpal.backend.fixtures.patient.PatientFixture.PATIENT_SIGNIFICANT;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.fixtures.caregiver.CaregiverFixture;
import com.patientpal.backend.matching.domain.FirstRequest;
import com.patientpal.backend.matching.domain.Match;
import com.patientpal.backend.matching.domain.MatchStatus;
import com.patientpal.backend.matching.domain.ReadStatus;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.domain.Provider;
import com.patientpal.backend.member.domain.Role;
import com.patientpal.backend.patient.domain.Patient;

public class MatchFixture {

    public static Match createMatchForPatient(Member requestMember, Member responseMember) {
        return Match.builder()
                .requestMember(requestMember)
                .receivedMember(responseMember)
                .matchStatus(MatchStatus.PENDING)
                .readStatus(ReadStatus.UNREAD)
                .firstRequest(FirstRequest.PATIENT_FIRST)
                .build();
    }

    public static Match createMatchForCaregiver(Member requestMember, Member responseMember) {
        return Match.builder()
                .requestMember(requestMember)
                .receivedMember(responseMember)
                .matchStatus(MatchStatus.PENDING)
                .readStatus(ReadStatus.UNREAD)
                .firstRequest(FirstRequest.CAREGIVER_FIRST)
                .build();
    }

    public static Member patientMemberForMatch() {
        return Member.builder()
                .username(DEFAULT_USERNAME)
                .password(DEFAULT_PASSWORD)
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();
    }
    public static Member caregiverMemberForMatch() {
        return Member.builder()
                .username(DEFAULT_USERNAME)
                .password(DEFAULT_PASSWORD)
                .role(Role.CAREGIVER)
                .provider(Provider.LOCAL)
                .build();
    }

    public static Patient defaultPatientForMatch() {
        return Patient.builder()
                .username(DEFAULT_USERNAME)
                .password(DEFAULT_PASSWORD)
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .contact(PATIENT_PHONE_NUMBER)
                .address(PATIENT_ADDRESS)
                .isNok(false)
                .realCarePlace(PATIENT_REAL_CARE_PLACE)
                .nokName(NOK_NAME)
                .nokContact(NOK_CONTACT)
                .patientSignificant(PATIENT_SIGNIFICANT)
                .build();
    }

    public static Caregiver defaultCaregiverForMatch() {
        return Caregiver.builder()
                .username(DEFAULT_USERNAME)
                .password(DEFAULT_PASSWORD)
                .role(Role.CAREGIVER)
                .provider(Provider.LOCAL)
                .contact(CaregiverFixture.CAREGIVER_NAME)
                .address(PATIENT_ADDRESS)
                .caregiverSignificant(PATIENT_SIGNIFICANT)
                .build();
    }
}
