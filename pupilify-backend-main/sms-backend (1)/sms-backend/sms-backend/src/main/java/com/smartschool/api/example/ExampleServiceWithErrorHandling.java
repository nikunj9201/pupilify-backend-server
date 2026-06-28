package com.smartschool.api.example;

import com.smartschool.api.exception.*;
import com.smartschool.api.util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Example Service showing best practices for error handling
 * IMPORTANT: This is a REFERENCE IMPLEMENTATION FOR DOCUMENTATION ONLY
 *
 * This is NOT an actual service - it's meant to show patterns and examples
 * DO NOT use this class in your application
 *
 * To use this as a template:
 * 1. Create your own service class
 * 2. Copy the patterns and methods you need
 * 3. Replace String with your actual Entity/DTO classes
 * 4. Add @Service annotation when you create your actual service
 *
 * This example is for learning purposes - customize for your entities
 */
public class ExampleServiceWithErrorHandling {

    private static final Logger log = LoggerFactory.getLogger(ExampleServiceWithErrorHandling.class);

    // NOTE: In actual implementation, inject your real repository
    // @Autowired
    // private StudentRepository studentRepository;

    /**
     * Placeholder for documentation - replace with your actual repository
     */
    private IStudentRepository studentRepository = null;

    // ───────────────────────────────────────────────────────────────
    // Example 1: Get By ID with Null Check
    // ───────────────────────────────────────────────────────────────
    /**
     * Retrieve entity by ID
     * Throws ResourceNotFoundException if not found
     * NOTE: Replace String with your actual Entity class
     */
    public String getStudentById(Long studentId) {
        log.info("Fetching student with ID: {}", studentId);

        return studentRepository.findById(studentId)
            .map(s -> "Student: " + s)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format("Student not found with ID: %d", studentId),
                ErrorConstants.ERROR_STUDENT_NOT_FOUND
            ));
    }

    // ───────────────────────────────────────────────────────────────
    // Example 2: Create with Validation and Duplicate Check
    // ───────────────────────────────────────────────────────────────
    /**
     * Create a new entity
     * Validates input and checks for duplicates
     * NOTE: Replace this with your actual creation logic
     */
    public String createStudent(String studentData) {
        log.info("Creating new student");

        // Validate input
        validateStudentData(studentData);

        // Check for duplicates (adapt to your entity)
        if (studentRepository.existsByEmail("test@example.com")) {
            throw new DuplicateResourceException(
                "Student with this email already exists",
                ErrorConstants.ERROR_DUPLICATE_EMAIL
            );
        }

        try {
            // In your actual code:
            // Student student = new Student(dto);
            // return studentRepository.save(student);
            log.info("Student created successfully");
            return "Created";

        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while creating student", e);
            throw new DataIntegrityException(
                "Failed to create student due to data constraint violation",
                ErrorConstants.ERROR_DATA_CONSTRAINT_VIOLATION,
                409,
                e
            );
        } catch (Exception e) {
            log.error("Unexpected error while creating student", e);
            throw new DatabaseException(
                "Failed to create student",
                ErrorConstants.ERROR_DATABASE,
                500,
                e
            );
        }
    }

    // ───────────────────────────────────────────────────────────────
    // Example 3: Update with Conflict Detection
    // ───────────────────────────────────────────────────────────────
    /**
     * Update entity information
     * Checks for duplicate email and phone when updating
     */
    public String updateStudent(Long studentId, String studentData) {
        log.info("Updating student ID: {}", studentId);

        // Get existing entity (throws ResourceNotFoundException if not found)
        String student = getStudentById(studentId);

        // Validate input
        validateStudentData(studentData);

        // In your actual code: check for duplicate email excluding current record
        // if (!student.getEmail().equals(dto.getEmail()) && ...)

        try {
            log.info("Student updated successfully");
            return "Updated";

        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while updating student", e);
            throw new DataIntegrityException(
                "Failed to update student due to data constraint violation",
                ErrorConstants.ERROR_DATA_CONSTRAINT_VIOLATION,
                409,
                e
            );
        } catch (Exception e) {
            log.error("Unexpected error while updating student", e);
            throw new DatabaseException(
                "Failed to update student",
                ErrorConstants.ERROR_DATABASE,
                500,
                e
            );
        }
    }

    // ───────────────────────────────────────────────────────────────
    // Example 4: Delete with Soft Delete Pattern
    // ───────────────────────────────────────────────────────────────
    /**
     * Delete (inactivate) an entity
     */
    public void deleteStudent(Long studentId) {
        log.info("Deleting student ID: {}", studentId);

        // Get entity (throws ResourceNotFoundException if not found)
        String student = getStudentById(studentId);

        try {
            // In your actual code:
            // student.setIsActive(false);
            // studentRepository.save(student);
            log.info("Student {} deleted successfully", studentId);
        } catch (Exception e) {
            log.error("Error deleting student {}", studentId, e);
            throw new DatabaseException(
                "Failed to delete student",
                ErrorConstants.ERROR_DATABASE,
                500,
                e
            );
        }
    }

    // ───────────────────────────────────────────────────────────────
    // Example 5: File Upload Processing
    // ───────────────────────────────────────────────────────────────
    /**
     * Upload entity photo
     */
    public String uploadStudentPhoto(Long studentId, MultipartFile photo) {
        log.info("Processing photo upload for student ID: {}", studentId);

        // Verify entity exists (throws ResourceNotFoundException if not found)
        String student = getStudentById(studentId);

        try {
            // Validate file
            validatePhotoFile(photo);

            // Save file
            String fileName = "photo_" + studentId + ".jpg";

            // Update entity with photo URL
            // In your actual code:
            // student.setPhotoUrl(fileName);
            // studentRepository.save(student);

            log.info("Photo uploaded successfully for student {}", studentId);
            return fileName;

        } catch (FileUploadException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error uploading photo for student {}", studentId, e);
            throw new FileUploadException(
                "Failed to upload photo",
                "UPLOAD_ERROR",
                400,
                e
            );
        }
    }

    // ───────────────────────────────────────────────────────────────
    // Example 6: Bulk Operations with Transaction Handling
    // ───────────────────────────────────────────────────────────────
    /**
     * Create multiple entities in bulk
     * If any record fails, entire transaction is rolled back
     */
    @Transactional(rollbackFor = Exception.class)
    public BulkUploadResponse bulkCreateStudents(List<String> studentDataList) {
        log.info("Bulk creating {} students", studentDataList.size());

        BulkUploadResponse response = new BulkUploadResponse();
        int successCount = 0;
        int failureCount = 0;

        for (int i = 0; i < studentDataList.size(); i++) {
            String data = studentDataList.get(i);
            try {
                validateStudentData(data);
                // In actual code: createStudent(dto);
                successCount++;
            } catch (DuplicateResourceException e) {
                failureCount++;
                response.addFailedRecord(i, data, "DUPLICATE: " + e.getMessage());
                log.warn("Duplicate record at row {}: {}", i, e.getMessage());
            } catch (ValidationException e) {
                failureCount++;
                response.addFailedRecord(i, data, "VALIDATION: " + e.getMessage());
                log.warn("Validation error at row {}: {}", i, e.getMessage());
            } catch (Exception e) {
                failureCount++;
                response.addFailedRecord(i, data, "ERROR: " + e.getMessage());
                log.error("Error at row {}", i, e);
            }
        }

        response.setSuccessCount(successCount);
        response.setFailureCount(failureCount);

        log.info("Bulk creation completed: {} successful, {} failed", successCount, failureCount);
        return response;
    }

    // ───────────────────────────────────────────────────────────────
    // Example 7: Complex Business Logic with Multiple Validations
    // ───────────────────────────────────────────────────────────────
    /**
     * Promote entity to next level
     * Complex operation with multiple validation checks
     */
    public String promoteStudent(Long studentId, Long newClassId) {
        log.info("Promoting student {} to class {}", studentId, newClassId);

        // Get entity
        String student = getStudentById(studentId);

        // Check if entity is active (adapt to your entity)
        // if (!student.isActive()) { ... }

        // Check if entity is eligible for promotion
        if (!isEligibleForPromotion(studentId)) {
            throw new CustomException(
                "Student does not meet promotion criteria (attendance or marks)",
                ErrorConstants.ERROR_OPERATION_NOT_ALLOWED,
                422
            );
        }

        // Verify new class exists
        if (newClassId == null || newClassId <= 0) {
            throw new ResourceNotFoundException(
                "Target class not found",
                ErrorConstants.ERROR_CLASS_NOT_FOUND
            );
        }

        try {
            // In your actual code:
            // student.setCurrentClass(newClass);
            // student.setPromotedDate(LocalDateTime.now());
            // return studentRepository.save(student);
            log.info("Student promoted successfully");
            return "Promoted";

        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity error during promotion", e);
            throw new DataIntegrityException(
                "Failed to promote student due to data constraint",
                ErrorConstants.ERROR_DATA_CONSTRAINT_VIOLATION,
                409,
                e
            );
        } catch (Exception e) {
            log.error("Error promoting student", e);
            throw new DatabaseException(
                "Failed to promote student",
                "PROMOTION_FAILED",
                500,
                e
            );
        }
    }

    // ───────────────────────────────────────────────────────────────
    // Example 8: Query with Validation
    // ───────────────────────────────────────────────────────────────
    /**
     * Get entities with filters
     * Validates all filter parameters
     */
    public List<String> getFilteredStudents(Long schoolId, Long classId, Long sectionId) {
        log.info("Fetching filtered students for school: {}", schoolId);

        // Validate school ID
        if (schoolId == null || schoolId <= 0) {
            throw new ValidationException(
                "School ID is required and must be positive",
                ErrorUtil.createFieldErrors("schoolId", "Invalid school ID")
            );
        }

        // Verify school exists (adapt to your actual checks)
        // if (!schoolService.existsById(schoolId)) { ... }

        // Validate class ID if provided
        if (classId != null && classId < 0) {
            throw new ResourceNotFoundException(
                "Class not found with ID: " + classId,
                ErrorConstants.ERROR_CLASS_NOT_FOUND
            );
        }

        // Validate section ID if provided
        if (sectionId != null && sectionId < 0) {
            throw new ResourceNotFoundException(
                "Section not found with ID: " + sectionId,
                ErrorConstants.ERROR_SECTION_NOT_FOUND
            );
        }

        try {
            // In your actual code: return studentRepository.findByFilters(...)
            log.info("Filtered students retrieved successfully");
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("Error fetching filtered students", e);
            throw new DatabaseException(
                "Failed to fetch students",
                ErrorConstants.ERROR_DATABASE,
                500,
                e
            );
        }
    }

    // ───────────────────────────────────────────────────────────────
    // Validation Helper Methods
    // ───────────────────────────────────────────────────────────────

    /**
     * Validate entity data
     */
    private void validateStudentData(String data) {
        log.debug("Validating student data");

        List<ApiError.FieldError> fieldErrors = new ArrayList<>();

        // Add your actual validation logic here
        if (data == null || data.trim().isEmpty()) {
            fieldErrors.add(new ApiError.FieldError("data", "Data is required"));
        }

        // Throw validation exception if there are errors
        if (!fieldErrors.isEmpty()) {
            throw new ValidationException("Input validation failed", fieldErrors);
        }
    }

    /**
     * Validate photo file
     */
    private void validatePhotoFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileUploadException("File cannot be empty");
        }

        String contentType = file.getContentType();
        if (contentType == null ||
            (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
            throw new FileUploadException(
                "Invalid file format. Only JPEG and PNG are allowed",
                ErrorConstants.ERROR_INVALID_FILE_FORMAT
            );
        }

        long maxFileSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxFileSize) {
            throw new FileUploadException(
                "File size exceeds 5MB limit",
                ErrorConstants.ERROR_FILE_SIZE_EXCEEDED,
                413
            );
        }
    }

    /**
     * Check if entity is eligible for promotion
     */
    private boolean isEligibleForPromotion(Long studentId) {
        // In your actual code:
        // double attendance = studentRepository.getAttendancePercentage(studentId);
        // if (attendance < 75.0) return false;
        // double marks = studentRepository.getOverallMarks(studentId);
        // return marks >= 40.0;

        return true; // Default for example
    }

    // ───────────────────────────────────────────────────────────────
    // Helper Classes
    // ───────────────────────────────────────────────────────────────

    public static class BulkUploadResponse {
        private int successCount;
        private int failureCount;
        private List<FailedRecord> failedRecords = new ArrayList<>();

        public void addFailedRecord(int rowIndex, String data, String error) {
            failedRecords.add(new FailedRecord(rowIndex, data, error));
        }

        // Getters and setters
        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }

        public int getFailureCount() { return failureCount; }
        public void setFailureCount(int failureCount) { this.failureCount = failureCount; }

        public List<FailedRecord> getFailedRecords() { return failedRecords; }
    }

    public static class FailedRecord {
        private int rowIndex;
        private String data;
        private String error;

        public FailedRecord(int rowIndex, String data, String error) {
            this.rowIndex = rowIndex;
            this.data = data;
            this.error = error;
        }

        // Getters
        public int getRowIndex() { return rowIndex; }
        public String getData() { return data; }
        public String getError() { return error; }
    }

    // ───────────────────────────────────────────────────────────────
    // Repository and Service Interfaces (Placeholder)
    // ───────────────────────────────────────────────────────────────

    @Repository
    interface IStudentRepository extends JpaRepository<String, Long> {
        boolean existsByEmail(String email);
        boolean existsByPhone(String phone);
    }
}

