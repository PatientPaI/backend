package com.patientpal.backend.common.querydsl;

import com.patientpal.backend.member.domain.Gender;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileSearchCondition {

    //TODO
    //가까운 순 정렬 -> 경도 위도 따야하나
    //후기 많은 순
    //최신 순

    private String firstAddress;
    private String secondAddress;

    private Gender gender;

    private String name;

    private Integer experienceYearsGoe;

    // TODO 이후 프로필 주민번호 기반으로 나이 계산 후 추가
    // private Integer ageLoe;
}
