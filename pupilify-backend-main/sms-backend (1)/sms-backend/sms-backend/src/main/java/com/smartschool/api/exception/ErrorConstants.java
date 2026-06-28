package com.smartschool.api.exception;

public class ErrorConstants {

    private ErrorConstants() {
        // Constants class, not meant to be instantiated
    }

    // HTTP Status Codes
    public static final int STATUS_BAD_REQUEST = 400;
    public static final int STATUS_UNAUTHORIZED = 401;
    public static final int STATUS_FORBIDDEN = 403;
    public static final int STATUS_NOT_FOUND = 404;
    public static final int STATUS_CONFLICT = 409;
    public static final int STATUS_PAYLOAD_TOO_LARGE = 413;
    public static final int STATUS_INTERNAL_SERVER_ERROR = 500;
    public static final int STATUS_SERVICE_UNAVAILABLE = 503;

    // Resource Not Found Errors
    public static final String ERROR_RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND";
    public static final String ERROR_STUDENT_NOT_FOUND = "STUDENT_NOT_FOUND";
    public static final String ERROR_TEACHER_NOT_FOUND = "TEACHER_NOT_FOUND";
    public static final String ERROR_SCHOOL_NOT_FOUND = "SCHOOL_NOT_FOUND";
    public static final String ERROR_CLASS_NOT_FOUND = "CLASS_NOT_FOUND";
    public static final String ERROR_SECTION_NOT_FOUND = "SECTION_NOT_FOUND";
    public static final String ERROR_ACADEMIC_YEAR_NOT_FOUND = "ACADEMIC_YEAR_NOT_FOUND";
    public static final String ERROR_SUBJECT_NOT_FOUND = "SUBJECT_NOT_FOUND";

    // Validation Errors
    public static final String ERROR_VALIDATION_FAILED = "VALIDATION_ERROR";
    public static final String ERROR_INVALID_INPUT = "INVALID_INPUT";
    public static final String ERROR_INVALID_EMAIL = "INVALID_EMAIL";
    public static final String ERROR_INVALID_PHONE = "INVALID_PHONE";
    public static final String ERROR_INVALID_DATE = "INVALID_DATE";

    // Authentication & Authorization Errors
    public static final String ERROR_UNAUTHORIZED = "UNAUTHORIZED";
    public static final String ERROR_FORBIDDEN = "FORBIDDEN";
    public static final String ERROR_INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
    public static final String ERROR_TOKEN_EXPIRED = "TOKEN_EXPIRED";
    public static final String ERROR_TOKEN_INVALID = "TOKEN_INVALID";

    // Duplicate Resource Errors
    public static final String ERROR_DUPLICATE_RESOURCE = "DUPLICATE_RESOURCE";
    public static final String ERROR_DUPLICATE_EMAIL = "DUPLICATE_EMAIL";
    public static final String ERROR_DUPLICATE_PHONE = "DUPLICATE_PHONE";
    public static final String ERROR_DUPLICATE_STUDENT = "DUPLICATE_STUDENT";

    // Data Integrity Errors
    public static final String ERROR_DATA_INTEGRITY = "DATA_INTEGRITY_ERROR";
    public static final String ERROR_DATA_CONSTRAINT_VIOLATION = "DATA_CONSTRAINT_VIOLATION";
    public static final String ERROR_FOREIGN_KEY_VIOLATION = "FOREIGN_KEY_VIOLATION";

    // File Upload Errors
    public static final String ERROR_FILE_UPLOAD = "FILE_UPLOAD_ERROR";
    public static final String ERROR_INVALID_FILE_FORMAT = "INVALID_FILE_FORMAT";
    public static final String ERROR_FILE_SIZE_EXCEEDED = "FILE_SIZE_EXCEEDED";
    public static final String ERROR_FILE_NOT_FOUND = "FILE_NOT_FOUND";

    // Database Errors
    public static final String ERROR_DATABASE = "DATABASE_ERROR";
    public static final String ERROR_DATABASE_CONNECTION = "DATABASE_CONNECTION_ERROR";

    // Business Logic Errors
    public static final String ERROR_OPERATION_NOT_ALLOWED = "OPERATION_NOT_ALLOWED";
    public static final String ERROR_INVALID_STATE = "INVALID_STATE";
    public static final String ERROR_INSUFFICIENT_PERMISSIONS = "INSUFFICIENT_PERMISSIONS";

    // Server Errors
    public static final String ERROR_INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
    public static final String ERROR_SERVICE_UNAVAILABLE = "SERVICE_UNAVAILABLE";
    public static final String ERROR_ENDPOINT_NOT_FOUND = "ENDPOINT_NOT_FOUND";
    public static final String ERROR_TYPE_MISMATCH = "TYPE_MISMATCH";

    // Error Messages
    public static final String MSG_RESOURCE_NOT_FOUND = "Requested resource not found";
    public static final String MSG_STUDENT_NOT_FOUND = "Student not found";
    public static final String MSG_TEACHER_NOT_FOUND = "Teacher not found";
    public static final String MSG_SCHOOL_NOT_FOUND = "School not found";
    public static final String MSG_CLASS_NOT_FOUND = "Class not found";
    public static final String MSG_UNAUTHORIZED = "Unauthorized access";
    public static final String MSG_FORBIDDEN = "Forbidden access";
    public static final String MSG_VALIDATION_FAILED = "Validation failed";
    public static final String MSG_INVALID_INPUT = "Invalid input provided";
    public static final String MSG_DUPLICATE_RESOURCE = "Resource already exists";
    public static final String MSG_INTERNAL_ERROR = "An unexpected error occurred";
    public static final String MSG_FILE_UPLOAD_ERROR = "File upload failed";
    public static final String MSG_DATABASE_ERROR = "Database operation failed";
}

