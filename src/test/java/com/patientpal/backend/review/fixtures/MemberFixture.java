package com.patientpal.backend.review.fixtures;

import com.patientpal.backend.member.domain.Address;
import com.patientpal.backend.member.domain.Gender;
import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.domain.Provider;
import com.patientpal.backend.member.domain.Role;

public class MemberFixture {
    public static Member createMember(Long id, String username, String name) {
        return Member.builder()
                .id(id)
                .username(username)
                .name(name)
                .contact("010-1234-5678")
                .provider(Provider.LOCAL)
                .role(Role.USER)
                .gender(Gender.MALE)
                .address(new Address("12345", "Seoul", "Gangnam"))
                .profileImageUrl("http://example.com/image.jpg")
                .build();
    }

}
