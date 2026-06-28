# Department Management API Guide

## Overview
यह guide सभी Department Management APIs को explain करता है।

---

## 1. DEPARTMENT MANAGEMENT APIs

### 1.1 Create Department
**Endpoint:** `POST /api/departments/create`
**Header:** `Content-Type: application/json`
**Body:**
```json
{
  "school": {
    "id": 1
  },
  "deptName": "Admission Department",
  "deptType": "ADMISSION"
}
```
**Response:**
```json
{
  "id": 1,
  "school": {
    "id": 1
  },
  "deptName": "Admission Department",
  "deptType": "ADMISSION",
  "createdAt": "2026-05-09T10:30:00"
}
```

### 1.2 Get Departments by School
**Endpoint:** `GET /api/departments/school/{schoolId}`
**Response:** List of departments

### 1.3 Get Department by ID
**Endpoint:** `GET /api/departments/{id}`

### 1.4 Get Department by Type
**Endpoint:** `GET /api/departments/type/{schoolId}/{deptType}`
**Example:** `GET /api/departments/type/1/ADMISSION`

### 1.5 Update Department
**Endpoint:** `PUT /api/departments/{id}`
**Body:** Updated department details

### 1.6 Delete Department
**Endpoint:** `DELETE /api/departments/{id}`

---

## 2. DEPARTMENT HEAD MANAGEMENT APIs

### 2.1 Assign Department Head
**Endpoint:** `POST /api/departments/{deptId}/assign-head`
**Body:**
```json
{
  "user": {
    "id": 5
  },
  "department": {
    "id": 1
  }
}
```

### 2.2 Get Department Head
**Endpoint:** `GET /api/departments/{deptId}/head`

### 2.3 Remove Department Head
**Endpoint:** `DELETE /api/departments/{deptId}/head`

---

## 3. PERMISSION MANAGEMENT APIs

### 3.1 Add Permission to Department
**Endpoint:** `POST /api/departments/{deptId}/permissions`
**Body:**
```json
{
  "permissionName": "STUDENT_ENROLLMENT",
  "description": "Permission to enroll students",
  "isActive": true
}
```

### 3.2 Get All Permissions
**Endpoint:** `GET /api/departments/{deptId}/permissions`

### 3.3 Get Active Permissions
**Endpoint:** `GET /api/departments/{deptId}/permissions/active`

### 3.4 Update Permission
**Endpoint:** `PUT /api/departments/permissions/{permId}`

### 3.5 Delete Permission
**Endpoint:** `DELETE /api/departments/permissions/{permId}`

---

## 4. DEPARTMENT LOGIN APIs

### 4.1 Department Login
**Endpoint:** `POST /api/department-login/login`
**Body:**
```json
{
  "username": "admissionhead@school.com",
  "password": "password123"
}
```
**Response:**
```json
{
  "success": true,
  "token": "Bearer_uuid_user_5_dept_1",
  "user": {
    "id": 5,
    "username": "admissionhead@school.com",
    "role": "TEACHER"
  },
  "department": {
    "id": 1,
    "name": "Admission Department",
    "type": "ADMISSION",
    "schoolId": 1
  },
  "message": "Login successful"
}
```

### 4.2 Verify Token
**Endpoint:** `POST /api/department-login/verify-token`
**Header:** `Authorization: Bearer <token>`

### 4.3 Get Current User Info
**Endpoint:** `GET /api/department-login/me`
**Header:** `Department-Id: 1`

### 4.4 Logout
**Endpoint:** `POST /api/department-login/logout`

---

## 5. ADMISSION DEPARTMENT APIs

### 5.1 Enroll Student
**Endpoint:** `POST /api/admission/enroll-student`
**Header:** `Department-Id: 1`
**Body:**
```json
{
  "firstName": "Rajesh",
  "lastName": "Kumar",
  "email": "rajesh@email.com",
  "phoneNumber": "9876543210",
  "school": {
    "id": 1
  }
}
```

### 5.2 Get Enrolled Students
**Endpoint:** `GET /api/admission/students/{schoolId}`
**Header:** `Department-Id: 1`

### 5.3 Get Student Details
**Endpoint:** `GET /api/admission/student/{studentId}`
**Header:** `Department-Id: 1`

### 5.4 Update Student Details
**Endpoint:** `PUT /api/admission/student/{studentId}`
**Header:** `Department-Id: 1`

### 5.5 Print ID Card
**Endpoint:** `POST /api/admission/print-id-card/{studentId}`
**Header:** `Department-Id: 1`
**Response:**
```json
{
  "message": "ID Card generated successfully",
  "studentId": 10,
  "studentName": "Rajesh Kumar",
  "schoolName": "School Name",
  "printDate": "2026-05-09T10:30:00",
  "status": "READY_TO_PRINT"
}
```

### 5.6 Admission Dashboard
**Endpoint:** `GET /api/admission/dashboard/{schoolId}`
**Header:** `Department-Id: 1`
**Response:**
```json
{
  "totalStudents": 250,
  "recentEnrollments": 15,
  "pendingApprovals": 3,
  "students": [...]
}
```

---

## 6. EXAM DEPARTMENT APIs

### 6.1 Upload Marks
**Endpoint:** `POST /api/exam/upload-marks`
**Header:** `Department-Id: 2`
**Body:**
```json
{
  "student": {
    "id": 10
  },
  "examSchedule": {
    "id": 1
  },
  "marks": 85,
  "subject": "Mathematics"
}
```

### 6.2 Get Exam Marks
**Endpoint:** `GET /api/exam/marks/{examId}`
**Header:** `Department-Id: 2`

### 6.3 Verify Marks
**Endpoint:** `GET /api/exam/verify-marks/{studentId}`
**Header:** `Department-Id: 2`

### 6.4 Verify Result
**Endpoint:** `PUT /api/exam/verify-result/{resultId}`
**Header:** `Department-Id: 2`

### 6.5 Print Result
**Endpoint:** `POST /api/exam/print-result/{studentId}`
**Header:** `Department-Id: 2`
**Response:**
```json
{
  "message": "Result card generated successfully",
  "studentId": 10,
  "totalMarks": 425,
  "totalExams": 5,
  "percentage": "85.00",
  "printDate": "2026-05-09T10:30:00",
  "status": "READY_TO_PRINT",
  "results": [...]
}
```

### 6.6 Get Report Card
**Endpoint:** `GET /api/exam/report-card/{studentId}`
**Header:** `Department-Id: 2`
**Response:**
```json
{
  "studentId": 10,
  "totalMarks": 425,
  "percentage": "85.00",
  "grade": "A",
  "totalExams": 5,
  "results": [...],
  "generatedDate": "2026-05-09T10:30:00"
}
```

### 6.7 Exam Dashboard
**Endpoint:** `GET /api/exam/dashboard/{schoolId}`
**Header:** `Department-Id: 2`

---

## 7. BUS DEPARTMENT APIs

### 7.1 Add Bus
**Endpoint:** `POST /api/bus-dept/add-bus`
**Header:** `Department-Id: 3`
**Body:**
```json
{
  "busName": "Bus 1",
  "busNumber": "UP-01-AB-0001",
  "capacity": 50,
  "driverName": "Mr. Singh",
  "driverPhone": "9876543210",
  "school": {
    "id": 1
  }
}
```

### 7.2 Get All Buses
**Endpoint:** `GET /api/bus-dept/buses/{schoolId}`
**Header:** `Department-Id: 3`

### 7.3 Get Bus Details
**Endpoint:** `GET /api/bus-dept/bus/{busId}`
**Header:** `Department-Id: 3`

### 7.4 Update Bus
**Endpoint:** `PUT /api/bus-dept/bus/{busId}`
**Header:** `Department-Id: 3`

### 7.5 Delete Bus
**Endpoint:** `DELETE /api/bus-dept/bus/{busId}`
**Header:** `Department-Id: 3`

### 7.6 Assign Student to Bus
**Endpoint:** `POST /api/bus-dept/assign-student/{studentId}/{busId}`
**Header:** `Department-Id: 3`
**Body:**
```json
{
  "student": {
    "id": 10
  },
  "bus": {
    "id": 1
  }
}
```

### 7.7 Get Bus Students
**Endpoint:** `GET /api/bus-dept/bus-students/{busId}`
**Header:** `Department-Id: 3`

### 7.8 Unassign Student
**Endpoint:** `POST /api/bus-dept/unassign-student/{assignmentId}`
**Header:** `Department-Id: 3`

### 7.9 Bus Dashboard
**Endpoint:** `GET /api/bus-dept/dashboard/{schoolId}`
**Header:** `Department-Id: 3`
**Response:**
```json
{
  "totalBuses": 5,
  "totalStudents": 250,
  "activeBuses": 5,
  "buses": [...]
}
```

---

## Headers Required

सभी department-specific APIs के लिए ये headers required हैं:

| Header | Value | Example |
|--------|-------|---------|
| `Department-Id` | Department ID | `1` |
| `Authorization` | JWT Token | `Bearer <token>` |
| `Content-Type` | JSON | `application/json` |

---

## Error Responses

### Unauthorized Access
```json
{
  "error": "Access denied. Only Admission Department can enroll students"
}
```

### Not Found
```json
{
  "error": "Department not found"
}
```

### Bad Request
```json
{
  "error": "schoolId is required"
}
```

---

## Department Types

| Type | Description | Permissions |
|------|-------------|-------------|
| ADMISSION | Student enrollment, ID cards | STUDENT_ENROLLMENT, ID_CARD_PRINT |
| EXAM | Marks & Results | MARKS_UPLOAD, MARKS_VERIFY, RESULT_PRINT |
| BUS | Bus & Transport | BUS_MANAGEMENT, STUDENT_ASSIGNMENT |

---

## Implementation Checklist

- [x] Department Entity & Repository
- [x] Department Head Management
- [x] Permissions System
- [x] Department Login
- [x] Admission Department APIs
- [x] Exam Department APIs
- [x] Bus Department APIs
- [ ] JWT Token Implementation (Production)
- [ ] Database Migrations
- [ ] Unit Tests
- [ ] Integration Tests

