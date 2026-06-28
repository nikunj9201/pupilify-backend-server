# Department Management System - Quick Start Guide

## मुझे सब कुछ implement कर दिया है! 🎉

---

## 📋 क्या बनाया गया है?

### 1️⃣ **Entity Classes**
- ✅ `Department.java` - Department entity
- ✅ `DepartmentType.java` - Enum (ADMISSION, EXAM, BUS)
- ✅ `DepartmentHead.java` - Department head assignment
- ✅ `DepartmentPermission.java` - Permissions system

### 2️⃣ **Repository Classes**
- ✅ `DepartmentRepository.java`
- ✅ `DepartmentHeadRepository.java`
- ✅ `DepartmentPermissionRepository.java`

### 3️⃣ **Service Layer**
- ✅ `DepartmentService.java` - All business logic

### 4️⃣ **Controller Classes**
- ✅ `DepartmentController.java` - Main department APIs
- ✅ `DepartmentLoginController.java` - Department login
- ✅ `AdmissionDepartmentController.java` - Admission APIs
- ✅ `ExamDepartmentController.java` - Exam APIs
- ✅ `BusDepartmentController.java` - Bus APIs

### 5️⃣ **Database Migrations**
- ✅ `005_add_department_management.sql` - SQL schema

### 6️⃣ **Documentation**
- ✅ `DEPARTMENT_API_GUIDE.md` - Complete API documentation
- ✅ `postman_department_management.json` - Postman collection

---

## 🚀 कैसे काम करेगा?

### **Step 1: Database Migration चलाएं**
```bash
# SQL file को अपने database में run करें
c:\smart-school-pro\sms-backend\db-migrations\005_add_department_management.sql
```

### **Step 2: Project Compile करें**
```bash
cd C:\smart-school-pro\sms-backend\sms-backend
.\mvnw.cmd clean compile
```

### **Step 3: Application Start करें**
```bash
.\mvnw.cmd spring-boot:run
```

---

## 📱 API Endpoints Summary

### **Department Management**
```
POST   /api/departments/create
GET    /api/departments/school/{schoolId}
GET    /api/departments/{id}
GET    /api/departments/type/{schoolId}/{deptType}
PUT    /api/departments/{id}
DELETE /api/departments/{id}
```

### **Department Head**
```
POST   /api/departments/{deptId}/assign-head
GET    /api/departments/{deptId}/head
DELETE /api/departments/{deptId}/head
```

### **Permissions**
```
POST   /api/departments/{deptId}/permissions
GET    /api/departments/{deptId}/permissions
GET    /api/departments/{deptId}/permissions/active
PUT    /api/departments/permissions/{permId}
DELETE /api/departments/permissions/{permId}
```

### **Department Login**
```
POST   /api/department-login/login
POST   /api/department-login/verify-token
GET    /api/department-login/me
POST   /api/department-login/logout
```

### **Admission Department**
```
POST   /api/admission/enroll-student        (Department-Id: 1)
GET    /api/admission/students/{schoolId}    (Department-Id: 1)
GET    /api/admission/student/{studentId}    (Department-Id: 1)
PUT    /api/admission/student/{studentId}    (Department-Id: 1)
POST   /api/admission/print-id-card/{studentId}  (Department-Id: 1)
GET    /api/admission/dashboard/{schoolId}   (Department-Id: 1)
```

### **Exam Department**
```
POST   /api/exam/upload-marks              (Department-Id: 2)
GET    /api/exam/marks/{examId}            (Department-Id: 2)
GET    /api/exam/verify-marks/{studentId}  (Department-Id: 2)
PUT    /api/exam/verify-result/{resultId}  (Department-Id: 2)
POST   /api/exam/print-result/{studentId}  (Department-Id: 2)
GET    /api/exam/report-card/{studentId}   (Department-Id: 2)
GET    /api/exam/dashboard/{schoolId}      (Department-Id: 2)
```

### **Bus Department**
```
POST   /api/bus-dept/add-bus                   (Department-Id: 3)
GET    /api/bus-dept/buses/{schoolId}          (Department-Id: 3)
GET    /api/bus-dept/bus/{busId}               (Department-Id: 3)
PUT    /api/bus-dept/bus/{busId}               (Department-Id: 3)
DELETE /api/bus-dept/bus/{busId}               (Department-Id: 3)
POST   /api/bus-dept/assign-student/{studentId}/{busId}  (Department-Id: 3)
GET    /api/bus-dept/bus-students/{busId}     (Department-Id: 3)
POST   /api/bus-dept/unassign-student/{assignmentId}     (Department-Id: 3)
GET    /api/bus-dept/dashboard/{schoolId}     (Department-Id: 3)
```

---

## 📄 Postman Collection

Postman में import करने के लिए:
1. Postman खोलें
2. **File → Import**
3. `postman_department_management.json` select करें
4. सभी APIs को test करें

---

## 🔐 Authorization Headers

सभी Department APIs के लिए ये headers भेजने होंगे:

```
Header Name: Department-Id
Header Value: {department_id}

Example:
Department-Id: 1  (Admission)
Department-Id: 2  (Exam)
Department-Id: 3  (Bus)
```

---

## 🎯 Workflow Example

### **Admission Department Setup**

**1. Department बनाएं:**
```bash
POST /api/departments/create
{
  "school": { "id": 1 },
  "deptName": "Admission Department",
  "deptType": "ADMISSION"
}
# Response: { "id": 1, ... }
```

**2. Teacher को Department Head assign करें:**
```bash
POST /api/departments/1/assign-head
{
  "user": { "id": 5 },  // Teacher ID
  "department": { "id": 1 }
}
```

**3. Permissions add करें:**
```bash
POST /api/departments/1/permissions
{
  "permissionName": "STUDENT_ENROLLMENT",
  "description": "Permission to enroll students",
  "isActive": true
}
```

**4. Department Head login करे:**
```bash
POST /api/department-login/login
{
  "username": "teacher@school.com",
  "password": "password123"
}
# Response: { "token": "...", "department": { "id": 1, "type": "ADMISSION", ... } }
```

**5. Student enroll करें:**
```bash
POST /api/admission/enroll-student
Headers: {
  "Department-Id": "1",
  "Authorization": "Bearer <token>"
}
{
  "firstName": "Rajesh",
  "lastName": "Kumar",
  "email": "rajesh@email.com",
  "phoneNumber": "9876543210",
  "school": { "id": 1 }
}
```

**6. ID Card print करें:**
```bash
POST /api/admission/print-id-card/{studentId}
Headers: { "Department-Id": "1" }
# Response: { "status": "READY_TO_PRINT", ... }
```

---

## 📊 Exam Department Example

**1. Teacher marks upload करे:**
```bash
POST /api/exam/upload-marks
Headers: { "Department-Id": "2" }
{
  "student": { "id": 10 },
  "marks": 85,
  "subject": "Mathematics"
}
```

**2. Department Head result verify करे:**
```bash
PUT /api/exam/verify-result/{resultId}
Headers: { "Department-Id": "2" }
```

**3. Result print करे:**
```bash
POST /api/exam/print-result/{studentId}
Headers: { "Department-Id": "2" }
# Response: { "percentage": "85.00", "grade": "A", ... }
```

---

## 🚌 Bus Department Example

**1. Bus add करें:**
```bash
POST /api/bus-dept/add-bus
Headers: { "Department-Id": "3" }
{
  "busName": "Bus 1",
  "busNumber": "UP-01-AB-0001",
  "capacity": 50,
  "driverName": "Mr. Singh",
  "driverPhone": "9876543210"
}
```

**2. Student को bus assign करें:**
```bash
POST /api/bus-dept/assign-student/{studentId}/{busId}
Headers: { "Department-Id": "3" }
```

**3. Bus dashboard देखें:**
```bash
GET /api/bus-dept/dashboard/{schoolId}
Headers: { "Department-Id": "3" }
# Response: { "totalBuses": 5, "totalStudents": 250, ... }
```

---

## 🛠️ Implementation Checklist

### Already Done ✅
- [x] Department entity और repository
- [x] Department head management
- [x] Permission system
- [x] Department login
- [x] Admission department APIs
- [x] Exam department APIs
- [x] Bus department APIs
- [x] Database schema
- [x] Postman collection

### Todo (Production में)
- [ ] JWT token implementation (jjwt library)
- [ ] Email verification
- [ ] Audit logging
- [ ] Unit tests
- [ ] Integration tests
- [ ] API rate limiting

---

## 📝 Database Schema

### departments
```
id (PK)
school_id (FK)
dept_name
dept_type (ENUM)
created_at
updated_at
```

### department_heads
```
id (PK)
department_id (FK)
user_id (FK)
assigned_date
```

### department_permissions
```
id (PK)
department_id (FK)
permission_name
description
is_active
```

---

## 🔧 Configuration Files

अगर कोई error आए तो check करें:

### `pom.xml`
```xml
<!-- Sभी dependencies automatically install होंगी -->
```

### `application.properties`
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smartschool
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
```

---

## ❓ Frequently Asked Questions

### Q: Department के लिए कितने users create करूँ?
A: हर department के लिए एक department head (teacher) होना चाहिए जो उस department को manage करे।

### Q: क्या एक teacher multiple departments का head हो सकता है?
A: नहीं, एक teacher एक ही department का head हो सकता है।

### Q: Department delete करने से क्या होगा?
A: Department delete होने पर सभी associated department heads और permissions भी delete हो जाएंगे।

### Q: Token कितने समय तक valid है?
A: अभी 24 hours (production में configure कर सकते हैं)।

---

## 🎓 Next Steps

1. **Database migrate करें** - SQL file run करें
2. **Application start करें** - mvnw.cmd spring-boot:run
3. **Postman में test करें** - collection import करें
4. **Production deployment** - JWT और security implement करें

---

## 📞 Support

अगर कोई error आए:
1. `DEPARTMENT_API_GUIDE.md` देखें (detailed API docs)
2. `postman_department_management.json` में examples देखें
3. Database schema check करें - `005_add_department_management.sql`

---

## ✅ Ready to Go!

सभी files create हो गई हैं। अब बस:
1. Database migrate करो
2. Application चलाओ
3. APIs test करो
4. Enjoy! 🚀

**Happy Coding! 💻**

