package com.patientpal.backend.matching.service;

import com.patientpal.backend.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchNotificationMemberResponse {

    private Long id;

    private String username;

    @Builder
    public MatchNotificationMemberResponse(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public static MatchNotificationMemberResponse from(Member member) {
        return MatchNotificationMemberResponse.builder()
                .username(member.getUsername())
                .build();
    }
}
