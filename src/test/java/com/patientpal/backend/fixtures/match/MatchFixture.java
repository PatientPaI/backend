package com.patientpal.backend.fixtures.match;

import static com.patientpal.backend.fixtures.member.MemberFixture.DEFAULT_PASSWORD;
import static com.patientpal.backend.fixtures.member.MemberFixture.DEFAULT_USERNAME;

import com.patientpal.backend.matching.domain.FirstRequest;
import com.patientpal.backend.matching.domain.Match;
import com.patientpal.backend.matching.domain.MatchStatus;
import com.patientpal.backend.matching.domain.ReadStatus;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.domain.Provider;
import com.patientpal.backend.member.domain.Role;

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
}
