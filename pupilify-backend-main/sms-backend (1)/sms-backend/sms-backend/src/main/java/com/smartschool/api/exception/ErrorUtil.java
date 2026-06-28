package com.smartschool.api.exception;

import java.util.ArrayList;
import java.util.List;

public class ErrorUtil {

    private ErrorUtil() {
        // Utility class, not meant to be instantiated
    }

    /**
     * Creates a list of field errors from a map
     */
    public static List<ApiError.FieldError> createFieldErrors(String field, String message) {
        List<ApiError.FieldError> errors = new ArrayList<>();
        errors.add(new ApiError.FieldError(field, message));
        return errors;
    }

    /**
     * Creates a list of field errors from multiple fields
     */
    public static List<ApiError.FieldError> createFieldErrors(List<String> fields, String message) {
        List<ApiError.FieldError> errors = new ArrayList<>();
        fields.forEach(field -> errors.add(new ApiError.FieldError(field, message)));
        return errors;
    }

    /**
     * Throws ResourceNotFoundException with custom error code
     */
    public static void throwResourceNotFound(String message, String errorCode) {
        throw new ResourceNotFoundException(message, errorCode);
    }

    /**
     * Throws ResourceNotFoundException
     */
    public static void throwResourceNotFound(String message) {
        throw new ResourceNotFoundException(message);
    }

    /**
     * Throws CustomException with error code and status code
     */
    public static void throwCustomException(String message, String errorCode, int statusCode) {
        throw new CustomException(message, errorCode, statusCode);
    }

    /**
     * Throws CustomException
     */
    public static void throwCustomException(String message, String errorCode) {
        throw new CustomException(message, errorCode);
    }

    /**
     * Throws ValidationException with field errors
     */
    public static void throwValidationException(String message, List<ApiError.FieldError> fieldErrors) {
        throw new ValidationException(message, fieldErrors);
    }

    /**
     * Throws ValidationException
     */
    public static void throwValidationException(String message) {
        throw new ValidationException(message);
    }

    /**
     * Throws UnauthorizedException
     */
    public static void throwUnauthorized(String message) {
        throw new UnauthorizedException(message);
    }

    /**
     * Throws ForbiddenException
     */
    public static void throwForbidden(String message) {
        throw new ForbiddenException(message);
    }

    /**
     * Throws DuplicateResourceException
     */
    public static void throwDuplicateResource(String message, String errorCode) {
        throw new DuplicateResourceException(message, errorCode);
    }

    /**
     * Throws DuplicateResourceException
     */
    public static void throwDuplicateResource(String message) {
        throw new DuplicateResourceException(message);
    }

    /**
     * Throws FileUploadException
     */
    public static void throwFileUploadException(String message, String errorCode, Throwable cause) {
        throw new FileUploadException(message, errorCode, 400, cause);
    }

    /**
     * Throws FileUploadException
     */
    public static void throwFileUploadException(String message) {
        throw new FileUploadException(message);
    }

    /**
     * Throws DatabaseException
     */
    public static void throwDatabaseException(String message, Throwable cause) {
        throw new DatabaseException(message, "DATABASE_ERROR", 500, cause);
    }

    /**
     * Throws DatabaseException
     */
    public static void throwDatabaseException(String message) {
        throw new DatabaseException(message);
    }

    /**
     * Throws DataIntegrityException
     */
    public static void throwDataIntegrityException(String message, String errorCode) {
        throw new DataIntegrityException(message, errorCode);
    }

    /**
     * Throws DataIntegrityException
     */
    public static void throwDataIntegrityException(String message) {
        throw new DataIntegrityException(message);
    }
}

