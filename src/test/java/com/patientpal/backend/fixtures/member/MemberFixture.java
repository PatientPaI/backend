package com.patientpal.backend.fixtures.member;

import com.patientpal.backend.member.domain.Member;
import com.patientpal.backend.member.domain.Provider;
import com.patientpal.backend.member.domain.Role;

public class MemberFixture {

    public static final String HUSEONG_USERNAME = "huseong";
    public static final String DOHYUN_USERNAME = "dohyun";
    public static final String JEONGHYE_USERNAME = "jeonghye";

    public static final String HUSEONG_NAME = "후성";
    public static final String DOHYUN_NAME = "도현";
    public static final String JEONGHYE_NAME = "정혜";

    public static final String PHONE_NUMBER = "010-1234-5678";

    public static Member huseongRoleCaregiver() {
        return Member.builder()
                .username(HUSEONG_USERNAME)
                .provider(Provider.LOCAL)
                .role(Role.CAREGIVER)
                .contact(PHONE_NUMBER)
                .build();
    }

    public static Member dohyunRoleCaregiver() {
        return Member.builder()
                .username(DOHYUN_USERNAME)
                .provider(Provider.LOCAL)
                .role(Role.CAREGIVER)
                .contact(PHONE_NUMBER)
                .build();
    }

    public static Member jeonghyeRoleCaregiver() {
        return Member.builder()
                .username(JEONGHYE_USERNAME)
                .provider(Provider.LOCAL)
                .role(Role.CAREGIVER)
                .contact(PHONE_NUMBER)
                .build();
    }

    public static Member huseongRolePatient() {
        return Member.builder()
                .username(HUSEONG_USERNAME)
                .provider(Provider.LOCAL)
                .role(Role.USER)
                .contact(PHONE_NUMBER)
                .build();
    }

    public static Member dohyunRolePatient() {
        return Member.builder()
                .username(DOHYUN_USERNAME)
                .provider(Provider.LOCAL)
                .role(Role.USER)
                .contact(PHONE_NUMBER)
                .build();
    }

    public static Member jeonghyeRolePatient() {
        return Member.builder()
                .username(JEONGHYE_USERNAME)
                .provider(Provider.LOCAL)
                .role(Role.USER)
                .contact(PHONE_NUMBER)
                .build();
    }
}
