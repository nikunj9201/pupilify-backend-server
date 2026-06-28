package com.smartschool.api.exception;

public class DatabaseException extends RuntimeException {

    private String errorCode;
    private int statusCode;

    public DatabaseException(String message) {
        super(message);
        this.errorCode = "DATABASE_ERROR";
        this.statusCode = 500;
    }

    public DatabaseException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = 500;
    }

    public DatabaseException(String message, String errorCode, int statusCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "DATABASE_ERROR";
        this.statusCode = 500;
    }

    public DatabaseException(String message, String errorCode, int statusCode, Throwable cause) {
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

