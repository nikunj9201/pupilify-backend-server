package com.smartschool.api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    private int status;
    private String message;
    private String path;
    private LocalDateTime timestamp;
    private String errorCode;
    private List<FieldError> fieldErrors;
    private String exception;

    public ApiError() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiError(int status, String message, String path) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    public ApiError(int status, String message, String path, String errorCode) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
    }

    public ApiError(int status, String message, String path, String errorCode, String exception) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.errorCode = errorCode;
        this.exception = exception;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(List<FieldError> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FieldError {
        private String field;
        private String message;

        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}