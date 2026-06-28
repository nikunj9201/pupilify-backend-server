package com.smartschool.api.exception;

public class FileUploadException extends RuntimeException {

    private String errorCode;
    private int statusCode;

    public FileUploadException(String message) {
        super(message);
        this.errorCode = "FILE_UPLOAD_ERROR";
        this.statusCode = 400;
    }

    public FileUploadException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = 400;
    }

    public FileUploadException(String message, String errorCode, int statusCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "FILE_UPLOAD_ERROR";
        this.statusCode = 400;
    }

    public FileUploadException(String message, String errorCode, int statusCode, Throwable cause) {
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

