package com.patientpal.backend.matching.exception;

import com.patientpal.backend.common.exception.BusinessException;
import com.patientpal.backend.common.exception.ErrorCode;

public class CanNotRequestException extends BusinessException {

    public CanNotRequestException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }

    public CanNotRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
