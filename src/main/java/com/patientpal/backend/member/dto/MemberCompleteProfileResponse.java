package com.patientpal.backend.member.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberCompleteProfileResponse {
    private Long memberId;
    private Boolean isCompleteProfile;

    @Builder
    public MemberCompleteProfileResponse(Long memberId, Boolean isCompleteProfile) {
        this.memberId = memberId;
        this.isCompleteProfile = isCompleteProfile;
    }

    public static MemberCompleteProfileResponse of(Long memberId, Boolean isCompleteProfile) {
        return MemberCompleteProfileResponse.builder()
                .memberId(memberId)
                .isCompleteProfile(isCompleteProfile)
                .build();
    }
}
