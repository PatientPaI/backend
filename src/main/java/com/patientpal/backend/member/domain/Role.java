package com.patientpal.backend.member.domain;

import lombok.Getter;

@Getter
public enum Role {
    USER("이용자"), CAREGIVER("간병인"), ADMIN("관리자");

    private final String description;

    Role(String description) {
        this.description = description;
    }
}
