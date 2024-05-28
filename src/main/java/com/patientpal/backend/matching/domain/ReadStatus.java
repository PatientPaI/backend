package com.patientpal.backend.matching.domain;
import lombok.Getter;

@Getter
public enum ReadStatus {
    READ("열람"),
    UNREAD("미열람");

    private final String description;

    ReadStatus(String description) {
        this.description = description;
    }
}
