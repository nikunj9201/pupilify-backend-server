# Department Login - Quick Reference

## 🎯 What Was Done

Added complete department login functionality to your SMS Backend:

1. ✅ User entity updated with `department` and `departmentId` fields
2. ✅ UserRepository methods added for department queries
3. ✅ AuthService interface extended with `departmentLogin()` method
4. ✅ AuthServiceImpl implements department login logic
5. ✅ AuthResponse DTO updated with department fields
6. ✅ AuthController added `/api/auth/department-login` endpoint
7. ✅ Database migration created
8. ✅ Sample data SQL provided

## 🔑 Department Login Endpoint

**URL:** `POST /api/auth/department-login`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "departmentId": "1",
  "password": "Math@123"
}
```

**Response (Success):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "role": "ROLE_ADMIN",
  "userId": 5,
  "departmentId": 1,
  "department": "Mathematics Department",
  "schoolId": 1,
  "schoolName": "Delhi Public School",
  "academicYear": "2025-26",
  "name": "Mathematics Department",
  "active": true
}
```

**Response (Error):**
```json
{
  "message": "Department not found!" / "Invalid Credentials!" / etc
}
```

## 📋 Setup Steps

### Step 1: Database Migration
Run this SQL:
```sql
ALTER TABLE users ADD COLUMN department VARCHAR(255) NULL;
ALTER TABLE users ADD COLUMN department_id BIGINT NULL;
CREATE INDEX idx_department_id ON users(department_id);
ALTER TABLE users ADD CONSTRAINT uq_department_id UNIQUE(department_id);
```

### Step 2: Create Department Users
When creating a department, also create a user record:

```java
User dept = new User();
dept.setUsername("physics@school.com");
dept.setPassword(passwordEncoder.encode("Physics@123")); // IMPORTANT: Hash it!
dept.setRole(Role.ROLE_ADMIN);
dept.setSchool(school);
dept.setDepartment("Physics Department");
dept.setDepartmentId(1L); // Unique ID
dept.setActive(true);
userRepository.save(dept);
```

### Step 3: Test with Postman
```json
{
  "departmentId": "1",
  "password": "Physics@123"
}
```

## 🛡️ Security Notes

- ✅ Passwords are hashed using BCryptPasswordEncoder
- ✅ JWT tokens generated on successful login
- ✅ Department ID must be unique
- ✅ Only active departments can login
- ✅ All login attempts are logged

## 📂 Files Modified/Created

### Modified Files:
- `src/main/java/com/smartschool/api/entity/User.java`
- `src/main/java/com/smartschool/api/repository/UserRepository.java`
- `src/main/java/com/smartschool/api/service/AuthService.java`
- `src/main/java/com/smartschool/api/serviceImpl/AuthServiceImpl.java`
- `src/main/java/com/smartschool/api/dto/AuthResponse.java`
- `src/main/java/com/smartschool/api/controller/AuthController.java`

### Created Files:
- `db-migrations/004_add_department_to_users.sql`
- `DEPARTMENT_LOGIN_GUIDE.md`
- `DEPARTMENT_USER_INSERT.sql`

## 🧪 Testing Checklist

- [ ] Database migration applied successfully
- [ ] New columns exist in users table
- [ ] Can create department with user record
- [ ] Department login endpoint works
- [ ] JWT token received on successful login
- [ ] Invalid credentials rejected
- [ ] Inactive departments rejected
- [ ] Department details returned in response

## ⚡ Quick Test Command

```bash
# Using cURL (replace values with your data)
curl -X POST http://localhost:8080/api/auth/department-login \
  -H "Content-Type: application/json" \
  -d '{
    "departmentId": "1",
    "password": "Math@123"
  }'
```

## 🔗 Related Endpoints

- `POST /api/auth/login` - Regular user login (email/username)
- `POST /api/auth/manager-login` - State/District manager login
- `POST /api/auth/department-login` - Department login (NEW)
- `POST /api/auth/refresh-token` - Refresh JWT token

## 📝 Important Fields

| Field | Type | Required | Notes |
|-------|------|----------|-------|
| departmentId | Long | Yes (in DB) | Unique identifier |
| department | String | Yes | Department name |
| username | String | Yes | Unique email |
| password | String | Yes | BCrypt hashed |
| role | Enum | Yes | ROLE_ADMIN, etc |
| school_id | Long | No | Can be null for some roles |
| active | Boolean | Yes | Default: true |

## 🚨 Common Issues & Solutions

### Issue: "Department not found!"
- Check if departmentId exists in database
- Verify the department_id value is correct
- Run: `SELECT * FROM users WHERE department_id = 1;`

### Issue: "Invalid Credentials!"
- Double-check password (case-sensitive)
- Ensure password is correct when department was created
- Verify it matches the BCrypt hash in database

### Issue: "This department is inactive"
- Check `active` field in users table
- Run: `UPDATE users SET active = true WHERE department_id = 1;`

### Issue: Column not found error
- Database migration might not be applied
- Run: `ALTER TABLE users ADD COLUMN department VARCHAR(255) NULL;`
- Run: `ALTER TABLE users ADD COLUMN department_id BIGINT NULL;`

## 📚 Documentation Files

- `DEPARTMENT_LOGIN_GUIDE.md` - Comprehensive guide
- `DEPARTMENT_USER_INSERT.sql` - Sample data
- `db-migrations/004_add_department_to_users.sql` - Database schema

## ✅ Implementation Status

| Component | Status | Details |
|-----------|--------|---------|
| User Entity | ✅ Done | Added department fields |
| Repository | ✅ Done | Query methods added |
| Service | ✅ Done | Login logic implemented |
| Controller | ✅ Done | Endpoint created |
| DTO | ✅ Done | Fields added |
| Database | ✅ Ready | Migration file ready |
| Documentation | ✅ Done | Complete guide provided |

---

**Ready to use!** Just apply the database migration and start testing.

