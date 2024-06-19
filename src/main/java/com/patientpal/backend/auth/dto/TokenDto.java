package com.patientpal.backend.auth.dto;

public record TokenDto(String accessToken, String refreshToken) {
}
