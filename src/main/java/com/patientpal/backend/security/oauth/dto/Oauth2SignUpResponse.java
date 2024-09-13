package com.patientpal.backend.security.oauth.dto;

import com.patientpal.backend.member.domain.Address;
import com.patientpal.backend.member.domain.Gender;
import com.patientpal.backend.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Oauth2SignUpResponse {
    private String email;
    private String username;
    private String name;
    private String role;
    private String provider;
    private String token;
    private Long memberId;
    private Boolean isProfilePublic;
    private Boolean isCompleteProfile;
    private String profileImageUrl;
    private String contact;
    private Address address;
    private Gender gender;
    private int age;


    public static Oauth2SignUpResponse fromMember(Member member, String username, String email, String name, String role, String provider, String token) {
        return Oauth2SignUpResponse.builder()
                .email(email)
                .username(username)
                .name(name)
                .role(role)
                .provider(provider)
                .token(token)
                .memberId(member.getId())
                .isProfilePublic(member.getIsProfilePublic())
                .isCompleteProfile(member.getIsCompleteProfile())
                .profileImageUrl(member.getProfileImageUrl())
                .contact(member.getContact())
                .address(member.getAddress())
                .gender(member.getGender())
                .age(member.getAge())
                .build();
    }
}
