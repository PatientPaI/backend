package com.patientpal.backend.common.exception;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String detail;

    public BusinessException(ErrorCode errorCode, String detail) {
        super(errorCode.getMessage() + (detail == null ? "" : " (" + detail + ")"));
        this.errorCode = errorCode;
        this.detail = detail;
    }

    public BusinessException(ErrorCode errorCode) {
        this(errorCode, null);
    }

    public BusinessException(@NotNull String detail) {
        super(detail);
        this.errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
    }
}
