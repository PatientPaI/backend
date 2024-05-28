package com.patientpal.backend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S_001", "서버에 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "S_002", "잘못된 요청 값입니다."),
    TOKEN_HASHING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S_003", "서버에 오류가 발생했습니다."),

    AUTHORIZATION_FAILED(HttpStatus.FORBIDDEN, "AR_001", "권한이 없습니다."),

    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "AU_001", "이메일 또는 비밀번호가 일치하지 않습니다."),

    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "V_001", "유효하지 않은 토큰입니다."),

    MEMBER_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "M_001", "이미 가입된 계정이 존재합니다."),
    MEMBER_NOT_EXIST(HttpStatus.BAD_REQUEST, "M_002", "해당 멤버는 존재하지 않습니다."),

    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "P_001", "해당 게시글을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(final HttpStatus status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
