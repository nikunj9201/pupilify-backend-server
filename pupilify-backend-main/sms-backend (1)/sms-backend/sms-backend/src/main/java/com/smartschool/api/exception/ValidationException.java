package com.smartschool.api.exception;

import java.util.List;

public class    ValidationException extends RuntimeException {

    private String errorCode;
    private List<ApiError.FieldError> fieldErrors;

    public ValidationException(String message) {
        super(message);
        this.errorCode = "VALIDATION_ERROR";
    }

    public ValidationException(String message, List<ApiError.FieldError> fieldErrors) {
        super(message);
        this.errorCode = "VALIDATION_ERROR";
        this.fieldErrors = fieldErrors;
    }

    public ValidationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ValidationException(String message, String errorCode, List<ApiError.FieldError> fieldErrors) {
        super(message);
        this.errorCode = errorCode;
        this.fieldErrors = fieldErrors;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public List<ApiError.FieldError> getFieldErrors() {
        return fieldErrors;
    }
}

