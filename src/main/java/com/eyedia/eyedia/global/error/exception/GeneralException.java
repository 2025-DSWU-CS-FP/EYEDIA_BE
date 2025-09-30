package com.eyedia.eyedia.global.error.exception;

import com.eyedia.eyedia.global.code.BaseErrorCode;
import com.eyedia.eyedia.global.code.ErrorReasonDTO;
import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {

  private final BaseErrorCode code;
  private final Object data;

  public GeneralException(BaseErrorCode code) {
    super(code.getReason().getMessage());
    this.code = code;
    this.data = null;
  }

  public GeneralException(BaseErrorCode code, Object data) {
    super(code.getReason().getMessage());
    this.code = code;
    this.data = data;
  }

  public ErrorReasonDTO getErrorReason() {
    return this.code.getReason();
  }

  public ErrorReasonDTO getErrorReasonHttpStatus() {
    return this.code.getReasonHttpStatus();
  }
}

