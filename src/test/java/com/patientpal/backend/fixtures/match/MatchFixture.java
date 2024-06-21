package com.patientpal.backend.fixtures.match;

import static com.patientpal.backend.fixtures.member.MemberFixture.DEFAULT_PASSWORD;
import static com.patientpal.backend.fixtures.member.MemberFixture.DEFAULT_USERNAME;

import com.patientpal.backend.caregiver.domain.Caregiver;
import com.patientpal.backend.fixtures.caregiver.CaregiverFixture;
import com.patientpal.backend.fixtures.patient.PatientFixture;
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
                .patient(requestMember.getPatient())
                .caregiver(responseMember.getCaregiver())
                .matchStatus(MatchStatus.PENDING)
                .readStatus(ReadStatus.UNREAD)
                .firstRequest(FirstRequest.CAREGIVER_FIRST)
                .build();
    }

    public static Match createMatchForCaregiver(Member requestMember, Member responseMember) {
        return Match.builder()
                .caregiver(requestMember.getCaregiver())
                .patient(responseMember.getPatient())
                .matchStatus(MatchStatus.PENDING)
                .readStatus(ReadStatus.UNREAD)
                .build();
    }

    public static Member patientMemberForMatch() {
        Member member = Member.builder()
                .username(DEFAULT_USERNAME)
                .password(DEFAULT_PASSWORD)
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();

        Patient patient = Patient.builder()
                .member(member)
                .name(PatientFixture.NAME)
                .isInMatchList(true)
                .build();


        return Member.builder()
                .username(member.getUsername())
                .password(member.getPassword())
                .role(member.getRole())
                .provider(member.getProvider())
                .patient(patient)
                .build();
    }
    public static Member caregiverMemberForMatch() {
        Member member = Member.builder()
                .username(DEFAULT_USERNAME)
                .password(DEFAULT_PASSWORD)
                .role(Role.CAREGIVER)
                .provider(Provider.LOCAL)
                .build();

        Caregiver caregiver = Caregiver.builder()
                .member(member)
                .name(CaregiverFixture.NAME)
                .isInMatchList(true)
                .build();


        return Member.builder()
                .username(member.getUsername())
                .password(member.getPassword())
                .role(member.getRole())
                .provider(member.getProvider())
                .caregiver(caregiver)
                .build();
    }
}
