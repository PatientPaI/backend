package com.patientpal.backend.common.querydsl;

import com.patientpal.backend.member.domain.Gender;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileSearchCondition {

    private String addr;

    private Gender gender;

    private String name;

    private Integer experienceYearsGoe;

    private String keyword;

    private Integer ageLoe;
}
