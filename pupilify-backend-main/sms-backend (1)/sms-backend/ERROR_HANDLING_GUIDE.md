# Error Handling Guide for SMS Backend

## Overview
This document provides comprehensive guidelines for error handling and exception management in the SMS Backend application. All custom exceptions are automatically handled by the `GlobalExceptionHandler` which provides consistent error responses across the API.

## Exception Classes

### 1. **CustomException**
Used for general business logic errors and custom exceptions.

```java
// Basic usage
throw new CustomException("Invalid input provided");

// With error code
throw new CustomException("Invalid input provided", "INVALID_INPUT");

// With error code and status code
throw new CustomException("Payment failed", "PAYMENT_FAILED", 402);

// With cause
throw new CustomException("Operation failed", "OPERATION_FAILED", 400, cause);
```

### 2. **ResourceNotFoundException**
Thrown when a requested resource is not found.

```java
// Basic usage
throw new ResourceNotFoundException("Student not found");

// With error code
throw new ResourceNotFoundException("Student not found", "STUDENT_NOT_FOUND");

// With custom status code
throw new ResourceNotFoundException("Resource not found", "CUSTOM_NOT_FOUND", 404);
```

### 3. **ValidationException**
Used for validation errors with field-level details.

```java
// Basic usage
throw new ValidationException("Validation failed");

// With field errors
List<ApiError.FieldError> fieldErrors = new ArrayList<>();
fieldErrors.add(new ApiError.FieldError("email", "Invalid email format"));
fieldErrors.add(new ApiError.FieldError("phone", "Phone must be 10 digits"));
throw new ValidationException("Validation failed", fieldErrors);

// Using ErrorUtil
List<ApiError.FieldError> errors = ErrorUtil.createFieldErrors("email", "Email already exists");
throw new ValidationException("Validation failed", errors);
```

### 4. **UnauthorizedException**
Thrown when user authentication fails.

```java
throw new UnauthorizedException("Invalid credentials");
throw new UnauthorizedException("User not authenticated", "AUTH_REQUIRED");
```

### 5. **ForbiddenException**
Thrown when user lacks permissions for an operation.

```java
throw new ForbiddenException("You do not have permission to access this resource");
throw new ForbiddenException("Access denied", "INSUFFICIENT_PERMISSIONS");
```

### 6. **DuplicateResourceException**
Thrown when attempting to create a duplicate resource.

```java
throw new DuplicateResourceException("Email already exists");
throw new DuplicateResourceException("User with this email already exists", "DUPLICATE_EMAIL");
```

### 7. **FileUploadException**
Used for file upload related errors.

```java
throw new FileUploadException("File upload failed");
throw new FileUploadException("Invalid file format", "INVALID_FILE_FORMAT");
throw new FileUploadException("File too large", "FILE_SIZE_EXCEEDED", 413, cause);
```

### 8. **DatabaseException**
Thrown for database operation failures.

```java
throw new DatabaseException("Failed to save record");
throw new DatabaseException("Connection timeout", "DATABASE_CONNECTION_ERROR", 500, cause);
```

### 9. **DataIntegrityException**
Thrown for data integrity violations.

```java
throw new DataIntegrityException("Foreign key constraint violation");
throw new DataIntegrityException("Parent record does not exist", "FOREIGN_KEY_VIOLATION");
```

## Error Constants

Use `ErrorConstants` class for predefined error codes and messages:

```java
import com.smartschool.api.exception.ErrorConstants;

// Error Codes
ErrorConstants.ERROR_STUDENT_NOT_FOUND
ErrorConstants.ERROR_TEACHER_NOT_FOUND
ErrorConstants.ERROR_DUPLICATE_EMAIL
ErrorConstants.ERROR_VALIDATION_FAILED
ErrorConstants.ERROR_FILE_UPLOAD
ErrorConstants.ERROR_DATABASE

// Error Messages
ErrorConstants.MSG_STUDENT_NOT_FOUND
ErrorConstants.MSG_UNAUTHORIZED
ErrorConstants.MSG_VALIDATION_FAILED
ErrorConstants.MSG_DATABASE_ERROR

// HTTP Status Codes
ErrorConstants.STATUS_NOT_FOUND      // 404
ErrorConstants.STATUS_UNAUTHORIZED  // 401
ErrorConstants.STATUS_FORBIDDEN     // 403
ErrorConstants.STATUS_CONFLICT      // 409
```

## ErrorUtil Helper Methods

Use `ErrorUtil` for convenient exception throwing:

```java
import com.smartschool.api.exception.ErrorUtil;
import com.smartschool.api.exception.ErrorConstants;

// Throw ResourceNotFoundException
ErrorUtil.throwResourceNotFound("Student not found");
ErrorUtil.throwResourceNotFound("Student not found", ErrorConstants.ERROR_STUDENT_NOT_FOUND);

// Throw CustomException
ErrorUtil.throwCustomException("Invalid input", "INVALID_INPUT");
ErrorUtil.throwCustomException("Invalid input", "INVALID_INPUT", 400);

// Throw ValidationException
ErrorUtil.throwValidationException("Validation failed");
List<ApiError.FieldError> errors = ErrorUtil.createFieldErrors("email", "Invalid email");
ErrorUtil.throwValidationException("Validation failed", errors);

// Throw other exceptions
ErrorUtil.throwUnauthorized("Invalid credentials");
ErrorUtil.throwForbidden("Access denied");
ErrorUtil.throwDuplicateResource("Email already exists");
ErrorUtil.throwFileUploadException("Invalid file format");
ErrorUtil.throwDatabaseException("Connection failed", cause);
ErrorUtil.throwDataIntegrityException("Constraint violation");
```

## Controller Usage Examples

### Example 1: Student Not Found
```java
@GetMapping("/{studentId}")
public ResponseEntity<ApiResponse<Student>> getStudent(@PathVariable Long studentId) {
    Student student = studentRepository.findById(studentId)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Student not found with ID: " + studentId,
            ErrorConstants.ERROR_STUDENT_NOT_FOUND
        ));
    
    return ResponseEntity.ok(ApiResponse.success("Student retrieved successfully", student));
}
```

### Example 2: Validation Error
```java
@PostMapping
public ResponseEntity<ApiResponse<Student>> createStudent(@RequestBody StudentDTO dto) {
    // Validate input
    List<ApiError.FieldError> errors = new ArrayList<>();
    
    if (dto.getEmail() == null || !isValidEmail(dto.getEmail())) {
        errors.add(new ApiError.FieldError("email", "Invalid email format"));
    }
    
    if (dto.getPhone() == null || !isValidPhone(dto.getPhone())) {
        errors.add(new ApiError.FieldError("phone", "Phone must be 10 digits"));
    }
    
    if (!errors.isEmpty()) {
        throw new ValidationException("Input validation failed", errors);
    }
    
    Student student = studentService.createStudent(dto);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.created("Student created successfully", student));
}
```

### Example 3: Duplicate Resource
```java
@PostMapping
public ResponseEntity<ApiResponse<Teacher>> createTeacher(@RequestBody TeacherDTO dto) {
    if (teacherRepository.existsByEmail(dto.getEmail())) {
        throw new DuplicateResourceException(
            "Teacher with email " + dto.getEmail() + " already exists",
            ErrorConstants.ERROR_DUPLICATE_EMAIL
        );
    }
    
    Teacher teacher = teacherService.createTeacher(dto);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.created("Teacher created successfully", teacher));
}
```

### Example 4: File Upload
```java
@PostMapping("/upload")
public ResponseEntity<ApiResponse<String>> uploadFile(@RequestParam MultipartFile file) {
    try {
        if (file.isEmpty()) {
            throw new FileUploadException("File is empty", "EMPTY_FILE");
        }
        
        String allowedTypes = "image/jpeg,image/png,application/pdf";
        if (!allowedTypes.contains(file.getContentType())) {
            throw new FileUploadException(
                "Invalid file format. Allowed: JPEG, PNG, PDF",
                ErrorConstants.ERROR_INVALID_FILE_FORMAT
            );
        }
        
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            throw new FileUploadException(
                "File size exceeds 5MB limit",
                ErrorConstants.ERROR_FILE_SIZE_EXCEEDED,
                413
            );
        }
        
        String fileName = fileService.saveFile(file);
        return ResponseEntity.ok(ApiResponse.success("File uploaded successfully", fileName));
        
    } catch (FileUploadException e) {
        throw e;
    } catch (Exception e) {
        throw new FileUploadException("File upload failed", "UPLOAD_ERROR", 400, e);
    }
}
```

### Example 5: Authorization Check
```java
@PostMapping("/{studentId}/approve")
public ResponseEntity<ApiResponse<Student>> approveStudent(@PathVariable Long studentId) {
    User currentUser = getCurrentUser();
    
    if (currentUser == null) {
        throw new UnauthorizedException("User not authenticated");
    }
    
    if (!currentUser.hasRole("ADMIN")) {
        throw new ForbiddenException(
            "You do not have permission to approve students",
            ErrorConstants.ERROR_INSUFFICIENT_PERMISSIONS
        );
    }
    
    Student student = studentService.approveStudent(studentId);
    return ResponseEntity.ok(ApiResponse.success("Student approved successfully", student));
}
```

### Example 6: Database Error Handling
```java
@PostMapping
public ResponseEntity<ApiResponse<Student>> createStudent(@RequestBody StudentDTO dto) {
    try {
        Student student = studentRepository.save(new Student(dto));
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.created("Student created successfully", student));
            
    } catch (DataIntegrityViolationException e) {
        throw new DataIntegrityException(
            "Failed to create student: constraint violation",
            ErrorConstants.ERROR_DATA_CONSTRAINT_VIOLATION
        );
    } catch (Exception e) {
        throw new DatabaseException(
            "Failed to create student",
            ErrorConstants.ERROR_DATABASE,
            500,
            e
        );
    }
}
```

## Response Format

### Success Response (2xx)
```json
{
    "success": true,
    "statusCode": 200,
    "message": "Student retrieved successfully",
    "data": {
        "id": 1,
        "name": "John Doe",
        "email": "john@example.com"
    },
    "timestamp": "2026-03-10T10:30:00"
}
```

### Error Response (4xx/5xx)
```json
{
    "status": 400,
    "message": "Validation failed",
    "path": "/api/students",
    "errorCode": "VALIDATION_ERROR",
    "fieldErrors": [
        {
            "field": "email",
            "message": "Invalid email format"
        },
        {
            "field": "phone",
            "message": "Phone must be 10 digits"
        }
    ],
    "timestamp": "2026-03-10T10:30:00"
}
```

### Not Found Response
```json
{
    "status": 404,
    "message": "Student not found with ID: 999",
    "path": "/api/students/999",
    "errorCode": "STUDENT_NOT_FOUND",
    "timestamp": "2026-03-10T10:30:00"
}
```

### Unauthorized Response
```json
{
    "status": 401,
    "message": "User not authenticated",
    "path": "/api/students/approve",
    "errorCode": "UNAUTHORIZED",
    "timestamp": "2026-03-10T10:30:00"
}
```

### Database Error Response
```json
{
    "status": 500,
    "message": "Database operation failed. Please try again later.",
    "path": "/api/students",
    "errorCode": "DATABASE_ERROR",
    "exception": "org.springframework.dao.DataIntegrityViolationException",
    "timestamp": "2026-03-10T10:30:00"
}
```

## Service Layer Exception Handling

### Example Service Implementation
```java
@Service
public class StudentServiceImpl implements StudentService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Override
    public Student getStudentById(Long studentId) {
        return studentRepository.findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format("Student not found with ID: %d", studentId),
                ErrorConstants.ERROR_STUDENT_NOT_FOUND
            ));
    }
    
    @Override
    public Student createStudent(StudentDTO dto) {
        // Validate
        if (studentRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException(
                "Email already exists: " + dto.getEmail(),
                ErrorConstants.ERROR_DUPLICATE_EMAIL
            );
        }
        
        try {
            Student student = new Student(dto);
            return studentRepository.save(student);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityException(
                "Failed to create student",
                ErrorConstants.ERROR_DATA_CONSTRAINT_VIOLATION,
                409,
                e
            );
        } catch (Exception e) {
            throw new DatabaseException(
                "Unexpected error while creating student",
                ErrorConstants.ERROR_DATABASE,
                500,
                e
            );
        }
    }
    
    @Override
    public Student updateStudent(Long studentId, StudentDTO dto) {
        Student student = getStudentById(studentId);
        
        if (!student.getEmail().equals(dto.getEmail()) && 
            studentRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException(
                "Email already exists: " + dto.getEmail(),
                ErrorConstants.ERROR_DUPLICATE_EMAIL
            );
        }
        
        // Update logic
        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        
        try {
            return studentRepository.save(student);
        } catch (Exception e) {
            throw new DatabaseException(
                "Failed to update student",
                ErrorConstants.ERROR_DATABASE,
                500,
                e
            );
        }
    }
}
```

## Best Practices

1. **Always provide meaningful error messages** - Help users understand what went wrong
2. **Use appropriate exception types** - Choose the exception that best represents the error
3. **Include error codes** - Make it easier for clients to handle specific errors
4. **Log exceptions** - The GlobalExceptionHandler logs all exceptions
5. **Provide field-level validation errors** - Use ApiError.FieldError for detailed validation feedback
6. **Don't expose sensitive information** - Generic messages for 500 errors in production
7. **Use ErrorConstants** - For consistent error codes across the application
8. **Chain exceptions** - When wrapping exceptions, include the original cause
9. **Use ApiResponse for success** - Maintain consistent response format
10. **Handle database exceptions** - Convert database errors to business exceptions

## Testing Error Handling

```java
@Test
public void testStudentNotFound() {
    Long studentId = 999L;
    
    MvcResult result = mockMvc.perform(get("/api/students/" + studentId))
        .andExpect(status().isNotFound())
        .andReturn();
    
    String content = result.getResponse().getContentAsString();
    assertTrue(content.contains("STUDENT_NOT_FOUND"));
    assertTrue(content.contains("Student not found"));
}

@Test
public void testValidationError() {
    StudentDTO dto = new StudentDTO();
    dto.setEmail("invalid-email");
    dto.setPhone("123");
    
    MvcResult result = mockMvc.perform(post("/api/students")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isBadRequest())
        .andReturn();
    
    String content = result.getResponse().getContentAsString();
    assertTrue(content.contains("VALIDATION_ERROR"));
    assertTrue(content.contains("fieldErrors"));
}
```

## Configuration (Optional)

If needed, add to `application.properties`:

```properties
# Error handling
server.error.include-message=always
server.error.include-binding-errors=always
server.error.include-stacktrace=on_param
server.error.include-exception=false

# File upload
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

## Summary

The error handling system provides:
- ✅ Comprehensive exception classes for different scenarios
- ✅ Centralized exception handling via GlobalExceptionHandler
- ✅ Consistent error response format
- ✅ Field-level validation errors
- ✅ Error codes for client-side error handling
- ✅ Detailed logging for debugging
- ✅ Security (no sensitive data in responses)
- ✅ Easy-to-use ErrorUtil and ErrorConstants classes

