# 📖 API REFERENCE - ALL WORKING EXAMPLES

## 1. Fees Due Report (Student Names Now Visible)

### GET /api/admin/fees/due-report/{schoolId}

```bash
curl -X GET "http://localhost:8080/api/admin/fees/due-report/1?academicYearId=2" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"
```

### Response (200 OK)
```json
[
  {
    "enrollmentId": "STU-2026-001",
    "rollNumber": 1,
    "studentName": "Sneha Patidar",
    "className": "class 2",
    "sectionName": "SECTION A",
    "currentYearFees": 25000.0,
    "paidThisYear": 0.0,
    "previousYearDue": 0.0,
    "grandTotalDue": 25000.0,
    "hasPreviousYearDue": false
  }
]
```

---

## 2. Bulk Upload Results (Fixed JSON Format)

### POST /api/admin/results/upload-bulk

**Parameters** (Query String):
- `schoolId` (required)
- `examScheduleId` (required)
- `academicYearId` (required)

**Headers**:
```
Content-Type: application/json
Authorization: Bearer <token>
```

**Body** (Raw JSON):
```json
[
  {
    "studentId": 1,
    "teacherId": 1,
    "isAbsent": false,
    "marksObtainedTheory": 50,
    "marksObtainedPractical": 12
  },
  {
    "studentId": 2,
    "teacherId": 1,
    "isAbsent": false,
    "marksObtainedTheory": 45,
    "marksObtainedPractical": 18
  },
  {
    "studentId": 3,
    "teacherId": 1,
    "isAbsent": true
  }
]
```

### cURL Example
```bash
curl -X POST "http://localhost:8080/api/admin/results/upload-bulk?schoolId=1&examScheduleId=1&academicYearId=2" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGc..." \
  -d '[
    {"studentId": 1, "teacherId": 1, "isAbsent": false, "marksObtainedTheory": 50, "marksObtainedPractical": 12},
    {"studentId": 2, "teacherId": 1, "isAbsent": false, "marksObtainedTheory": 45, "marksObtainedPractical": 18},
    {"studentId": 3, "teacherId": 1, "isAbsent": true}
  ]'
```

### Response (200 OK)
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

### Response (400 Bad Request - Invalid JSON)
```json
{
  "status": 400,
  "message": "Invalid JSON format in request body: Unexpected character...",
  "path": "/api/admin/results/upload-bulk",
  "timestamp": "2026-05-28T15:00:00.000",
  "errorCode": "JSON_PARSE_ERROR"
}
```

---

## 3. Update Student

### PUT /api/admin/students/update/{studentId}

**Headers**:
```
Authorization: Bearer <token>
```

**Body** (Multipart Form Data):
- `studentData` (text) — JSON string of fields to update
- `photo` (file, optional) — student photo
- `marksheet` (file, optional) — last class marksheet
- Other document files (optional)

**Example studentData JSON**:
```json
{
  "phoneNo": "9999999999",
  "address": "New Address, City",
  "fatherName": "Father Name",
  "gender": "M",
  "caste": "SC"
}
```

### cURL Example (PowerShell)
```powershell
$studentJson = @"
{
  "phoneNo": "9999999999",
  "address": "New Address"
}
"@

$form = @{
    studentData = $studentJson
    photo = Get-Item -Path "C:\photo.jpg"
}

Invoke-WebRequest -Uri "http://localhost:8080/api/admin/students/update/1" `
  -Method PUT `
  -Headers @{"Authorization" = "Bearer <token>"} `
  -Form $form
```

### Response (200 OK)
```json
{
  "id": 1,
  "name": "Sneha Patidar",
  "phoneNo": "9999999999",
  "address": "New Address, City",
  "email": "sneha@school.com",
  "enrollmentId": "STU-2026-001",
  "rollNumber": 1,
  "schoolClass": {...},
  "section": {...},
  "studentPhoto": "http://localhost:8080/api/admin/students/files/photo_123.jpg",
  ...
}
```

---

## 4. Get Class Result Sheet

### GET /api/admin/results/class-sheet/{schoolId}/{classId}

**Query Parameters**:
- `sectionId` (optional)
- `examName` (required) — e.g., "Half Yearly"
- `academicYearId` (required)

**Example**:
```bash
curl -X GET "http://localhost:8080/api/admin/results/class-sheet/1/1?sectionId=1&examName=Half%20Yearly&academicYearId=2" \
  -H "Authorization: Bearer <token>"
```

### Response (200 OK)
```json
{
  "schoolName": "SmartSchool",
  "className": "class 2",
  "sectionName": "SECTION A",
  "examName": "Half Yearly",
  "academicYear": "2025-26",
  "subjectNames": ["Mathematics", "English", "Science"],
  "rows": [
    {
      "enrollmentId": "STU-2026-001",
      "rollNumber": 1,
      "studentName": "Sneha Patidar",
      "subjectMarksList": [
        {
          "marksObtainedTheory": 45,
          "totalTheoryMarks": 50,
          "marksObtainedPractical": 12,
          "totalPracticalMarks": 20,
          "totalMarksObtained": 57,
          "totalMaxMarks": 70,
          "grade": "A",
          "isPassed": true,
          "isAbsent": false
        }
      ],
      "totalMarksObtained": 171,
      "totalMaxMarks": 210,
      "percentage": 81.43,
      "grade": "A",
      "isPassed": true,
      "rank": 1
    }
  ],
  "totalStudents": 30,
  "totalPassed": 28,
  "totalFailed": 2
}
```

---

## 5. Get Student Result (by Enrollment ID)

### GET /api/admin/results/by-enrollment/{enrollmentId}/{schoolId}

**Query Parameters**:
- `classId` (optional)
- `sectionId` (optional)
- `examName` (required)
- `academicYearId` (required)

```bash
curl -X GET "http://localhost:8080/api/admin/results/by-enrollment/STU-2026-001/1?examName=Half%20Yearly&academicYearId=2" \
  -H "Authorization: Bearer <token>"
```

### Response (200 OK)
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "studentName": "Sneha Patidar",
    "enrollmentId": "STU-2026-001",
    "rollNumber": 1,
    "className": "class 2",
    "sectionName": "SECTION A",
    "examName": "Half Yearly",
    "academicYear": "2025-26",
    "results": [
      {
        "subjectName": "Mathematics",
        "marksObtainedTheory": 45,
        "totalTheoryMarks": 50,
        "marksObtainedPractical": 12,
        "totalPracticalMarks": 20,
        "totalMarksObtained": 57,
        "totalMaxMarks": 70,
        "percentage": 81.43,
        "grade": "A",
        "isPassed": true
      }
    ],
    "totalMarksObtained": 171,
    "totalMaxMarks": 210,
    "overallPercentage": 81.43,
    "overallGrade": "A",
    "overallPassed": true,
    "subjectsPassed": 3,
    "subjectsFailed": 0,
    "subjectsAbsent": 0
  }
}
```

---

## 🔑 Required Headers (All APIs)

```
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>
```

---

## ⚠️ Common Error Responses

### 400 Bad Request (JSON Parsing Error)
```json
{
  "status": 400,
  "message": "Invalid JSON format in request body: Unexpected character...",
  "errorCode": "JSON_PARSE_ERROR"
}
```
**Fix**: Check JSON syntax (quoted field names, proper boolean format)

### 401 Unauthorized
```json
{
  "status": 401,
  "message": "Unauthorized access attempt",
  "errorCode": "UNAUTHORIZED"
}
```
**Fix**: Provide valid JWT token in Authorization header

### 404 Not Found
```json
{
  "status": 404,
  "message": "Student nahi mila: id=999",
  "errorCode": "RESOURCE_NOT_FOUND"
}
```
**Fix**: Verify ID exists in database

### 500 Internal Server Error
```json
{
  "status": 500,
  "message": "An unexpected error occurred. Please try again later.",
  "errorCode": "INTERNAL_SERVER_ERROR",
  "exception": "..."
}
```
**Fix**: Check server logs for detailed error

---

## 📝 Field Definitions

### ExamResultRequest
```json
{
  "studentId": "Long (required)",
  "teacherId": "Long (required)",
  "isAbsent": "boolean (default: false)",
  "marksObtainedTheory": "Integer (required if isAbsent=false)",
  "marksObtainedPractical": "Integer (required if subject.hasPractical=true)"
}
```

### StudentUpdateRequest
```json
{
  "phoneNo": "String (optional)",
  "address": "String (optional)",
  "fatherName": "String (optional)",
  "motherName": "String (optional)",
  "fatherContactNumber": "String (optional)",
  "aadharCardNo": "String (optional)",
  "samagraId": "String (optional)",
  "rollNumber": "Integer (optional)",
  "gender": "String (optional) - M/F/Other",
  "caste": "String (optional)"
}
```

---

## 🎯 Valid Exam Names (Examples)
- "Half Yearly"
- "Annual"
- "Unit Test 1"
- "Periodic Test"

(Check your database for exact values)

---

**Last Updated**: 2026-05-28
**All Examples Tested**: ✅

