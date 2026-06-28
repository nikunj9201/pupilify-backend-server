# Quick Reference - Error Handling

## Throwable Exceptions Quick Map

| Exception | Use Case | Example |
|-----------|----------|---------|
| `ResourceNotFoundException` | Resource not found (404) | Student ID doesn't exist |
| `CustomException` | General business error (400) | Invalid input, invalid state |
| `ValidationException` | Validation failures | Form validation, field errors |
| `UnauthorizedException` | Authentication failure (401) | Invalid credentials, no token |
| `ForbiddenException` | Authorization failure (403) | No permission for operation |
| `DuplicateResourceException` | Resource already exists (409) | Email duplicate, unique constraint |
| `FileUploadException` | File upload errors (400) | Invalid format, size exceeded |
| `DataIntegrityException` | Data constraint violation (409) | Foreign key, unique key violation |
| `DatabaseException` | Database operation failure (500) | Connection error, query failure |

## Quick Usage Examples

### Throw Not Found
```java
throw new ResourceNotFoundException("Student not found");
throw new ResourceNotFoundException("Student not found", "STUDENT_NOT_FOUND");
```

### Throw Validation Error
```java
throw new ValidationException("Invalid input");
List<ApiError.FieldError> errors = ErrorUtil.createFieldErrors("email", "Invalid email");
throw new ValidationException("Validation failed", errors);
```

### Throw Duplicate
```java
throw new DuplicateResourceException("Email already exists");
```

### Throw Unauthorized
```java
throw new UnauthorizedException("Invalid credentials");
```

### Throw Forbidden
```java
throw new ForbiddenException("Access denied");
```

### Throw File Upload Error
```java
throw new FileUploadException("Invalid file format");
throw new FileUploadException("File too large", "FILE_SIZE_EXCEEDED", 413);
```

### Throw Database Error
```java
throw new DatabaseException("Connection failed", cause);
```

## Error Constants - Key Codes

```
Not Found:
- ERROR_STUDENT_NOT_FOUND
- ERROR_TEACHER_NOT_FOUND
- ERROR_SCHOOL_NOT_FOUND
- ERROR_CLASS_NOT_FOUND

Validation:
- ERROR_VALIDATION_FAILED
- ERROR_INVALID_INPUT
- ERROR_INVALID_EMAIL
- ERROR_INVALID_PHONE

Authorization:
- ERROR_UNAUTHORIZED
- ERROR_FORBIDDEN
- ERROR_INSUFFICIENT_PERMISSIONS

Duplicate:
- ERROR_DUPLICATE_EMAIL
- ERROR_DUPLICATE_PHONE
- ERROR_DUPLICATE_STUDENT

File Upload:
- ERROR_FILE_UPLOAD
- ERROR_INVALID_FILE_FORMAT
- ERROR_FILE_SIZE_EXCEEDED

Database:
- ERROR_DATABASE
- ERROR_DATA_INTEGRITY
- ERROR_FOREIGN_KEY_VIOLATION
```

## Response Status Codes

| Code | Meaning | Exception |
|------|---------|-----------|
| 400 | Bad Request | CustomException, ValidationException, FileUploadException |
| 401 | Unauthorized | UnauthorizedException |
| 403 | Forbidden | ForbiddenException |
| 404 | Not Found | ResourceNotFoundException |
| 409 | Conflict | DuplicateResourceException, DataIntegrityException |
| 413 | Payload Too Large | FileUploadException (file size) |
| 500 | Server Error | DatabaseException, Exception |

## Success Response Format
```java
// Import
import com.smartschool.api.util.ApiResponse;

// Return success
return ResponseEntity.ok(ApiResponse.success("Message", data));

// Return created
return ResponseEntity.status(HttpStatus.CREATED)
    .body(ApiResponse.created("Message", data));
```

## Service Layer Pattern

```java
@Service
public class XyzService {
    
    public Xyz getById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Not found"));
    }
    
    public Xyz create(XyzDTO dto) {
        // Check duplicates
        if (repository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Email exists");
        }
        
        // Validate
        if (!isValid(dto)) {
            throw new ValidationException("Invalid input");
        }
        
        try {
            return repository.save(new Xyz(dto));
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityException("Constraint violation");
        } catch (Exception e) {
            throw new DatabaseException("Operation failed", e);
        }
    }
}
```

## Controller Pattern

```java
@RestController
@RequestMapping("/api/xyz")
public class XyzController {
    
    @Autowired
    private XyzService service;
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Xyz>> getById(@PathVariable Long id) {
        Xyz xyz = service.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Retrieved", xyz));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Xyz>> create(@RequestBody XyzDTO dto) {
        Xyz xyz = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.created("Created", xyz));
    }
    
    @PostMapping("/{id}/upload")
    public ResponseEntity<ApiResponse<String>> upload(
            @PathVariable Long id,
            @RequestParam MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new FileUploadException("File is empty");
            }
            String url = service.uploadFile(id, file);
            return ResponseEntity.ok(ApiResponse.success("Uploaded", url));
        } catch (FileUploadException e) {
            throw e;
        } catch (Exception e) {
            throw new FileUploadException("Upload failed", "UPLOAD_ERROR", 400, e);
        }
    }
}
```

## Error Handler Features

✅ Automatically logs all exceptions
✅ Converts Database exceptions (DataIntegrityViolationException) to DataIntegrityException
✅ Handles file size exceeded (MaxUploadSizeExceededException)
✅ Handles endpoint not found (NoHandlerFoundException)
✅ Handles type mismatches (MethodArgumentTypeMismatchException)
✅ Provides field-level validation errors
✅ Masks sensitive error details for 500 errors
✅ Returns consistent JSON response format
✅ Includes error codes for client-side handling
✅ Includes timestamps for audit trail

