package com.patientpal.backend.matching.domain;

import lombok.Getter;

@Getter
public enum MatchStatus {
    //신청 보낸 사람
    COMPLETED("신청 완료"),

    //신청 받은 사람
    PENDING("대기중"),

    //공통
    CANCELED("취소 완료"),
    ACCEPTED("수락됨");

    private final String description;

    MatchStatus(String description) {
        this.description = description;
    }
}
