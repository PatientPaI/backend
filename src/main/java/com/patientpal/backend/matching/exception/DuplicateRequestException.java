package com.patientpal.backend.matching.exception;

import com.patientpal.backend.common.exception.BusinessException;
import com.patientpal.backend.common.exception.ErrorCode;

public class DuplicateRequestException extends BusinessException {

    public DuplicateRequestException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }

    public DuplicateRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
