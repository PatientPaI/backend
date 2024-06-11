package com.patientpal.backend.fixtures.member;

import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.domain.Provider;
import com.patientpal.backend.member.domain.Role;

public class MemberFixture {

    public static final String DEFAULT_USERNAME = "testuser";
    public static final String DEFAULT_PASSWORD = "password123";
    public static final Provider DEFAULT_PROVIDER = Provider.LOCAL;
    public static final Role DEFAULT_ROLE = Role.USER;
    public static final String DEFAULT_CONTACT = "010-1234-5678";

    public static Member createDefaultMember() {
        return createMemberBuilder().build();
    }

    private static Member.MemberBuilder createMemberBuilder() {
        return Member.builder()
                .username(DEFAULT_USERNAME)
                .password(DEFAULT_PASSWORD)
                .provider(DEFAULT_PROVIDER)
                .role(DEFAULT_ROLE)
                .contact(DEFAULT_CONTACT);
    }
}
