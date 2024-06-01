package com.patientpal.backend.matching.exception;

import com.patientpal.backend.common.exception.BusinessException;
import com.patientpal.backend.common.exception.ErrorCode;

public class CanNotReadException extends BusinessException {

    public CanNotReadException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }

    public CanNotReadException(ErrorCode errorCode) {
        super(errorCode);
    }
}
