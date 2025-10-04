package com.yoosang.oauth2_practice.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Base class for all domain-specific exceptions.
 */
public abstract class BaseException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;

    protected BaseException(HttpStatus httpStatus, String errorCode, String errorMessage) {
        super(errorMessage);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
