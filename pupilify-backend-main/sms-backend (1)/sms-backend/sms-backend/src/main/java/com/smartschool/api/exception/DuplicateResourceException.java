package com.smartschool.api.exception;

public class DuplicateResourceException extends RuntimeException {

    private String errorCode;
    private int statusCode;

    public DuplicateResourceException(String message) {
        super(message);
        this.errorCode = "DUPLICATE_RESOURCE";
        this.statusCode = 409;
    }

    public DuplicateResourceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = 409;
    }

    public DuplicateResourceException(String message, String errorCode, int statusCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "DUPLICATE_RESOURCE";
        this.statusCode = 409;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

