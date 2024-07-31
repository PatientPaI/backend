package com.patientpal.backend.review.fixtures;

import com.patientpal.backend.matching.domain.FirstRequest;
import com.patientpal.backend.matching.domain.Match;
import com.patientpal.backend.matching.domain.MatchStatus;
import com.patientpal.backend.matching.domain.ReadStatus;
import com.patientpal.backend.member.domain.Member;
import java.time.LocalDateTime;

public class MatchFixture {
    public static Match createMatch() {
        Member requestMember = MemberFixture.createMember(1L, "requester", "John Doe");
        Member receivedMember = MemberFixture.createMember(2L, "receiver", "Jane Doe");

        return Match.builder()
                .requestMember(requestMember)
                .receivedMember(receivedMember)
                .matchStatus(MatchStatus.COMPLETED)
                .readStatus(ReadStatus.READ)
                .firstRequest(FirstRequest.PATIENT_FIRST)
                .careStartDateTime(LocalDateTime.of(2024, 7, 1, 8, 0))
                .careEndDateTime(LocalDateTime.of(2024, 7, 2, 8, 0))
                .totalAmount(150000L)
                .requestMemberCurrentSignificant("Important medical condition")
                .realCarePlace("Seoul")
                .isNok(true)
                .nokName("Emergency Contact")
                .nokContact("123-456-7890")
                .build();
    }
}
