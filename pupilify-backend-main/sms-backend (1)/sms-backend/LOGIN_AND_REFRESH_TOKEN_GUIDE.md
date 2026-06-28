# Authentication API - Login & Refresh Token Guide

## Overview
This guide explains how to use the login and refresh token endpoints to authenticate users and manage JWT tokens.

---

## 🔑 1. Normal User Login - `/api/auth/login`

### Endpoint
```
POST /api/auth/login
```

### Request Headers
```
Content-Type: application/json
```

### Request Body
```json
{
  "username": "super_admin@school.com",
  "password": "your_password"
}
```

### Success Response (200 OK)
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdXBlcl9hZG1pbkBzY2hvb2wuY29tIiwiaWF0IjoxNzc5MTIxNzI0LCJleHAiOjE3ODI3MTM3MjR9.xxx",
  "role": "ROLE_SUPER_ADMIN",
  "userId": 1,
  "name": "Super Admin",
  "schoolId": null,
  "active": true,
  "message": "Login successful"
}
```

### Error Response (401 Unauthorized)
```json
{
  "error": "Invalid Credentials!",
  "message": "Authentication failed"
}
```

### Supported Roles
- `ROLE_SUPER_ADMIN` - Super Administrator
- `ROLE_ADMIN` - Principal (has schoolId, schoolName, schoolLogo)
- `ROLE_TEACHER` - Teacher (has schoolId, schoolName, academicYear, academicYearId, subjectExpertise)
- `ROLE_STUDENT` - Student (has schoolId, schoolName, className, sectionName, rollNumber, academicYear, academicYearId)
- `ROLE_DEPARTMENT` - Department Head (has schoolId, schoolName, departmentId, department)

---

## 🔄 2. Refresh Token - `/api/auth/refresh-token`

### Endpoint
```
POST /api/auth/refresh-token
```

### Request Headers
```
Content-Type: application/json
Authorization: Bearer <your_current_jwt_token>
```

### Request Body
No body needed - token is in header.

### Success Response (200 OK)
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdXBlcl9hZG1pbkBzY2hvb2wuY29tIiwiaWF0IjoxNzc5MTIxODUwLCJleHAiOjE3ODI3MTM4NTB9.yyy",
  "message": "Token refreshed successfully"
}
```

### Error Response (401 Unauthorized)
```json
{
  "error": "Invalid or expired token",
  "message": "Exception message"
}
```

---

## 📋 3. Manager Login - `/api/auth/manager-login`

### Endpoint
```
POST /api/auth/manager-login
```

### Request Headers
```
Content-Type: application/json
```

### Request Body
```json
{
  "username": "state_manager@email.com",
  "password": "your_password"
}
```

### Success Response (200 OK)
```json
{
  "id": 5,
  "email": "state_manager@email.com",
  "role": "ROLE_STATE_MANAGER",
  "name": "State Manager Name",
  "active": true,
  "message": "Login successful",
  "success": true,
  "token": "eyJhbGciOiJIUzI1NiJ9.xxx"
}
```

---

## 🔐 Frontend Implementation Example

### Login Flow
```javascript
// Step 1: Send login credentials
const response = await fetch('http://localhost:8080/api/auth/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    username: 'super_admin@school.com',
    password: 'password'
  })
});

const data = await response.json();

// Step 2: Store token
if (response.ok) {
  localStorage.setItem('authToken', data.token);
  localStorage.setItem('user', JSON.stringify(data));
  console.log('Login successful!');
} else {
  console.error('Login failed:', data.error);
}
```

### Refresh Token Flow
```javascript
// When token is about to expire or you want a new one
const currentToken = localStorage.getItem('authToken');

const response = await fetch('http://localhost:8080/api/auth/refresh-token', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${currentToken}`
  }
});

const data = await response.json();

if (response.ok) {
  localStorage.setItem('authToken', data.token);
  console.log('Token refreshed!');
} else {
  console.error('Token refresh failed:', data.error);
  // Redirect to login
}
```

### Using Token in Requests
```javascript
// Send authenticated requests with token
const token = localStorage.getItem('authToken');

const response = await fetch('http://localhost:8080/api/some-endpoint', {
  method: 'GET',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  }
});
```

---

## 🧪 Postman Testing

### 1. Login Request
- **Method:** POST
- **URL:** `http://localhost:8080/api/auth/login`
- **Body (raw JSON):**
  ```json
  {
    "username": "super_admin@school.com",
    "password": "your_password"
  }
  ```

### 2. Refresh Token Request
- **Method:** POST
- **URL:** `http://localhost:8080/api/auth/refresh-token`
- **Headers:**
  - `Authorization: Bearer <token_from_login>`

---

## ⚠️ Common Issues & Solutions

### Issue 1: "Token is null"
**Solution:** Make sure the login endpoint is returning a token. Check:
1. User exists in database
2. Password is correct
3. User account is active

### Issue 2: "Invalid token" on refresh
**Solution:** 
1. Ensure token is not expired
2. Use full "Bearer " prefix in Authorization header
3. Token must be from valid login

### Issue 3: 401 Unauthorized on protected endpoints
**Solution:**
1. Token may have expired - use refresh endpoint
2. Token format must be `Bearer <token>`
3. Check if user role has permission for endpoint

---

## 🔄 Token Lifecycle

```
Login
  ↓
Receive JWT Token
  ↓
Use Token in Authorization Header
  ↓
Token Expires (after ~1 hour by default)
  ↓
Call Refresh Token Endpoint
  ↓
Receive New Token
  ↓
Continue Using New Token
```

---

## 📊 Response Fields Explained

### AuthResponse Fields
| Field | Type | Description |
|-------|------|-------------|
| token | String | JWT token to use in Authorization header |
| role | String | User role (ROLE_SUPER_ADMIN, ROLE_ADMIN, etc.) |
| userId | Long | User ID in database |
| name | String | User's name |
| message | String | Success/error message |
| schoolId | Long | School ID (for non-super-admin users) |
| schoolName | String | School name |
| schoolLogo | String | School logo filename |
| active | Boolean | Is user account active |
| academicYear | String | Current academic year (e.g., "2025-26") |
| academicYearId | Long | Academic year ID |

---

**Last Updated:** May 19, 2026

