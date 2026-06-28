# 📚 Authentication & Token - Complete Documentation Index

## 🎯 Quick Navigation

### For Developers
- **[Quick Token Guide](./QUICK_TOKEN_GUIDE.md)** - Start here! (Hindi/English)
- **[Login & Refresh Token Guide](./LOGIN_AND_REFRESH_TOKEN_GUIDE.md)** - Detailed documentation
- **[Token Flow Visual Guide](./TOKEN_FLOW_VISUAL_GUIDE.md)** - Flow diagrams
- **[Token Fix Summary](./TOKEN_LOGIN_FIX_SUMMARY.md)** - What was fixed

### For Testing
- **[postman_auth_login_refresh.json](./postman_auth_login_refresh.json)** - Postman collection
- **[postman_department_login_enhanced.json](./postman_department_login_enhanced.json)** - Department login tests

### For Frontend Integration
- [Login & Refresh Token Guide](./LOGIN_AND_REFRESH_TOKEN_GUIDE.md) → Section: "Frontend Implementation Example"
- [Token Flow Visual Guide](./TOKEN_FLOW_VISUAL_GUIDE.md) → Section: "Using Token in Protected Endpoints"

---

## 📋 What Was Fixed

### Issue
Super Admin login endpoint was not returning token properly to frontend.

### Root Cause
- Token was being generated but error handling was catching exceptions and returning generic messages
- Message field was not being set to indicate success
- No logging to help debug issues

### Solution
1. ✅ Improved error handling with specific error messages
2. ✅ Added logging for debugging
3. ✅ Ensured token is always set in response
4. ✅ Set proper HTTP status codes (200, 401, 400, 500)
5. ✅ Added validation checks

---

## 🔧 Files Modified

### Core Authentication Files
| File | Changes | Impact |
|------|---------|--------|
| `AuthController.java` | `/api/auth/login` endpoint improved | Token now properly returned |
| `AuthController.java` | `/api/auth/refresh-token` endpoint improved | Token refresh works reliably |
| `AuthResponse.java` | Added school details fields | Full school info in responses |
| `AuthServiceImpl.java` | `departmentLogin()` populated with school fields | Department login enhanced |

---

## 🚀 API Endpoints

### 1. Normal User Login
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "super_admin@school.com",
  "password": "your_password"
}

Response: 200 OK
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "role": "ROLE_SUPER_ADMIN",
  "userId": 1,
  "message": "Login successful"
}
```

### 2. Refresh Token
```
POST /api/auth/refresh-token
Authorization: Bearer <current_token>

Response: 200 OK
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "message": "Token refreshed successfully"
}
```

### 3. Manager Login
```
POST /api/auth/manager-login
Content-Type: application/json

{
  "username": "state_manager@email.com",
  "password": "your_password"
}

Response: 200 OK
{
  "id": 5,
  "email": "state_manager@email.com",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "message": "Login successful"
}
```

### 4. Department Login
```
POST /api/department-login/login
Content-Type: application/json

{
  "username": "fees@gmail.com",
  "password": "your_password"
}

Response: 200 OK
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": { ... },
  "department": { ... },
  "school": { ... },
  "academicYear": { ... },
  "message": "Login successful"
}
```

---

## 📊 Supported User Roles

| Role | Login Endpoint | Returns |
|------|---|---|
| ROLE_SUPER_ADMIN | `/api/auth/login` | userId, name, role, token |
| ROLE_ADMIN (Principal) | `/api/auth/login` | userId, schoolId, schoolName, schoolLogo, academicYear, academicYearId, token |
| ROLE_TEACHER | `/api/auth/login` | userId, teacherId, schoolId, schoolName, className, subjectExpertise, academicYear, academicYearId, token |
| ROLE_STUDENT | `/api/auth/login` | userId, studentId, schoolId, schoolName, className, sectionName, rollNumber, academicYear, academicYearId, token |
| ROLE_DEPARTMENT | `/api/department-login/login` | userId, departmentId, department, schoolId, schoolName, school (full), academicYear, academicYearId, token |
| ROLE_STATE_MANAGER | `/api/auth/manager-login` | id, email, name, token |
| ROLE_DISTRICT_MANAGER | `/api/auth/manager-login` | id, email, name, token |

---

## 🧪 Testing Checklist

### Manual Testing (Postman)
- [ ] Import `postman_auth_login_refresh.json`
- [ ] Run "Super Admin Login" 
  - [ ] Check response has `token` field
  - [ ] Check `message` = "Login successful"
  - [ ] Check status code = 200
- [ ] Run "Refresh Token" (uses saved token)
  - [ ] Check response has new `token` field
  - [ ] Check `message` = "Token refreshed successfully"
  - [ ] Check status code = 200
- [ ] Run "Login - Invalid Credentials"
  - [ ] Check status code = 401
  - [ ] Check error message is shown

### Unit Testing (Optional)
```java
@Test
public void testLoginReturnsToken() {
    LoginRequest request = new LoginRequest();
    request.setUsername("super_admin@school.com");
    request.setPassword("password");
    
    ResponseEntity<?> response = authController.login(request);
    
    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(((AuthResponse)response.getBody()).getToken());
    assertEquals("Login successful", 
               ((AuthResponse)response.getBody()).getMessage());
}
```

---

## 🔒 Security Best Practices

1. **Store Token Safely**
   - ❌ Don't store in localStorage (vulnerable to XSS)
   - ✅ Use httpOnly cookies in production
   - ✅ Use sessionStorage for SPAs (cleared on close)

2. **Token Expiry**
   - Default: 1 hour
   - Always refresh before expiry
   - Handle 401 responses by redirecting to login

3. **HTTPS Only**
   - Always use HTTPS in production
   - Never send tokens over HTTP

4. **Token Validation**
   - Always validate token signature
   - Check expiration time
   - Validate user still exists and is active

---

## 📞 Troubleshooting

### Issue: "Token is null"
**Solutions:**
1. Check user exists in database
2. Verify password is correct
3. Ensure user account is active
4. Check server logs for errors

### Issue: "Invalid token on refresh"
**Solutions:**
1. Token may have expired - login again
2. Use correct format: `Bearer <token>`
3. Verify token wasn't tampered with
4. Check server time is synchronized

### Issue: "401 on protected endpoint"
**Solutions:**
1. Token may have expired - refresh it
2. Authorization header format: `Bearer <token>`
3. User role may not have permission
4. Check @PreAuthorize annotations

### Issue: "Can't extract username from token"
**Solutions:**
1. Verify token is not corrupted
2. Check JwtUtil.extractUsername() implementation
3. Ensure same secret key is used for signing
4. Check token format matches expectations

---

## 🔄 Token Lifecycle Example

```
Time: 15:00:00
└─ User logs in
   └─ GET token ABC123
   └─ Expiry: 16:00:00

Time: 15:30:00
└─ User is active
└─ Use token ABC123

Time: 15:55:00
└─ User calls refresh
└─ GET token DEF456
└─ Expiry: 16:55:00

Time: 16:00:00
└─ Old token ABC123 expired
└─ But user has DEF456 (still valid)

Time: 16:55:00
└─ Token DEF456 expired
└─ User must login again
```

---

## 📚 Related Documentation

- [Department Login Enhanced Response](./postman_department_login_enhanced.json)
- [School-State-District Guide](./SCHOOL_STATE_DISTRICT_GUIDE.md)
- [Department Login Guide](./DEPARTMENT_LOGIN_GUIDE.md)
- [Error Handling Guide](./ERROR_HANDLING_GUIDE.md)

---

## ✅ Implementation Status

| Component | Status | Files |
|-----------|--------|-------|
| Login Endpoint | ✅ Complete | AuthController.java |
| Refresh Token | ✅ Complete | AuthController.java |
| Error Handling | ✅ Enhanced | AuthController.java |
| Logging | ✅ Added | AuthController.java |
| Documentation | ✅ Complete | This file + guides |
| Testing Collection | ✅ Ready | postman_auth_login_refresh.json |

---

## 🎓 For New Team Members

**Start Here:**
1. Read [Quick Token Guide](./QUICK_TOKEN_GUIDE.md) (5 min read)
2. Review [Token Flow Visual Guide](./TOKEN_FLOW_VISUAL_GUIDE.md) (10 min read)
3. Import Postman collection and test endpoints (10 min)
4. Read [Login & Refresh Token Guide](./LOGIN_AND_REFRESH_TOKEN_GUIDE.md) (20 min)
5. Check [Token Fix Summary](./TOKEN_LOGIN_FIX_SUMMARY.md) for details (10 min)

**Total Time:** ~55 minutes to understand complete auth flow

---

## 📞 Questions?

Check these in order:
1. [Login & Refresh Token Guide](./LOGIN_AND_REFRESH_TOKEN_GUIDE.md) - Common issues section
2. [Token Fix Summary](./TOKEN_LOGIN_FIX_SUMMARY.md) - What was fixed
3. [Token Flow Visual Guide](./TOKEN_FLOW_VISUAL_GUIDE.md) - Visual explanations
4. [Quick Token Guide](./QUICK_TOKEN_GUIDE.md) - Quick reference

---

**Last Updated:** May 19, 2026
**Version:** 1.0
**Status:** ✅ COMPLETE & TESTED


