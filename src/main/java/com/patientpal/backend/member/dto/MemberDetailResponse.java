package com.patientpal.backend.member.dto;


import com.patientpal.backend.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class MemberDetailResponse {

    private String username;

    private String name;

    private String profileImageUrl;

    public static MemberDetailResponse of(Member member) {
        return MemberDetailResponse.builder()
                .username(member.getUsername())
                .name(member.getName())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }
}
