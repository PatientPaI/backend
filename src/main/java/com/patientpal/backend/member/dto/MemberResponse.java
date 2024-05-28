package com.patientpal.backend.member.dto;

import com.patientpal.backend.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberResponse {
    private String username;

    @Builder
    private MemberResponse(String username) {
        this.username = username;
    }

    public static MemberResponse of(final Member member) {
        return MemberResponse.builder()
                .username(member.getUsername())
                .build();
    }
}
