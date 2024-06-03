package com.patientpal.backend.matching.exception;

import com.patientpal.backend.common.exception.BusinessException;
import com.patientpal.backend.common.exception.ErrorCode;

public class NotCompleteProfileException extends BusinessException {

    public NotCompleteProfileException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }

    public NotCompleteProfileException(ErrorCode errorCode) {
        super(errorCode);
    }
}
