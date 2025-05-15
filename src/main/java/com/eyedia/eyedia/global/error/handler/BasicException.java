package com.eyedia.eyedia.global.error.handler;

import com.eyedia.eyedia.global.code.BaseErrorCode;
import com.eyedia.eyedia.global.error.exception.GeneralException;

public class BasicException extends GeneralException {
    public BasicException(BaseErrorCode code) {
        super(code);
    }
}