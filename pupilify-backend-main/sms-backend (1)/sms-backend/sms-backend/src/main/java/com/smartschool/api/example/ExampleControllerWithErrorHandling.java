package com.smartschool.api.example;

import com.smartschool.api.exception.*;
import com.smartschool.api.util.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.ArrayList;

/**
 * Example Controller showing best practices for error handling
 * IMPORTANT: This is a REFERENCE IMPLEMENTATION FOR DOCUMENTATION ONLY
 *
 * This is NOT an actual controller - it's meant to show patterns and examples
 * DO NOT use this class in your application
 *
 * To use this as a template:
 * 1. Create your own controller class
 * 2. Copy the patterns and methods you need
 * 3. Replace String with your actual Entity/DTO classes
 * 4. Inject your actual service classes
 * 5. Add @RestController and @RequestMapping annotations
 *
 * This example is for learning purposes - customize for your specific entities
 */
public class ExampleControllerWithErrorHandling {

    private static final Logger log = LoggerFactory.getLogger(ExampleControllerWithErrorHandling.class);

    // NOTE: In actual implementation, inject your real service
    // @Autowired
    // private StudentService studentService;

    /**
     * Placeholder for documentation - replace with your actual service
     */
    private IStudentService studentService = null;

    // ───────────────────────────────────────────────────────────────
    // Example 1: Get By ID with Not Found Handling
    // ───────────────────────────────────────────────────────────────
    /**
     * Retrieve entity by ID
     * Throws ResourceNotFoundException if not found
     *
     * HTTP METHOD: GET /api/students/{id}
     *
     * Example implementation:
     * @GetMapping("/{id}")
     * public ResponseEntity<ApiResponse<String>> getStudentById(@PathVariable Long id) {
     */
    public ResponseEntity<ApiResponse<String>> getStudentById(Long id) {
        log.info("Fetching student with ID: {}", id);

        String student = studentService.getStudentById(id);
        // Service throws ResourceNotFoundException if not found

        return ResponseEntity.ok(
            ApiResponse.success("Student retrieved successfully", student)
        );
    }

    // ───────────────────────────────────────────────────────────────
    // Example 2: Create with Validation & Duplicate Check
    // ───────────────────────────────────────────────────────────────
    /**
     * Create a new entity
     * Validates input and checks for duplicates
     *
     * HTTP METHOD: POST /api/students
     *
     * Example implementation:
     * @PostMapping
     * public ResponseEntity<ApiResponse<String>> createStudent(@RequestBody String dto) {
     */
    public ResponseEntity<ApiResponse<String>> createStudent(String dto) {
        log.info("Creating new student");

        String student = studentService.createStudent(dto);
        // Service handles validation and duplicate checking

        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.created("Student created successfully", student)
        );
    }

    // ───────────────────────────────────────────────────────────────
    // Example 3: Update with Conflict Check
    // ───────────────────────────────────────────────────────────────
    /**
     * Update entity information
     * Checks for duplicate email when updating
     *
     * HTTP METHOD: PUT /api/students/{id}
     *
     * Example implementation:
     * @PutMapping("/{id}")
     * public ResponseEntity<ApiResponse<String>> updateStudent(
     *        @PathVariable Long id,
     *        @RequestBody String dto) {
     */
    public ResponseEntity<ApiResponse<String>> updateStudent(Long id, String dto) {
        log.info("Updating student ID: {}", id);

        String student = studentService.updateStudent(id, dto);
        // Service throws ResourceNotFoundException if not found
        // Service throws DuplicateResourceException if duplicate

        return ResponseEntity.ok(
            ApiResponse.success("Student updated successfully", student)
        );
    }

    // ───────────────────────────────────────────────────────────────
    // Example 4: Delete with Soft Delete
    // ───────────────────────────────────────────────────────────────
    /**
     * Delete (inactivate) an entity
     *
     * HTTP METHOD: DELETE /api/students/{id}
     *
     * Example implementation:
     * @DeleteMapping("/{id}")
     * public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long id) {
     */
    public ResponseEntity<ApiResponse<Void>> deleteStudent(Long id) {
        log.info("Deleting student ID: {}", id);

        studentService.deleteStudent(id);
        // Service throws ResourceNotFoundException if not found

        return ResponseEntity.ok(
            ApiResponse.success("Student deleted successfully")
        );
    }

    // ───────────────────────────────────────────────────────────────
    // Example 5: File Upload with Comprehensive Error Handling
    // ───────────────────────────────────────────────────────────────
    /**
     * Upload file
     * Validates file type, size, and empty files
     *
     * HTTP METHOD: POST /api/students/{id}/upload
     *
     * Example implementation:
     * @PostMapping("/{id}/upload")
     * public ResponseEntity<ApiResponse<String>> uploadStudentPhoto(
     *        @PathVariable Long id,
     *        @RequestParam("file") MultipartFile photo) {
     */
    public ResponseEntity<ApiResponse<String>> uploadStudentPhoto(Long id, MultipartFile photo) {
        log.info("Uploading file for student ID: {}", id);

        // Verify entity exists
        studentService.getStudentById(id);

        try {
            // Validate file is not empty
            if (photo.isEmpty()) {
                throw new FileUploadException(
                    "File cannot be empty",
                    ErrorConstants.ERROR_FILE_UPLOAD
                );
            }

            // Validate file type
            String contentType = photo.getContentType();
            if (contentType == null ||
                (!contentType.equals("image/jpeg") &&
                 !contentType.equals("image/png"))) {
                throw new FileUploadException(
                    "Invalid file format. Only JPEG and PNG are allowed",
                    ErrorConstants.ERROR_INVALID_FILE_FORMAT
                );
            }

            // Validate file size (max 5MB)
            long maxFileSize = 5 * 1024 * 1024;
            if (photo.getSize() > maxFileSize) {
                throw new FileUploadException(
                    "File size exceeds 5MB limit",
                    ErrorConstants.ERROR_FILE_SIZE_EXCEEDED,
                    413
                );
            }

            // Upload the file
            String photoUrl = studentService.uploadStudentPhoto(id, photo);

            return ResponseEntity.ok(
                ApiResponse.success("Photo uploaded successfully", photoUrl)
            );

        } catch (FileUploadException e) {
            // Re-throw FileUploadException (will be handled by GlobalExceptionHandler)
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during photo upload for student {}", id, e);
            throw new FileUploadException(
                "Photo upload failed due to an unexpected error",
                "UPLOAD_ERROR",
                400,
                e
            );
        }
    }

    // ───────────────────────────────────────────────────────────────
    // Example 6: Bulk Upload with Transaction Handling
    // ───────────────────────────────────────────────────────────────
    /**
     * Bulk upload from CSV/Excel
     * Returns detailed error information for failed records
     *
     * HTTP METHOD: POST /api/students/bulk-upload/{schoolId}
     *
     * Example implementation:
     * @PostMapping("/bulk-upload/{schoolId}")
     * public ResponseEntity<ApiResponse<Object>> bulkUploadStudents(
     *        @PathVariable Long schoolId,
     *        @RequestParam MultipartFile file) {
     */
    public ResponseEntity<ApiResponse<Object>> bulkUploadStudents(Long schoolId, MultipartFile file) {
        log.info("Bulk uploading students for school ID: {}", schoolId);

        try {
            if (file.isEmpty()) {
                throw new FileUploadException("File cannot be empty");
            }

            if (!file.getOriginalFilename().endsWith(".csv")) {
                throw new FileUploadException(
                    "Only CSV files are supported",
                    ErrorConstants.ERROR_INVALID_FILE_FORMAT
                );
            }

            Object result = studentService.bulkUploadStudents(schoolId, file);

            return ResponseEntity.ok(
                ApiResponse.success("Bulk upload completed", result)
            );

        } catch (FileUploadException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error during bulk upload for school {}", schoolId, e);
            throw new FileUploadException(
                "Bulk upload failed",
                "BULK_UPLOAD_ERROR",
                400,
                e
            );
        }
    }

    // ───────────────────────────────────────────────────────────────
    // Example 7: Authorization Check
    // ───────────────────────────────────────────────────────────────
    /**
     * Example showing authorization checks
     * This would typically be in a security component
     *
     * HTTP METHOD: POST /api/students/{id}/approve
     *
     * Example implementation:
     * @PostMapping("/{id}/approve")
     * public ResponseEntity<ApiResponse<String>> approveStudent(@PathVariable Long id) {
     */
    public ResponseEntity<ApiResponse<String>> approveStudent(Long id) {
        log.info("Approving student ID: {}", id);

        // Get current user (implementation depends on your security setup)
        User currentUser = getCurrentUser();

        // Check if user is authenticated
        if (currentUser == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        // Check if user has required permission
        if (!currentUser.hasRole("ADMIN")) {
            throw new ForbiddenException(
                "You do not have permission to approve students",
                ErrorConstants.ERROR_INSUFFICIENT_PERMISSIONS
            );
        }

        String student = studentService.approveStudent(id);

        return ResponseEntity.ok(
            ApiResponse.success("Student approved successfully", student)
        );
    }

    // ───────────────────────────────────────────────────────────────
    // Example 8: List with Query Parameters
    // ───────────────────────────────────────────────────────────────
    /**
     * Get entities with filters
     * Validates filter parameters
     *
     * HTTP METHOD: GET /api/students/search
     *
     * Example implementation:
     * @GetMapping("/search")
     * public ResponseEntity<ApiResponse<List<String>>> getStudents(
     *        @RequestParam Long schoolId,
     *        @RequestParam(required = false) Long classId,
     *        @RequestParam(required = false) Long sectionId,
     *        @RequestParam Long academicYearId) {
     */
    public ResponseEntity<ApiResponse<List<String>>> getStudents(
            Long schoolId,
            Long classId,
            Long sectionId,
            Long academicYearId) {
        log.info("Fetching students for school: {}, class: {}, section: {}",
                 schoolId, classId, sectionId);

        // Validate academicYearId is provided
        if (academicYearId == null || academicYearId <= 0) {
            throw new ValidationException(
                "Academic year ID is required and must be positive",
                ErrorUtil.createFieldErrors("academicYearId", "Invalid academic year")
            );
        }

        List<String> students = studentService.getFilteredStudents(
            schoolId, classId, sectionId, academicYearId
        );

        return ResponseEntity.ok(
            ApiResponse.success("Students retrieved successfully", students)
        );
    }

    // ───────────────────────────────────────────────────────────────
    // Example 9: Pagination with Error Handling
    // ───────────────────────────────────────────────────────────────
    /**
     * Get paginated results
     * Validates pagination parameters
     *
     * HTTP METHOD: GET /api/students/paginated/{schoolId}
     *
     * Example implementation:
     * @GetMapping("/paginated/{schoolId}")
     * public ResponseEntity<ApiResponse<Object>> getStudentsPaged(
     *        @PathVariable Long schoolId,
     *        @RequestParam(defaultValue = "0") int page,
     *        @RequestParam(defaultValue = "20") int size) {
     */
    public ResponseEntity<ApiResponse<Object>> getStudentsPaged(
            Long schoolId,
            int page,
            int size) {
        log.info("Fetching paginated students for school: {}", schoolId);

        // Validate pagination parameters
        if (page < 0) {
            throw new CustomException(
                "Page number must be >= 0",
                "INVALID_PAGE_NUMBER"
            );
        }

        if (size <= 0 || size > 100) {
            throw new CustomException(
                "Page size must be between 1 and 100",
                "INVALID_PAGE_SIZE"
            );
        }

        Object pageData = studentService.getStudentsPaged(schoolId, page, size);

        return ResponseEntity.ok(
            ApiResponse.success("Students retrieved successfully", pageData)
        );
    }

    // ───────────────────────────────────────────────────────────────
    // Example 10: Custom Business Logic Error
    // ───────────────────────────────────────────────────────────────
    /**
     * Promote entity to next level
     * Contains business logic that can fail in multiple ways
     *
     * HTTP METHOD: POST /api/students/{id}/promote
     *
     * Example implementation:
     * @PostMapping("/{id}/promote")
     * public ResponseEntity<ApiResponse<String>> promoteStudent(
     *        @PathVariable Long id,
     *        @RequestParam Long newClassId) {
     */
    public ResponseEntity<ApiResponse<String>> promoteStudent(Long id, Long newClassId) {
        log.info("Promoting student {} to class {}", id, newClassId);

        // Fetch entity
        String student = studentService.getStudentById(id);

        // Verify entity is eligible for promotion
        if (!studentService.isEligibleForPromotion(id)) {
            throw new CustomException(
                "Student is not eligible for promotion due to poor attendance or marks",
                "STUDENT_NOT_ELIGIBLE",
                422  // Unprocessable Entity
            );
        }

        // Verify target class exists
        if (newClassId == null || newClassId <= 0) {
            throw new ResourceNotFoundException(
                "Target class not found",
                ErrorConstants.ERROR_CLASS_NOT_FOUND
            );
        }

        // Perform promotion
        try {
            String promotedStudent = studentService.promoteStudent(id, newClassId);
            return ResponseEntity.ok(
                ApiResponse.success("Student promoted successfully", promotedStudent)
            );
        } catch (Exception e) {
            log.error("Error promoting student {}", id, e);
            throw new DatabaseException(
                "Failed to promote student",
                "PROMOTION_FAILED",
                500,
                e
            );
        }
    }

    // Helper method (implementation depends on your security setup)
    private User getCurrentUser() {
        // TODO: Implement based on your authentication mechanism
        return null;
    }

    // ───────────────────────────────────────────────────────────────
    // Placeholder Classes & Interfaces (for compilation)
    // ───────────────────────────────────────────────────────────────

    static class User {
        boolean hasRole(String role) { return false; }
    }

    /**
     * Service interface - Replace with your actual service
     * NOTE: In actual implementation, inject your real StudentService
     */
    interface IStudentService {
        String getStudentById(Long id);
        String createStudent(String dto);
        String updateStudent(Long id, String dto);
        void deleteStudent(Long id);
        String uploadStudentPhoto(Long id, MultipartFile photo);
        Object bulkUploadStudents(Long schoolId, MultipartFile file);
        String approveStudent(Long id);
        List<String> getFilteredStudents(Long schoolId, Long classId, Long sectionId, Long academicYearId);
        Object getStudentsPaged(Long schoolId, int page, int size);
        boolean isEligibleForPromotion(Long id);
        String promoteStudent(Long id, Long newClassId);
    }
}

