# Department Login Implementation Guide

## Overview
Aapke project me ab department login functionality add ho gaya hai. Jab principal department create karega, uska user ID aur password `user` table me automatically save hoga aur department apne department ID se login kar sakta hai.

## What Was Updated

### 1. **User Entity** (`User.java`)
Added two new fields:
```java
@Column(nullable = true)
private String department; // Department name

@Column(nullable = true)
private Long departmentId; // Department ID for login
```

### 2. **UserRepository** (`UserRepository.java`)
Added department-related query methods:
```java
Optional<User> findByDepartmentId(Long departmentId);
Optional<User> findByDepartment(String department);
List<User> findBySchoolIdAndDepartment(Long schoolId, String department);
```

### 3. **AuthService Interface** (`AuthService.java`)
Added department login method:
```java
AuthResponse departmentLogin(Map<String, String> loginData);
```

### 4. **AuthServiceImpl** (`AuthServiceImpl.java`)
Implemented department login logic with:
- Department ID validation
- Password verification
- Token generation
- Department user details retrieval

### 5. **AuthResponse DTO** (`AuthResponse.java`)
Added department fields:
```java
private Long departmentId;
private String department;
```

### 6. **AuthController** (`AuthController.java`)
Added new endpoint:
```
POST /api/auth/department-login
```

## Database Migration

SQL migration file created: `004_add_department_to_users.sql`

Run this to add columns to your database:
```sql
ALTER TABLE users ADD COLUMN department VARCHAR(255) NULL;
ALTER TABLE users ADD COLUMN department_id BIGINT NULL;
CREATE INDEX idx_department_id ON users(department_id);
CREATE INDEX idx_department ON users(department);
ALTER TABLE users ADD CONSTRAINT uq_department_id UNIQUE(department_id);
```

## How to Use

### 1. **Create Department with User**

When principal creates a department, ensure the following:

```java
// Example: Creating a department and its user
User departmentUser = new User();
departmentUser.setUsername("dept_admin@school.com"); // Unique email
departmentUser.setPassword(passwordEncoder.encode("password123")); // Hashed password
departmentUser.setRole(Role.ROLE_ADMIN); // Or appropriate role
departmentUser.setSchool(school);
departmentUser.setDepartment("Mathematics Department"); // Department name
departmentUser.setDepartmentId(1L); // Unique department ID
departmentUser.setActive(true);

userRepository.save(departmentUser);
```

### 2. **Department Login API**

**Endpoint:** `POST /api/auth/department-login`

**Request Body:**
```json
{
  "departmentId": "1",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "role": "ROLE_ADMIN",
  "userId": 5,
  "departmentId": 1,
  "department": "Mathematics Department",
  "schoolId": 1,
  "schoolName": "Delhi Public School",
  "schoolLogo": "https://...",
  "academicYear": "2025-26",
  "academicYearId": 1,
  "name": "Mathematics Department",
  "active": true,
  "message": null
}
```

### 3. **Sample Postman Collection**

Add this to your Postman collection:

```json
{
  "name": "Department Login",
  "request": {
    "method": "POST",
    "header": [
      {
        "key": "Content-Type",
        "value": "application/json"
      }
    ],
    "body": {
      "mode": "raw",
      "raw": "{\n  \"departmentId\": \"1\",\n  \"password\": \"password123\"\n}"
    },
    "url": {
      "raw": "{{baseUrl}}/api/auth/department-login",
      "host": ["{{baseUrl}}"],
      "path": ["api", "auth", "department-login"]
    }
  }
}
```

## Important Notes

1. **Password Security**: Always hash passwords using `BCryptPasswordEncoder` before saving
2. **Department ID Uniqueness**: Each department should have a unique department ID
3. **Active Status**: Ensure `active` flag is set to `true` for login to work
4. **Token Generation**: JWT token is automatically generated on successful login
5. **School Association**: Department user should be associated with a school

## Testing Steps

1. **Apply Database Migration**
   - Run `004_add_department_to_users.sql` on your database

2. **Create a Test Department**
   - Insert a user record with department and departmentId fields

3. **Test Login**
   - Use the department login endpoint with correct departmentId and password
   - Verify token is generated
   - Check response contains department details

## Error Handling

The endpoint handles these errors:

| Error | HTTP Status | Message |
|-------|-------------|---------|
| Missing Department ID | 400 | "Department ID missing" |
| Missing Password | 400 | "Department ID and Password are required!" |
| Department Not Found | 401 | "Department not found!" |
| Department Inactive | 401 | "This department is inactive. Please contact the Principal/Admin." |
| Wrong Password | 401 | "Invalid Credentials!" |
| Invalid Department ID Format | 401 | "Invalid Department ID format!" |

## Integration with Principal Department Creation

When principal creates a department, your principal creation code should:

1. Create a Department entity (if you have one)
2. Create a User record with:
   - `username`: Unique email
   - `password`: Hashed password
   - `role`: ROLE_ADMIN or appropriate role
   - `school`: Associated school
   - `department`: Department name
   - `departmentId`: Unique department ID
   - `active`: true

## Example SQL Insert

```sql
INSERT INTO users (username, password, role, school_id, department, department_id, active)
VALUES (
  'physics_dept@school.com',
  '$2a$10$... (BCrypt hashed password)',
  'ROLE_ADMIN',
  1,
  'Physics Department',
  2,
  true
);
```

## Security Considerations

1. Always use BCryptPasswordEncoder for password hashing
2. Use JWT tokens for subsequent API calls
3. Validate department_id format (should be numeric)
4. Check active status before allowing login
5. Log all login attempts for audit trail
6. Use HTTPS in production for secure token transmission

## Next Steps

1. Update your department creation logic to save user data
2. Apply the database migration
3. Test department login with Postman
4. Create admin panel for department management
5. Add department-specific endpoints as needed

