package com.patientpal.backend.fixtures.member;

import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.domain.Provider;
import com.patientpal.backend.member.domain.Role;

public class MemberFixture {

    public static final String DEFAULT_USERNAME = "patientpal";
    public static final Provider DEFAULT_PROVIDER = Provider.LOCAL;
    public static final String DEFAULT_CONTACT = "010-1234-5678";
    public static final String DEFAULT_PASSWORD = "password123";
    public static final Long DEFAULT_ID = 1L;
    public static final Integer DEFAULT_AGE = 30;

    public static Member createDefaultMember() {
        return createMemberBuilder().build();
    }

    public static Member defaultRoleCaregiver() {
        return createMemberBuilderWithCaregiverRole().username(DEFAULT_USERNAME)
                .build();
    }


    public static Member defaultRolePatient() {
        return createMemberBuilderWithUserRole().username(DEFAULT_USERNAME)
                .build();
    }

    private static Member.MemberBuilder createMemberBuilder() {
        return Member.builder()
                .id(DEFAULT_ID)
                .username(DEFAULT_USERNAME)
                .password(DEFAULT_PASSWORD)
                .provider(DEFAULT_PROVIDER)
                .role(Role.USER)
                .contact(DEFAULT_CONTACT)
                .age(DEFAULT_AGE);
    }

    private static Member.MemberBuilder createMemberBuilderWithUserRole() {
        return createMemberBuilder().role(Role.USER);
    }

    private static Member.MemberBuilder createMemberBuilderWithCaregiverRole() {
        return createMemberBuilder().role(Role.CAREGIVER);
    }

}
