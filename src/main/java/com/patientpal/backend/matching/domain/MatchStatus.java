package com.patientpal.backend.matching.domain;

public enum MatchStatus {
    PENDING,
    CANCELED,
    ACCEPTED,
    IN_PROGRESS_CHAT,
    IN_PROGRESS_CONTRACT,
    COMPLETED;
}
