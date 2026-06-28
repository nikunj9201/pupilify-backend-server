package com.smartschool.api.exception;

public class ForbiddenException extends RuntimeException {

    private String errorCode;
    private int statusCode;

    public ForbiddenException(String message) {
        super(message);
        this.errorCode = "FORBIDDEN";
        this.statusCode = 403;
    }

    public ForbiddenException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = 403;
    }

    public ForbiddenException(String message, String errorCode, int statusCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "FORBIDDEN";
        this.statusCode = 403;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

