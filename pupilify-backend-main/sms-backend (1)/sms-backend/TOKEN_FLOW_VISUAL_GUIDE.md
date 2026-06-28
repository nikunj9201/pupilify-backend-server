# 🔄 Token Flow - Visual Guide

## Login Flow

```
Frontend/Postman
    │
    ├─── POST /api/auth/login
    │    {
    │      "username": "super_admin@school.com",
    │      "password": "password"
    │    }
    │
    ▼
AuthController.login()
    │
    ├─── AuthService.login()
    │    └─── Check username exists
    │    └─── Check password matches
    │    └─── Check account active
    │    └─── Return AuthResponse
    │
    ├─── JwtUtil.generateToken()
    │    └─── Create JWT token
    │
    ├─── response.setToken(token)
    ├─── response.setMessage("Login successful")
    │
    ▼
HTTP 200 OK
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "role": "ROLE_SUPER_ADMIN",
  "userId": 1,
  "name": "Super Admin",
  "message": "Login successful"
}
    │
    ▼
Frontend stores token in localStorage/sessionStorage
```

---

## Token Refresh Flow

```
Frontend
    │
    ├─── POST /api/auth/refresh-token
    │    Header: Authorization: Bearer <old_token>
    │
    ▼
AuthController.refreshToken()
    │
    ├─── Check Authorization header exists
    │
    ├─── Extract token from "Bearer " prefix
    │    old_token = Authorization.substring(7)
    │
    ├─── JwtUtil.extractUsername(old_token)
    │    └─── Validate token signature
    │    └─── Extract username
    │
    ├─── JwtUtil.generateToken(username)
    │    └─── Create new JWT token
    │
    ├─── response.setToken(new_token)
    ├─── response.setMessage("Token refreshed successfully")
    │
    ▼
HTTP 200 OK
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "message": "Token refreshed successfully"
}
    │
    ▼
Frontend updates stored token with new token
```

---

## Using Token in Protected Endpoints

```
Frontend
    │
    ├─── GET /api/protected-endpoint
    │    Header: Authorization: Bearer <token>
    │    Header: Content-Type: application/json
    │
    ▼
Spring Security Filter Chain
    │
    ├─── JwtAuthenticationFilter
    │    └─── Extract token from Authorization header
    │    └─── Validate token signature
    │    └─── Check token expiry
    │    └─── Extract username from token
    │    └─── Load user from database
    │    └─── Set SecurityContext
    │
    ▼
Request reaches Controller
    │
    ├─── @PreAuthorize("hasRole('SUPER_ADMIN')")
    │    └─── Check user role
    │
    ├─── Process request
    │
    ▼
HTTP 200 OK
{
  "data": "..."
}
```

---

## Error Flow - Invalid Login

```
Frontend
    │
    ├─── POST /api/auth/login
    │    {
    │      "username": "nonexistent@school.com",
    │      "password": "wrong_password"
    │    }
    │
    ▼
AuthController.login()
    │
    ├─── AuthService.login()
    │    └─── Find user by username
    │    └─── User not found! ❌
    │    └─── Throw RuntimeException
    │
    ├─── Catch RuntimeException
    │
    ▼
HTTP 401 UNAUTHORIZED
{
  "error": "User not found!",
  "message": "Authentication failed"
}
    │
    ▼
Frontend shows error message to user
```

---

## Error Flow - Invalid Token

```
Frontend
    │
    ├─── POST /api/auth/refresh-token
    │    Header: Authorization: Bearer invalid_token
    │
    ▼
AuthController.refreshToken()
    │
    ├─── Check Authorization header ✅
    │
    ├─── JwtUtil.extractUsername(invalid_token)
    │    └─── Token signature invalid ❌
    │    └─── Throw Exception
    │
    ├─── Catch Exception
    │
    ▼
HTTP 401 UNAUTHORIZED
{
  "error": "Invalid or expired token",
  "message": "..."
}
    │
    ▼
Frontend should redirect to login page
```

---

## Token Structure (JWT)

```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdXBlcl9hZG1pbkBzY2hvb2wuY29tIiwiaWF0IjoxNzc5MTIxNzI0LCJleXAiOjE3ODI3MTM3MjR9.xxx
│                   │                                                     │
│                   │                                                     │
Header              Payload                                               Signature
│                   │                                                     │
├─ Algorithm        ├─ Subject (username)                                 └─ HMAC-SHA256
│  "HS256"          ├─ Issued At (iat)                                      (signed with secret key)
│                   └─ Expiration (exp)
└─ Type
   "JWT"
```

---

## Token Lifecycle

```
Time: T0
  │
  ├─── User logs in
  │    POST /api/auth/login
  │    │
  │    └─── Get token: ABC123
  │         Expiry: T0 + 1 hour
  │
  ▼
Time: T0 + 30 min
  │
  ├─── User is active
  │    Token still valid
  │    Use token in requests
  │
  ▼
Time: T0 + 59 min (before expiry)
  │
  ├─── User wants to stay logged in
  │    POST /api/auth/refresh-token
  │    │
  │    └─── Get new token: DEF456
  │         Expiry: T0 + 59 min + 1 hour = T0 + 119 min
  │
  ▼
Time: T0 + 119 min
  │
  ├─── Old token expired
  │    But user has new token
  │
  ▼
Time: T0 + 180 min
  │
  ├─── New token expired
  │    User not authenticated
  │    POST /api/auth/login again
  │
```

---

## Response Headers

```
HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
Content-Length: 456
Date: Fri, 19 May 2026 15:30:45 GMT
Server: Apache Tomcat/10.1.19

{
  "token": "...",
  "message": "Login successful"
}
```

---

## Request Headers (Protected Endpoint)

```
GET /api/protected-endpoint HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
User-Agent: Mozilla/5.0
```

---

**Created:** May 19, 2026
**Status:** ✅ Complete

