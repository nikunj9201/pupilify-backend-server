package com.smartschool.api.exception;

public class UnauthorizedException extends RuntimeException {

    private String errorCode;
    private int statusCode;

    public UnauthorizedException(String message) {
        super(message);
        this.errorCode = "UNAUTHORIZED";
        this.statusCode = 401;
    }

    public UnauthorizedException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = 401;
    }

    public UnauthorizedException(String message, String errorCode, int statusCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "UNAUTHORIZED";
        this.statusCode = 401;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

