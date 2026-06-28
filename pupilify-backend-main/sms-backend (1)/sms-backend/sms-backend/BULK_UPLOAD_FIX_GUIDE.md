# 🔧 BULK RESULTS UPLOAD - HTTP 500 FIX GUIDE

## समस्या (Problem)
```
API: POST /api/admin/results/upload-bulk?schoolId=1&examScheduleId=5&academicYearId=3
Status: 500 Internal Server Error
Exception: org.springframework.http.converter.HttpMessageConversionException

Request Body:
[{studentId: 1, teacherId: 1, marksObtainedTheory: 50, marksObtainedPractical: 0, isAbsent: false}]
```

---

## 🎯 Root Cause Analysis

### Common Reasons for HttpMessageConversionException:

1. **Missing Content-Type Header** ❌
   - You're sending JSON but header says `Content-Type: text/plain` or is missing
   - Spring can't deserialize without proper Content-Type

2. **Invalid JSON Format** ❌
   - Field names must be in double quotes
   - Boolean values must be lowercase: `true`/`false`
   - Numbers must NOT be quoted

3. **Wrong Data Types** ❌
   - `studentId` and `teacherId` must be NUMBERS, not strings
   - `marksObtainedTheory` must be INTEGER
   - `isAbsent` must be BOOLEAN

4. **Missing Required Fields** ❌
   - `studentId` is required
   - `teacherId` is required

---

## ✅ SOLUTION: Correct JSON Format

### WRONG ❌
```json
[{studentId: 1, teacherId: 1, marksObtainedTheory: 50, marksObtainedPractical: 0, isAbsent: false}]
```
Problems:
- Field names not quoted
- Numbers are bare (some JSON parsers may reject this)

### CORRECT ✅
```json
[
  {
    "studentId": 1,
    "teacherId": 1,
    "marksObtainedTheory": 50,
    "marksObtainedPractical": 0,
    "isAbsent": false
  }
]
```

---

## 🚀 How to Test Locally

### Using cURL (PowerShell)
```powershell
$json = @(
    @{
        studentId = 1
        teacherId = 1
        marksObtainedTheory = 50
        marksObtainedPractical = 0
        isAbsent = $false
    }
) | ConvertTo-Json

curl -X POST "http://localhost:8080/api/admin/results/upload-bulk?schoolId=1&examScheduleId=5&academicYearId=3" `
  -H "Content-Type: application/json" `
  -H "Authorization: Bearer <YOUR_TOKEN>" `
  -d $json
```

### Using Postman
1. **Method**: POST
2. **URL**: `http://localhost:8080/api/admin/results/upload-bulk?schoolId=1&examScheduleId=5&academicYearId=3`
3. **Headers**:
   - `Content-Type: application/json` ← **IMPORTANT**
   - `Authorization: Bearer <token>`
4. **Body** (raw, JSON):
```json
[
  {
    "studentId": 1,
    "teacherId": 1,
    "marksObtainedTheory": 50,
    "marksObtainedPractical": 0,
    "isAbsent": false
  }
]
```
5. **Click Send**

---

## 📋 ExamResultRequest DTO Structure

```java
{
  "studentId": Long,              // ✅ Required (number)
  "teacherId": Long,              // ✅ Required (number)
  "isAbsent": boolean,            // Optional (default: false)
  "marksObtainedTheory": Integer, // ✅ Required if isAbsent=false (0–100 typically)
  "marksObtainedPractical": Integer // Required if subject.hasPractical = true
}
```

---

## 📊 Validation Rules (Service-Side)

| Condition | Rule |
|-----------|------|
| `isAbsent = true` | marks ignored, grade="AB", passed=false |
| `isAbsent = false` | `marksObtainedTheory` must be non-null |
| Subject has practical | `marksObtainedPractical` must be non-null |
| Theory marks | Must be 0 ≤ marks ≤ subject.totalTheoryMarks |
| Practical marks | Must be 0 ≤ marks ≤ subject.totalPracticalMarks |

---

## 🔍 What I Fixed (Code Changes)

### 1. Added JSON Parsing Error Handler
**File**: `src/main/java/com/smartschool/api/exception/GlobalExceptionHandler.java`

```java
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
```

**Benefit**: Now when JSON is invalid, you get a `400 Bad Request` with detailed error message instead of generic 500.

### 2. Added Input Validation & Logging
**File**: `src/main/java/com/smartschool/api/controller/ExamResultController.java`

```java
@PostMapping("/upload-bulk")
public ResponseEntity<ApiResponse<Object>> uploadBulkResults(
        @RequestParam Long schoolId,
        @RequestParam Long examScheduleId,
        @RequestParam Long academicYearId,
        @RequestBody List<ExamResultRequest> requests) {
    try {
        // 🚩 Input validation
        if (requests == null || requests.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Request list is empty"));
        }
        
        log.info("📤 Received bulk upload request: schoolId={}, examScheduleId={}, academicYearId={}, count={}",
                schoolId, examScheduleId, academicYearId, requests.size());
        
        // Log first request for debugging
        if (!requests.isEmpty()) {
            ExamResultRequest first = requests.get(0);
            log.info("   First request: studentId={}, teacherId={}, isAbsent={}, theory={}, practical={}",
                    first.getStudentId(), first.getTeacherId(), first.isAbsent(),
                    first.getMarksObtainedTheory(), first.getMarksObtainedPractical());
        }
        
        List<ExamResult> saved = resultService.uploadBulkResults(
                schoolId, examScheduleId, academicYearId, requests);
        
        Map<String, Object> res = new HashMap<>();
        res.put("savedCount", saved.size());
        res.put("message", saved.size() + " students ke marks save ho gaye");
        log.info("✅ Bulk upload successful: {} records saved", saved.size());
        return ResponseEntity.ok(ApiResponse.success("Upload complete", res));
    } catch (Exception e) {
        log.error("❌ Bulk upload failed: {}", e.getMessage(), e);
        return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
    }
}
```

**Benefit**: 
- Empty list check prevents null pointer
- Detailed logs help diagnose issues
- Error messages show what went wrong

---

## 🛠️ Next Steps

### 1. Rebuild & Restart
```bash
mvn -DskipTests=true clean package
mvn spring-boot:run
```

### 2. Test with Correct JSON
Use the Postman example above or curl command.

### 3. Check Server Logs
Look for lines like:
```
📤 Received bulk upload request: schoolId=1, examScheduleId=5, academicYearId=3, count=1
   First request: studentId=1, teacherId=1, isAbsent=false, theory=50, practical=0
✅ Bulk upload successful: 1 records saved
```

### 4. If Still Getting 500
**Check**:
- Is examScheduleId=5 valid in database?
- Is academicYearId=3 valid?
- Does student with ID=1 exist in schoolId=1?
- Is teacherId=1 valid?

If valid params, paste the full server error log and I'll fix it.

---

## 📝 Complete Example (Multi-Student)

```json
[
  {
    "studentId": 1,
    "teacherId": 1,
    "isAbsent": false,
    "marksObtainedTheory": 45,
    "marksObtainedPractical": 12
  },
  {
    "studentId": 2,
    "teacherId": 1,
    "isAbsent": false,
    "marksObtainedTheory": 52,
    "marksObtainedPractical": 18
  },
  {
    "studentId": 3,
    "teacherId": 1,
    "isAbsent": true
  }
]
```

**Expected Response (200 OK)**:
```json
{
  "success": true,
  "message": "Upload complete",
  "data": {
    "savedCount": 3,
    "message": "3 students ke marks save ho gaye"
  }
}
```

---

## 🎓 Key Takeaways

✅ Always send `Content-Type: application/json`
✅ Use proper JSON syntax (quoted field names, lowercase booleans)
✅ Match data types (numbers NOT quoted, booleans NOT quoted)
✅ Check server logs when debugging
✅ Validate IDs exist in database before uploading

---

## 📞 Still Having Issues?

1. Open IDE console / server logs
2. Paste the full ERROR stack trace
3. Tell me:
   - What is your exact request JSON?
   - What are the values of schoolId, examScheduleId, academicYearId?
   - Do these IDs exist in your database?

I'll diagnose the exact issue.

