package com.patientpal.backend.matching.exception;

import com.patientpal.backend.common.exception.BusinessException;
import com.patientpal.backend.common.exception.ErrorCode;

public class CanNotAcceptException extends BusinessException {

    public CanNotAcceptException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }

    public CanNotAcceptException(ErrorCode errorCode) {
        super(errorCode);
    }
}
