package com.patientpal.backend.fixtures.member;

import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.domain.Provider;
import com.patientpal.backend.member.domain.Role;

public class MemberFixture {

    public static final String DEFAULT_USERNAME = "patientpal";
    public static final String HUSEONG_USERNAME = "huseong";
    public static final String DOHYUN_USERNAME = "dohyun";
    public static final String JEONGHYE_USERNAME = "jeonghye";

    public static final String DEFAULT_NAME = "간병";
    public static final String HUSEONG_NAME = "후성";
    public static final String DOHYUN_NAME = "도현";
    public static final String JEONGHYE_NAME = "정혜";

    public static final Provider DEFAULT_PROVIDER = Provider.LOCAL;
    public static final String DEFAULT_CONTACT = "010-1234-5678";
    public static final String DEFAULT_PASSWORD = "password123";

    public static Member createDefaultMember() {
        return createMemberBuilder().build();
    }

    public static Member huseongRoleCaregiver() {
        return createMemberBuilderWithCaregiverRole().username(HUSEONG_USERNAME)
                .build();
    }

    public static Member dohyunRoleCaregiver() {
        return createMemberBuilderWithCaregiverRole().username(DOHYUN_USERNAME)
                .build();
    }

    public static Member jeonghyeRoleCaregiver() {
        return createMemberBuilderWithCaregiverRole().username(JEONGHYE_USERNAME)
                .build();
    }

    public static Member huseongRolePatient() {
        return createMemberBuilderWithUserRole().username(HUSEONG_USERNAME)
                .build();
    }

    public static Member dohyunRolePatient() {
        return createMemberBuilderWithUserRole().username(DOHYUN_USERNAME)
                .build();
    }

    public static Member jeonghyeRolePatient() {
        return createMemberBuilderWithUserRole().username(JEONGHYE_USERNAME)
                .build();
    }


    private static Member.MemberBuilder createMemberBuilder() {
        return Member.builder()
                .username(DEFAULT_USERNAME)
                .password(DEFAULT_PASSWORD)
                .provider(DEFAULT_PROVIDER)
                .role(Role.USER)
                .contact(DEFAULT_CONTACT);
    }

    private static Member.MemberBuilder createMemberBuilderWithUserRole() {
        return createMemberBuilder().role(Role.USER);
    }

    private static Member.MemberBuilder createMemberBuilderWithCaregiverRole() {
        return createMemberBuilder().role(Role.CAREGIVER);
    }

    private static Member.MemberBuilder createMemberBuilderWithAdminRole() {
        return createMemberBuilder().role(Role.ADMIN);
    }
}
