package com.patientpal.backend.notification.domain;

import lombok.Getter;

@Getter
public enum NotificationMessage {

    MATCH_NEW_REQUEST("새로운 매칭 요청이 있습니다.");

    private String message;

    NotificationMessage(String message) {
        this.message = message;
    }

}
