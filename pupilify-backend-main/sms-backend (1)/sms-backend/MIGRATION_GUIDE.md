# Migration Guide: Adopting Production Error Handling

## Overview
This guide helps you migrate your existing codebase to use the new error handling system.

---

## Phase 1: Setup (Day 1)

### 1.1 Verify All Files Are Created
```bash
# Exception classes
ls src/main/java/com/smartschool/api/exception/

# Expected files:
# - ApiError.java ✅
# - CustomException.java ✅
# - ResourceNotFoundException.java ✅
# - ValidationException.java ✅
# - UnauthorizedException.java ✅
# - ForbiddenException.java ✅
# - DuplicateResourceException.java ✅
# - FileUploadException.java ✅
# - DataIntegrityException.java ✅
# - DatabaseException.java ✅
# - ErrorConstants.java ✅
# - ErrorUtil.java ✅
# - GlobalExceptionHandler.java ✅

# Utilities
# src/main/java/com/smartschool/api/util/ApiResponse.java ✅

# Examples
# src/main/java/com/smartschool/api/example/ExampleControllerWithErrorHandling.java
# src/main/java/com/smartschool/api/example/ExampleServiceWithErrorHandling.java
# src/main/java/com/smartschool/api/example/ErrorHandlingIntegrationTests.java
```

### 1.2 Test the Build
```bash
cd sms-backend
mvn clean compile
```

### 1.3 Read Documentation
1. Read: `ERROR_HANDLING_QUICK_REFERENCE.md` (5 mins)
2. Review: `ERROR_HANDLING_GUIDE.md` (20 mins)
3. Skim: Example files (15 mins)

**Time spent: ~40 minutes**

---

## Phase 2: Fix Repository Issues (Day 1)

### 2.1 Fix SubjectRepository Query

**Current Error:**
```
No property 'teacherId' found for type 'Subject'
```

**Location:** `src/main/java/com/smartschool/api/repository/SubjectRepository.java`

**Solution 1: Using @Query**
```java
@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    
    @Query("SELECT s FROM Subject s WHERE s.teacher.id = ?1 AND s.isActive = true AND s.academicYear.id = ?2")
    List<Subject> findByTeacherIdAndIsActiveTrueAndAcademicYearId(Long teacherId, Long academicYearId);
    
    // ... other methods
}
```

**Solution 2: If Subject has teacher relationship**
```java
// In Subject entity:
@ManyToOne
@JoinColumn(name = "teacher_id")
private Teacher teacher;

// In repository:
List<Subject> findByTeacherAndIsActiveTrueAndAcademicYearId(Teacher teacher, Long academicYearId);
```

**Solution 3: Using custom method**
```java
@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long>, SubjectRepositoryCustom {
}

@Repository
public class SubjectRepositoryImpl implements SubjectRepositoryCustom {
    @Autowired
    private EntityManager em;
    
    @Override
    public List<Subject> findByTeacherIdAndIsActiveTrueAndAcademicYearId(Long teacherId, Long academicYearId) {
        return em.createQuery(
            "SELECT s FROM Subject s WHERE s.teacher.id = :teacherId AND s.isActive = true AND s.academicYear.id = :academicYearId",
            Subject.class)
            .setParameter("teacherId", teacherId)
            .setParameter("academicYearId", academicYearId)
            .getResultList();
    }
}
```

### 2.2 Test the Fix
```bash
mvn clean build
```

**Time spent: ~30 minutes**

---

## Phase 3: Update StudentController (Day 2)

### 3.1 Backup Current File
```bash
cp src/main/java/com/smartschool/api/controller/StudentController.java StudentController.java.backup
```

### 3.2 Update Imports
Add these imports at the top:
```java
import com.smartschool.api.exception.*;
import com.smartschool.api.util.ApiResponse;
import org.springframework.http.HttpStatus;
```

### 3.3 Update Individual Methods

#### Before:
```java
@GetMapping("/{id}")
public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
    return ResponseEntity.ok(studentService.getStudentById(id));
}
```

#### After:
```java
@GetMapping("/{id}")
public ResponseEntity<ApiResponse<Student>> getStudentById(@PathVariable Long id) {
    Student student = studentService.getStudentById(id);
    return ResponseEntity.ok(ApiResponse.success("Student retrieved successfully", student));
}
```

#### Before:
```java
@PostMapping
public ResponseEntity<Student> createStudent(@RequestBody StudentDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(studentService.createStudent(dto));
}
```

#### After:
```java
@PostMapping
public ResponseEntity<ApiResponse<Student>> createStudent(@RequestBody StudentDTO dto) {
    Student student = studentService.createStudent(dto);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.created("Student created successfully", student));
}
```

#### Before:
```java
@PostMapping("/onboard/{schoolId}/{classId}/{sectionId}")
public ResponseEntity<?> onboardStudent(...) {
    try {
        // ... logic
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
    }
}
```

#### After:
```java
@PostMapping("/onboard/{schoolId}/{classId}/{sectionId}")
public ResponseEntity<ApiResponse<Student>> onboardStudent(...) {
    log.info("Onboarding student for schoolId:{} with Year ID:{}", schoolId, academicYearId);
    try {
        ObjectMapper mapper = new ObjectMapper();
        Student student = mapper.readValue(studentDataJson, Student.class);
        Student savedStudent = studentService.onboardStudentWithApaar(
                student, photo, marksheet, tc, aadharImg, samagraImg,
                passbookImg, apaarImg, schoolId, classId, sectionId, academicYearId);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.created("Student onboarded successfully", savedStudent));
    } catch (FileUploadException e) {
        throw e;  // Let GlobalExceptionHandler handle it
    } catch (Exception e) {
        log.error("Error onboarding student", e);
        throw new FileUploadException("Student onboarding failed", "ONBOARD_ERROR", 400, e);
    }
}
```

### 3.4 Update All GET Methods
```java
@GetMapping("/report/filter/{schoolId}")
public ResponseEntity<ApiResponse<List<Student>>> getFilteredStudentReport(...) {
    List<Student> students = studentService.getFilteredStudents(...);
    return ResponseEntity.ok(ApiResponse.success("Report retrieved successfully", students));
}

@GetMapping("/school/{schoolId}")
public ResponseEntity<ApiResponse<List<Student>>> getAllStudentsBySchool(...) {
    List<Student> students = studentService.getFilteredStudents(...);
    return ResponseEntity.ok(ApiResponse.success("Students retrieved successfully", students));
}
```

### 3.5 Update All DELETE Methods
```java
@DeleteMapping("/delete/{id}")
public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long id) {
    studentService.deleteStudent(id);
    return ResponseEntity.ok(ApiResponse.success("Student deleted successfully"));
}
```

**Time spent: ~1 hour**

---

## Phase 4: Update StudentService (Day 2)

### 4.1 Add Imports
```java
import com.smartschool.api.exception.*;
import java.util.ArrayList;
import java.util.List;
```

### 4.2 Update getStudentById
```java
@Override
public Student getStudentById(Long studentId) {
    log.info("Fetching student with ID: {}", studentId);
    return studentRepository.findById(studentId)
        .orElseThrow(() -> new ResourceNotFoundException(
            String.format("Student not found with ID: %d", studentId),
            ErrorConstants.ERROR_STUDENT_NOT_FOUND
        ));
}
```

### 4.3 Update createStudent
```java
@Override
public Student createStudent(StudentDTO dto) {
    log.info("Creating new student with email: {}", dto.getEmail());
    
    // Validate
    validateStudentDTO(dto);
    
    // Check duplicate
    if (studentRepository.existsByEmail(dto.getEmail())) {
        throw new DuplicateResourceException(
            "Student with email " + dto.getEmail() + " already exists",
            ErrorConstants.ERROR_DUPLICATE_EMAIL
        );
    }
    
    try {
        Student student = new Student();
        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        // ... set other fields
        return studentRepository.save(student);
    } catch (DataIntegrityViolationException e) {
        log.error("Data integrity violation", e);
        throw new DataIntegrityException(
            "Failed to create student",
            ErrorConstants.ERROR_DATA_CONSTRAINT_VIOLATION,
            409,
            e
        );
    } catch (Exception e) {
        log.error("Error creating student", e);
        throw new DatabaseException(
            "Failed to create student",
            ErrorConstants.ERROR_DATABASE,
            500,
            e
        );
    }
}
```

### 4.4 Add Validation Method
```java
private void validateStudentDTO(StudentDTO dto) {
    List<ApiError.FieldError> errors = new ArrayList<>();
    
    if (dto.getName() == null || dto.getName().trim().isEmpty()) {
        errors.add(new ApiError.FieldError("name", "Name is required"));
    }
    
    if (dto.getEmail() == null || !isValidEmail(dto.getEmail())) {
        errors.add(new ApiError.FieldError("email", "Invalid email format"));
    }
    
    if (dto.getPhone() == null || !isValidPhone(dto.getPhone())) {
        errors.add(new ApiError.FieldError("phone", "Phone must be 10 digits"));
    }
    
    if (!errors.isEmpty()) {
        throw new ValidationException("Validation failed", errors);
    }
}

private boolean isValidEmail(String email) {
    return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
}

private boolean isValidPhone(String phone) {
    return phone.matches("^[0-9]{10}$");
}
```

### 4.5 Update deleteStudent
```java
@Override
public void deleteStudent(Long studentId) {
    log.info("Deleting student ID: {}", studentId);
    Student student = getStudentById(studentId); // throws if not found
    student.setIsActive(false);
    try {
        studentRepository.save(student);
    } catch (Exception e) {
        log.error("Error deleting student", e);
        throw new DatabaseException("Failed to delete student", 
            ErrorConstants.ERROR_DATABASE, 500, e);
    }
}
```

**Time spent: ~1.5 hours**

---

## Phase 5: Update Other Controllers (Day 3+)

### 5.1 Priority Order
1. ✅ StudentController (Done)
2. TeacherController
3. SchoolController
4. ClassController
5. Other controllers

### 5.2 Pattern for Each Controller
```java
// Step 1: Update imports
import com.smartschool.api.exception.*;
import com.smartschool.api.util.ApiResponse;

// Step 2: Update return types
// FROM: ResponseEntity<Entity>
// TO: ResponseEntity<ApiResponse<Entity>>

// Step 3: Wrap successful responses
// FROM: return ResponseEntity.ok(entity);
// TO: return ResponseEntity.ok(ApiResponse.success("message", entity));

// Step 4: Use appropriate status codes
// FROM: ResponseEntity.status(HttpStatus.CREATED).body(entity)
// TO: ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created("message", entity))

// Step 5: Let exceptions be thrown (handled by GlobalExceptionHandler)
// FROM: try-catch with manual response
// TO: let exceptions propagate
```

**Time spent: ~1-2 hours per controller**

---

## Phase 6: Write Tests (Day 3-4)

### 6.1 Create Test File
```bash
mkdir -p src/test/java/com/smartschool/api/controller
touch src/test/java/com/smartschool/api/controller/StudentControllerErrorHandlingTests.java
```

### 6.2 Copy Test Pattern
Use `ErrorHandlingIntegrationTests.java` as a template and adapt for StudentController.

### 6.3 Test Coverage
- [ ] Test all success scenarios (HTTP 200, 201)
- [ ] Test all error scenarios (400, 401, 403, 404, 409, 500)
- [ ] Test validation with multiple field errors
- [ ] Test duplicate resource errors
- [ ] Test not found errors
- [ ] Test response format includes all required fields

### 6.4 Run Tests
```bash
mvn test
```

**Time spent: ~2-3 hours**

---

## Phase 7: Code Review & QA (Day 4-5)

### 7.1 Checklist
- [ ] All exceptions are being thrown correctly
- [ ] No raw try-catch returning error responses
- [ ] GlobalExceptionHandler is handling all exceptions
- [ ] Response format is consistent
- [ ] Error codes are being used
- [ ] Validation errors include field details
- [ ] No sensitive data in error messages
- [ ] Tests are passing
- [ ] Build is successful

### 7.2 Manual Testing
1. Test 404 error - verify error response format
2. Test validation error - verify field errors
3. Test duplicate - verify 409 response
4. Test file upload - verify file validation
5. Test success - verify response format

### 7.3 Load Testing
```bash
# Optional: Run basic load test
ab -n 1000 -c 10 http://localhost:8080/api/students/1
```

**Time spent: ~2-3 hours**

---

## Phase 8: Deployment (Day 5)

### 8.1 Update application.properties
```properties
# In application.properties
server.error.include-message=always
server.error.include-binding-errors=always
server.error.include-stacktrace=on_param
server.error.include-exception=false

spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

logging.level.com.smartschool.api.exception.GlobalExceptionHandler=DEBUG
```

### 8.2 Final Build
```bash
mvn clean build
```

### 8.3 Deploy
```bash
# Build JAR
mvn clean package

# Run
java -jar target/sms-backend-0.0.1-SNAPSHOT.jar
```

### 8.4 Verify in Production
1. Test a few endpoints
2. Check logs for proper error handling
3. Monitor for any exceptions
4. Verify error responses format

**Time spent: ~1 hour**

---

## Migration Timeline

| Phase | Task | Duration | Status |
|-------|------|----------|--------|
| 1 | Setup & Documentation | 40 min | ⏱️ |
| 2 | Fix Repository Issues | 30 min | ⏱️ |
| 3 | Update StudentController | 1 hour | ⏱️ |
| 4 | Update StudentService | 1.5 hours | ⏱️ |
| 5 | Update Other Controllers | 5-10 hours | ⏱️ |
| 6 | Write Tests | 2-3 hours | ⏱️ |
| 7 | Code Review & QA | 2-3 hours | ⏱️ |
| 8 | Deployment | 1 hour | ⏱️ |
| **Total** | **Complete Adoption** | **13-20 hours** | |

---

## Rollback Plan

If you need to rollback:

```bash
# Restore backup
cp StudentController.java.backup src/main/java/com/smartschool/api/controller/StudentController.java

# Remove new exception classes (optional)
rm src/main/java/com/smartschool/api/exception/Validation*.java
rm src/main/java/com/smartschool/api/exception/Unauthorized*.java
# ... etc

# Rebuild
mvn clean build
```

---

## Common Issues & Solutions

### Issue 1: GlobalExceptionHandler Not Being Called
**Solution**: Ensure `@RestControllerAdvice` is on the class and it's in a component-scanned package.

### Issue 2: ApiResponse Not Imported
**Solution**: Add import:
```java
import com.smartschool.api.util.ApiResponse;
```

### Issue 3: ErrorConstants Not Found
**Solution**: Add import:
```java
import com.smartschool.api.exception.ErrorConstants;
```

### Issue 4: Tests Failing
**Solution**: Review test output and compare with `ErrorHandlingIntegrationTests.java` example.

### Issue 5: Build Errors
**Solution**: Run `mvn clean compile` to see full error messages.

---

## Resources

- **Main Guide**: `ERROR_HANDLING_GUIDE.md`
- **Quick Reference**: `ERROR_HANDLING_QUICK_REFERENCE.md`
- **Checklist**: `IMPLEMENTATION_CHECKLIST.md`
- **Summary**: `README_ERROR_HANDLING.md`
- **Examples**: 
  - `ExampleControllerWithErrorHandling.java`
  - `ExampleServiceWithErrorHandling.java`
  - `ErrorHandlingIntegrationTests.java`

---

## Success Criteria

Migration is successful when:

✅ All builds pass  
✅ All tests pass  
✅ Error responses are consistent  
✅ Error codes are present  
✅ No stack traces in production  
✅ Validation errors include field details  
✅ All endpoints working as expected  
✅ Logs show proper error handling  

---

## Questions?

Refer to the comprehensive documentation or review the example implementations for guidance.

**Happy migrating! 🚀**

