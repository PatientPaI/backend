package com.patientpal.backend.matching.domain;

import lombok.Getter;

@Getter
public enum MatchStatus {
    PENDING("대기중"),
    CANCELED("중도 취소"),
    ACCEPTED("수락됨"),
    COMPLETED("완료");

    private final String description;

    MatchStatus(String description) {
        this.description = description;
    }
}
