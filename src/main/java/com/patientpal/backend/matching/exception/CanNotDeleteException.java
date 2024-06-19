package com.patientpal.backend.matching.exception;

import com.patientpal.backend.common.exception.BusinessException;
import com.patientpal.backend.common.exception.ErrorCode;

public class CanNotDeleteException extends BusinessException {

    public CanNotDeleteException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }

    public CanNotDeleteException(ErrorCode errorCode) {
        super(errorCode);
    }
}
