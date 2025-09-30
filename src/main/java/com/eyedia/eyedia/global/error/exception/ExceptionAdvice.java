package com.eyedia.eyedia.global.error.exception;

import com.eyedia.eyedia.global.ApiResponse;
import com.eyedia.eyedia.global.code.ErrorReasonDTO;
import com.eyedia.eyedia.global.error.status.ErrorStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    /**
     * Validation 실패 시 처리 (예: @Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        String errorCode = ErrorStatus._BAD_REQUEST.getCode(); // 기본값
        String errorMessage = ErrorStatus._BAD_REQUEST.getMessage();
        ErrorStatus resolved = null; // 처음에 생긴 예외를 처리

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            String msg = fieldError.getDefaultMessage();

            ErrorStatus status = ErrorStatus.fromCode(msg); // ← name으로 매핑
            if (status != null) {
                if(resolved == null) {
                    errorCode = status.getCode();
                    errorMessage = status.getMessage();
                    resolved = status;
                }
                errors.put(fieldError.getField(), status.getMessage());
            } else {
                errors.put(fieldError.getField(), msg); // 일반 메시지
            }
        }

        for (ObjectError globalError : e.getBindingResult().getGlobalErrors()) {
            String msg = globalError.getDefaultMessage();

            ErrorStatus status = ErrorStatus.fromCode(msg);
            if (status != null) {
                errorCode = status.getCode();
                errorMessage = status.getMessage();
                errors.put(globalError.getObjectName(), status.getMessage());
            } else {
                errors.put(globalError.getObjectName(), msg);
            }
        }

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.onFailure(errorCode, errorMessage, errors));
    }

    /**
     * javax.validation.ConstraintViolationException 처리 (예: @Validated + @RequestParam)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<?> handleConstraintViolation(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(v -> v.getMessage())
                .findFirst()
                .orElse("Invalid request");
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.onFailure(ErrorStatus._BAD_REQUEST.getCode(), message, null));
    }

    /**
     * 비즈니스 예외 처리용
     */
    @ExceptionHandler(GeneralException.class)
    protected ResponseEntity<?> handleGeneralException(GeneralException e) {
        ErrorReasonDTO reason = e.getErrorReasonHttpStatus();
        Object data = e.getData();

        return ResponseEntity
                .status(reason.getHttpStatus())
                .body(ApiResponse.onFailure(
                        reason.getCode(),
                        reason.getMessage(),
                        data
                ));
    }

    /**
     * 그 외 예상 못한 예외 처리
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<?> handleUnexpectedException(Exception e, HttpServletRequest request) {
        log.error("Unexpected error occurred at {} {}", request.getMethod(), request.getRequestURI(), e);
        return ResponseEntity
                .internalServerError()
                .body(ApiResponse.onFailure(ErrorStatus._INTERNAL_SERVER_ERROR.getCode(), "Internal Server Error", null));
    }
}
