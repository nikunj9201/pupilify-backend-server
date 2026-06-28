package com.smartschool.api.exception;

public class ResourceNotFoundException extends RuntimeException {

    private String errorCode;
    private int statusCode;

    public ResourceNotFoundException(String message) {
        super(message);
        this.errorCode = "RESOURCE_NOT_FOUND";
        this.statusCode = 404;
    }

    public ResourceNotFoundException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = 404;
    }

    public ResourceNotFoundException(String message, String errorCode, int statusCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "RESOURCE_NOT_FOUND";
        this.statusCode = 404;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}