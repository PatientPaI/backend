package com.patientpal.backend.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AccessTokenResponse(@JsonProperty("refresh_token") String refreshToken) {
}
