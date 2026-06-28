package com.smartschool.api.exception;

public class CustomException extends RuntimeException {

    private String errorCode;
    private int statusCode;

    public CustomException(String message) {
        super(message);
        this.errorCode = "CUSTOM_ERROR";
        this.statusCode = 400;
    }

    public CustomException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = 400;
    }

    public CustomException(String message, String errorCode, int statusCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    public CustomException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "CUSTOM_ERROR";
        this.statusCode = 400;
    }

    public CustomException(String message, String errorCode, int statusCode, Throwable cause) {
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