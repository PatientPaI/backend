package com.patientpal.backend.member.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberCompleteProfileResponse {
    private Long memberId;
    private String memberName;
    private Boolean isCompleteProfile;

    @Builder
    public MemberCompleteProfileResponse(Long memberId, String memberName, Boolean isCompleteProfile) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.isCompleteProfile = isCompleteProfile;
    }

    public static MemberCompleteProfileResponse of(Long memberId, String memberName, Boolean isCompleteProfile) {
        return MemberCompleteProfileResponse.builder()
                .memberId(memberId)
                .memberName(memberName)
                .isCompleteProfile(isCompleteProfile)
                .build();
    }
}
