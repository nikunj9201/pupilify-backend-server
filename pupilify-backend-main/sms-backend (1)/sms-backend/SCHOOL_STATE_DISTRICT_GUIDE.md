# School-State-District Connection Implementation Guide

## ✅ COMPLETED

Your SmartSchool API now supports connecting schools to states and districts. Here's what has been implemented:

---

## 📦 What Was Added

### 1. **Database Schema Updates**
- Added `state_id` column to `schools` table
- Added `district_id` column to `schools` table
- Added foreign key constraints
- Added indexes for faster queries

### 2. **Entity Updates**
- `School.java` now includes:
  - `@ManyToOne` relationship with `State`
  - `@ManyToOne` relationship with `District`

### 3. **Repository Updates**
- `SchoolRepository.java` includes:
  - `findByStateId(Long stateId)` - Find schools by state
  - `findByDistrictId(Long districtId)` - Find schools by district

### 4. **Service Layer**
- `SchoolService.java` includes:
  - `getSchoolsByState(Long stateId)`
  - `getSchoolsByDistrict(Long districtId)`
  - `assignStateToSchool(Long schoolId, Long stateId)`
  - `assignDistrictToSchool(Long schoolId, Long districtId)`

### 5. **Controller Endpoints**
- `SchoolController.java` includes new endpoints:
  - `PUT /api/superadmin/schools/{schoolId}/assign-state/{stateId}`
  - `PUT /api/superadmin/schools/{schoolId}/assign-district/{districtId}`
  - `GET /api/superadmin/schools/by-state/{stateId}`
  - `GET /api/superadmin/schools/by-district/{districtId}`

---

## 🚀 How to Use

### Step 1: Run Database Migration

Open your MySQL client and execute the SQL commands from `SQL_MIGRATION_GUIDE.md`:

```bash
# First, create the state and district
# (Already done via API if you used StateController and DistrictController)

# Then run the migration SQL to add columns to schools table
ALTER TABLE schools ADD COLUMN state_id BIGINT NULL;
ALTER TABLE schools ADD COLUMN district_id BIGINT NULL;
# ... (see SQL_MIGRATION_GUIDE.md for complete SQL)
```

### Step 2: Start Your Backend

```bash
cd C:\smart-school-pro\sms-backend\sms-backend
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
```

### Step 3: Use the APIs

#### **Create/Manage States and Districts**
```
POST /api/superadmin/state/create
Body: { "name": "Madhya Pradesh", "code": "MP" }

POST /api/district/create
Body: { "name": "Indore", "code": "IND", "state": { "id": 1 } }
```

#### **Assign State to School**
```
PUT /api/superadmin/schools/{schoolId}/assign-state/{stateId}
```

Example:
```
PUT http://localhost:8080/api/superadmin/schools/5/assign-state/1
Response: {
  "message": "State assigned successfully",
  "schoolId": 5,
  "stateId": 1,
  "stateName": "Madhya Pradesh"
}
```

#### **Assign District to School**
```
PUT /api/superadmin/schools/{schoolId}/assign-district/{districtId}
```

Example:
```
PUT http://localhost:8080/api/superadmin/schools/5/assign-district/2
Response: {
  "message": "District assigned successfully",
  "schoolId": 5,
  "districtId": 2,
  "districtName": "Indore"
}
```

#### **Get All Schools by State (For State Manager)**
```
GET /api/superadmin/schools/by-state/{stateId}
```

Example:
```
GET http://localhost:8080/api/superadmin/schools/by-state/1
Response: [
  { "id": 5, "schoolName": "ABC School", "state": {...}, "district": {...} },
  { "id": 6, "schoolName": "XYZ School", "state": {...}, "district": {...} }
]
```

#### **Get All Schools by District (For District Manager)**
```
GET /api/superadmin/schools/by-district/{districtId}
```

Example:
```
GET http://localhost:8080/api/superadmin/schools/by-district/2
Response: [
  { "id": 5, "schoolName": "ABC School", "state": {...}, "district": {...} }
]
```

---

## 📋 Complete API Flow Example

### 1. Create a State
```bash
POST http://localhost:8080/api/superadmin/state/create
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Madhya Pradesh",
  "code": "MP"
}
```

### 2. Create a District
```bash
POST http://localhost:8080/api/district/create
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Indore",
  "code": "IND",
  "state": { "id": 1 }
}
```

### 3. Create a School
```bash
POST http://localhost:8080/api/superadmin/schools/add
Authorization: Bearer <token>
Content-Type: multipart/form-data

schoolData: {
  "schoolName": "ABC Public School",
  "mailId": "abc@school.com",
  "password": "Pass@123",
  "address": "Indore",
  "phoneNumber": "9876543210"
}
currentYearId: 1
```

### 4. Assign State to School
```bash
PUT http://localhost:8080/api/superadmin/schools/1/assign-state/1
Authorization: Bearer <token>
```

### 5. Assign District to School
```bash
PUT http://localhost:8080/api/superadmin/schools/1/assign-district/1
Authorization: Bearer <token>
```

### 6. Verify - Get Schools by State
```bash
GET http://localhost:8080/api/superadmin/schools/by-state/1
Authorization: Bearer <token>
```

---

## 🔐 Permissions

Currently, all endpoints require a valid JWT token with appropriate roles:
- **State-level endpoints**: SUPER_ADMIN or STATE_ADMIN
- **School endpoints**: SUPER_ADMIN (modify), ADMIN/PRINCIPAL/TEACHER/STUDENT (view)

Future enhancement: Restrict state managers to only see their state's schools, and district managers to only their district's schools.

---

## 📁 Files Modified/Created

### Created:
- `db-migrations/001_add_state_district_to_schools.sql`
- `SQL_MIGRATION_GUIDE.md`
- `postman_collection_school_state_district.json`

### Modified:
- `entity/School.java` - Added state and district fields
- `repository/SchoolRepository.java` - Added query methods
- `service/SchoolService.java` - Added interface methods
- `serviceImpl/SchoolServiceImpl.java` - Implemented service methods
- `controller/SchoolController.java` - Added new endpoints

---

## ✨ What's Next (Optional Enhancements)

1. **Fine-grained Permissions**: Restrict state/district managers to their jurisdictions
2. **Dashboard for Managers**: Show statistics per state/district
3. **Bulk Operations**: Assign multiple schools to state/district at once
4. **Audit Logging**: Track who assigned schools to states/districts
5. **Approval Workflow**: Require approval for school assignments

---

## 🐛 Troubleshooting

### Error: "State not found with ID: X"
- Ensure the state exists before assigning to school
- Create state using: `POST /api/superadmin/state/create`

### Error: "Foreign key constraint fails"
- Run the SQL migration first
- Ensure state_id and district_id columns exist in schools table

### Error: "Unexpected error when getting schools"
- Rebuild the project: `mvn clean compile`
- Restart the backend server

---

## 📞 Support

For issues or questions:
1. Check the error logs in `logs/smartschool.log`
2. Verify database migration ran successfully
3. Ensure JWT token is valid and user has correct role
4. Test endpoints using the Postman collection

---

## Summary

✅ Schools can now be linked to states and districts  
✅ State managers can view schools in their state  
✅ District managers can view schools in their district  
✅ Super admin can manage all schools, states, and districts  

**Your school management system is now hierarchical!**

