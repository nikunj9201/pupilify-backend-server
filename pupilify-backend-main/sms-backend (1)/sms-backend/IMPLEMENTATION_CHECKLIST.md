# Error Handling Implementation Checklist

## Step 1: Review Created Exception Classes ✅
These files have been created and are ready to use:
- [x] `CustomException.java` - Enhanced with error codes and status codes
- [x] `ResourceNotFoundException.java` - Enhanced with error codes
- [x] `ValidationException.java` - NEW - For validation errors with field details
- [x] `UnauthorizedException.java` - NEW - For authentication failures (401)
- [x] `ForbiddenException.java` - NEW - For authorization failures (403)
- [x] `DuplicateResourceException.java` - NEW - For duplicate resources (409)
- [x] `FileUploadException.java` - NEW - For file upload errors
- [x] `DataIntegrityException.java` - NEW - For database constraint violations
- [x] `DatabaseException.java` - NEW - For database operation failures

## Step 2: Review Core Classes ✅
- [x] `ApiError.java` - Enhanced with error codes, field errors, and exception details
- [x] `GlobalExceptionHandler.java` - Completely rewritten with comprehensive exception handling
- [x] `ErrorConstants.java` - NEW - Centralized error codes and messages
- [x] `ErrorUtil.java` - NEW - Convenience methods for throwing exceptions
- [x] `ApiResponse.java` - NEW - Standard response wrapper for successful responses

## Step 3: Review Documentation Files ✅
- [x] `ERROR_HANDLING_GUIDE.md` - Comprehensive guide with examples
- [x] `ERROR_HANDLING_QUICK_REFERENCE.md` - Quick reference for developers

## Step 4: Review Example Implementations ✅
- [x] `ExampleControllerWithErrorHandling.java` - 10 controller examples
- [x] `ExampleServiceWithErrorHandling.java` - Service layer examples
- [x] `ErrorHandlingIntegrationTests.java` - Integration test examples

---

## Step 5: Apply to Your Existing Controllers

### For StudentController:
```java
// Instead of this:
try {
    ObjectMapper mapper = new ObjectMapper();
    Student student = mapper.readValue(studentDataJson, Student.class);
    // ...
    return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
} catch (Exception e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
}

// Use this:
@PostMapping("/onboard/{schoolId}/{classId}/{sectionId}")
public ResponseEntity<ApiResponse<Student>> onboardStudent(
        @PathVariable Long schoolId,
        @PathVariable Long classId,
        @PathVariable Long sectionId,
        @RequestParam("studentData") String studentDataJson,
        @RequestParam("photo") MultipartFile photo,
        // ... other params
        @RequestParam Long academicYearId) {
    
    try {
        ObjectMapper mapper = new ObjectMapper();
        Student student = mapper.readValue(studentDataJson, Student.class);
        Student savedStudent = studentService.onboardStudentWithApaar(
                student, photo, marksheet, tc, aadharImg, samagraImg,
                passbookImg, apaarImg, schoolId, classId, sectionId, academicYearId);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.created("Student onboarded successfully", savedStudent));
    } catch (FileUploadException e) {
        throw e; // Will be handled by GlobalExceptionHandler
    } catch (Exception e) {
        throw new FileUploadException("Student onboarding failed", "ONBOARD_ERROR", 400, e);
    }
}
```

### For getStudentById endpoints:
```java
@GetMapping("/{id}")
public ResponseEntity<ApiResponse<Student>> getStudentById(@PathVariable Long id) {
    Student student = studentService.getStudentById(id);
    // Service throws ResourceNotFoundException if not found
    return ResponseEntity.ok(ApiResponse.success("Student retrieved successfully", student));
}
```

### For delete endpoints:
```java
@DeleteMapping("/delete/{id}")
public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long id) {
    studentService.deleteStudent(id);
    // Service throws ResourceNotFoundException if not found
    return ResponseEntity.ok(ApiResponse.success("Student deleted successfully"));
}
```

---

## Step 6: Update Your Service Layer

### For StudentService:
```java
@Service
public class StudentServiceImpl implements StudentService {

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
        // Validate input
        validateStudentDTO(dto);
        
        // Check duplicates
        if (studentRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException(
                "Email already exists: " + dto.getEmail(),
                ErrorConstants.ERROR_DUPLICATE_EMAIL
            );
        }
        
        try {
            Student student = new Student();
            // ... set fields from dto
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
                "Failed to create student",
                ErrorConstants.ERROR_DATABASE,
                500,
                e
            );
        }
    }

    private void validateStudentDTO(StudentDTO dto) {
        List<ApiError.FieldError> errors = new ArrayList<>();
        
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            errors.add(new ApiError.FieldError("name", "Name is required"));
        }
        if (dto.getEmail() == null || !isValidEmail(dto.getEmail())) {
            errors.add(new ApiError.FieldError("email", "Invalid email format"));
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed", errors);
        }
    }
}
```

---

## Step 7: Fix the Repository Query Error

From your error:
```
No property 'teacherId' found for type 'Subject'
```

This needs to be fixed in your SubjectRepository. The property name might be different.

### Check your Subject entity:
```java
@Entity
public class Subject {
    // ... fields
    // Should be 'teacher' or similar, not 'teacherId'
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
}
```

### Fix the repository query:
```java
// Instead of:
List<Subject> findByTeacherIdAndIsActiveTrueAndAcademicYearId(Long teacherId, Long academicYearId);

// Use:
List<Subject> findByTeacherAndIsActiveTrueAndAcademicYearId(Teacher teacher, Long academicYearId);

// Or if teacher_id is a column:
@Query("SELECT s FROM Subject s WHERE s.teacher.id = ?1 AND s.isActive = true AND s.academicYear.id = ?2")
List<Subject> findByTeacherIdAndIsActiveTrueAndAcademicYearId(Long teacherId, Long academicYearId);
```

---

## Step 8: Testing Checklist

- [ ] Create unit tests for service layer
- [ ] Create integration tests for controller endpoints
- [ ] Test happy path (successful scenarios)
- [ ] Test error scenarios (all error types)
- [ ] Test validation errors with multiple field errors
- [ ] Test file upload validation
- [ ] Test database constraint violations
- [ ] Test unauthorized/forbidden access
- [ ] Verify error responses contain error codes
- [ ] Verify error responses contain timestamps
- [ ] Test that sensitive data is not exposed in 500 errors

### Run Tests:
```bash
mvn test
```

---

## Step 9: Configuration Updates

Add to `application.properties`:

```properties
# Error handling
server.error.include-message=always
server.error.include-binding-errors=always
server.error.include-stacktrace=on_param
server.error.include-exception=false

# File upload
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Logging
logging.level.com.smartschool.api.exception.GlobalExceptionHandler=DEBUG
logging.level.com.smartschool.api=INFO
```

---

## Step 10: Database Migration (If Needed)

If your Subject entity is missing the teacher relationship:

```sql
-- Add teacher_id column if missing
ALTER TABLE subject ADD COLUMN teacher_id BIGINT;

-- Add foreign key constraint
ALTER TABLE subject 
ADD CONSTRAINT fk_subject_teacher 
FOREIGN KEY (teacher_id) REFERENCES teacher(id);

-- Create index for performance
CREATE INDEX idx_subject_teacher_academic_year 
ON subject(teacher_id, academic_year_id, is_active);
```

---

## Step 11: Verify Build

```bash
# Clean build
mvn clean build

# Or with Maven Wrapper
./mvnw clean build
```

---

## Common Patterns to Apply

### Pattern 1: Get By ID
```java
public Student getStudentById(Long id) {
    return studentRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
}
```

### Pattern 2: Create with Duplicate Check
```java
public Student createStudent(StudentDTO dto) {
    if (studentRepository.existsByEmail(dto.getEmail())) {
        throw new DuplicateResourceException("Email already exists");
    }
    return studentRepository.save(new Student(dto));
}
```

### Pattern 3: Update with Validation
```java
public Student updateStudent(Long id, StudentDTO dto) {
    Student student = getStudentById(id); // throws if not found
    student.setName(dto.getName());
    return studentRepository.save(student);
}
```

### Pattern 4: Delete
```java
public void deleteStudent(Long id) {
    Student student = getStudentById(id); // throws if not found
    student.setActive(false);
    studentRepository.save(student);
}
```

### Pattern 5: File Upload
```java
public String uploadFile(Long studentId, MultipartFile file) {
    Student student = getStudentById(studentId); // throws if not found
    
    if (file.isEmpty()) {
        throw new FileUploadException("File is empty");
    }
    
    // ... validate file type, size
    
    return fileService.saveFile(file);
}
```

---

## Production Checklist

Before going to production:

- [ ] All exceptions are caught and handled
- [ ] No stack traces exposed in production responses
- [ ] Error codes are meaningful and documented
- [ ] All HTTP status codes are appropriate
- [ ] Field validation is comprehensive
- [ ] Database constraint violations are handled gracefully
- [ ] File upload limits are configured
- [ ] Logging is configured appropriately
- [ ] Security headers are set
- [ ] CORS is properly configured
- [ ] Rate limiting is implemented (if needed)
- [ ] Integration tests are passing
- [ ] Load testing is done
- [ ] Error messages don't expose sensitive information

---

## Helpful Links in Project

1. **Main Guide**: `ERROR_HANDLING_GUIDE.md`
2. **Quick Reference**: `ERROR_HANDLING_QUICK_REFERENCE.md`
3. **Example Controller**: `src/main/java/com/smartschool/api/example/ExampleControllerWithErrorHandling.java`
4. **Example Service**: `src/main/java/com/smartschool/api/example/ExampleServiceWithErrorHandling.java`
5. **Example Tests**: `src/main/java/com/smartschool/api/example/ErrorHandlingIntegrationTests.java`

---

## Support & Questions

If you encounter issues:

1. Check if the exception is defined in the `exception` folder
2. Review the relevant example file for usage
3. Ensure GlobalExceptionHandler is being used
4. Check that ApiResponse is imported correctly
5. Verify error codes match ErrorConstants

---

## Next Steps

1. ✅ Review all created files
2. ✅ Update StudentController to use new error handling
3. ✅ Update StudentService to use new exceptions
4. ✅ Update other controllers following the same pattern
5. ✅ Write integration tests
6. ✅ Fix the repository query issue (teacherId property)
7. ✅ Run tests and verify
8. ✅ Deploy with confidence

---

**Status**: Ready for Implementation
**Created**: March 10, 2026
**Version**: 1.0

