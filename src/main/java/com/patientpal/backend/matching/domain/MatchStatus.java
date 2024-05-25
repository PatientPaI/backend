package com.patientpal.backend.matching.domain;

import lombok.Getter;

@Getter
public enum MatchStatus {
    PENDING("대기중"),
    APPROVED("승인됨"),
    REJECTED("거절됨"),
    COMPLETED("완료됨");

    private final String description;

    MatchStatus(String description) {
        this.description = description;
    }
}
