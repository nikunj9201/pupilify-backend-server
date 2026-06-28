package com.smartschool.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {
        log.error("Resource not found: {}", ex.getMessage());

        ApiError error = new ApiError(
                ex.getStatusCode(),
                ex.getMessage(),
                request.getRequestURI(),
                ex.getErrorCode()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiError> handleCustomException(
            CustomException ex,
            HttpServletRequest request) {
        log.error("Custom exception: {}", ex.getMessage(), ex);

        ApiError error = new ApiError(
                ex.getStatusCode(),
                ex.getMessage(),
                request.getRequestURI(),
                ex.getErrorCode()
        );
        return new ResponseEntity<>(error, HttpStatus.valueOf(ex.getStatusCode()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorizedException(
            UnauthorizedException ex,
            HttpServletRequest request) {
        log.warn("Unauthorized access attempt: {}", ex.getMessage());

        ApiError error = new ApiError(
                ex.getStatusCode(),
                ex.getMessage(),
                request.getRequestURI(),
                ex.getErrorCode()
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiError> handleForbiddenException(
            ForbiddenException ex,
            HttpServletRequest request) {
        log.warn("Forbidden access: {}", ex.getMessage());

        ApiError error = new ApiError(
                ex.getStatusCode(),
                ex.getMessage(),
                request.getRequestURI(),
                ex.getErrorCode()
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleDuplicateResourceException(
            DuplicateResourceException ex,
            HttpServletRequest request) {
        log.error("Duplicate resource: {}", ex.getMessage());

        ApiError error = new ApiError(
                ex.getStatusCode(),
                ex.getMessage(),
                request.getRequestURI(),
                ex.getErrorCode()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidationException(
            ValidationException ex,
            HttpServletRequest request) {
        log.error("Validation error: {}", ex.getMessage());

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getRequestURI(),
                ex.getErrorCode()
        );
        error.setFieldErrors(ex.getFieldErrors());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ApiError> handleFileUploadException(
            FileUploadException ex,
            HttpServletRequest request) {
        log.error("File upload error: {}", ex.getMessage());

        ApiError error = new ApiError(
                ex.getStatusCode(),
                ex.getMessage(),
                request.getRequestURI(),
                ex.getErrorCode()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityException.class)
    public ResponseEntity<ApiError> handleDataIntegrityException(
            DataIntegrityException ex,
            HttpServletRequest request) {
        log.error("Data integrity error: {}", ex.getMessage());

        ApiError error = new ApiError(
                ex.getStatusCode(),
                ex.getMessage(),
                request.getRequestURI(),
                ex.getErrorCode()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ApiError> handleDatabaseException(
            DatabaseException ex,
            HttpServletRequest request) {
        log.error("Database error: {}", ex.getMessage(), ex);

        ApiError error = new ApiError(
                ex.getStatusCode(),
                "Database operation failed. Please try again later.",
                request.getRequestURI(),
                ex.getErrorCode()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        log.error("Validation failed for request");

        List<ApiError.FieldError> fieldErrors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            fieldErrors.add(new ApiError.FieldError(error.getField(), error.getDefaultMessage()))
        );

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                request.getRequestURI(),
                "VALIDATION_ERROR"
        );
        error.setFieldErrors(fieldErrors);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        log.error("Type mismatch: {}", ex.getMessage());

        String message = String.format("Invalid value for parameter '%s': %s",
                ex.getName(), ex.getValue());

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                message,
                request.getRequestURI(),
                "TYPE_MISMATCH"
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {
        log.error("Database constraint violation: {}", ex.getMessage());

        ApiError error = new ApiError(
                HttpStatus.CONFLICT.value(),
                "Data integrity violation. Check your input and try again.",
                request.getRequestURI(),
                "DATA_INTEGRITY_VIOLATION"
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiError> handleMaxUploadSizeExceeded(
            MaxUploadSizeExceededException ex,
            HttpServletRequest request) {
        log.error("File size exceeded: {}", ex.getMessage());

        ApiError error = new ApiError(
                HttpStatus.PAYLOAD_TOO_LARGE.value(),
                "File size exceeds maximum limit",
                request.getRequestURI(),
                "FILE_SIZE_EXCEEDED"
        );
        return new ResponseEntity<>(error, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiError> handleNoHandlerFoundException(
            NoHandlerFoundException ex,
            HttpServletRequest request) {
        log.error("Resource not found: {} {}", ex.getHttpMethod(), ex.getRequestURL());

        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "Endpoint not found",
                request.getRequestURI(),
                "ENDPOINT_NOT_FOUND"
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<ApiError> handleHttpMessageConversionException(
            HttpMessageConversionException ex,
            HttpServletRequest request) {
        log.error("JSON parsing error: {}", ex.getMessage(), ex);

        String detailedMessage = "Invalid JSON format in request body";
        if (ex.getCause() != null) {
            detailedMessage += ": " + ex.getCause().getMessage();
        }

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                detailedMessage,
                request.getRequestURI(),
                "JSON_PARSE_ERROR"
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGlobalException(
            Exception ex,
            HttpServletRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);

        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred. Please try again later.",
                request.getRequestURI(),
                "INTERNAL_SERVER_ERROR",
                ex.getClass().getName()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

