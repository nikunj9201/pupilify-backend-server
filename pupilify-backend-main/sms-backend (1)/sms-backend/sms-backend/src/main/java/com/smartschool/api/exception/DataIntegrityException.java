package com.smartschool.api.exception;

public class DataIntegrityException extends RuntimeException {

    private String errorCode;
    private int statusCode;

    public DataIntegrityException(String message) {
        super(message);
        this.errorCode = "DATA_INTEGRITY_ERROR";
        this.statusCode = 409;
    }

    public DataIntegrityException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = 409;
    }

    public DataIntegrityException(String message, String errorCode, int statusCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    public DataIntegrityException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "DATA_INTEGRITY_ERROR";
        this.statusCode = 409;
    }

    public DataIntegrityException(String message, String errorCode, int statusCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

