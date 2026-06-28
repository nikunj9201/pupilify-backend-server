# State Manager - Create & Login Complete Guide

## 📋 Step 1: Create State Manager (Super Admin Only)

### API Endpoint
```
POST http://localhost:8080/api/superadmin/state-managers/create
```

### Headers Required
```
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN
```

### Request Body
```json
{
  "name": "Rajesh Kumar",
  "email": "rajesh@state.com",
  "password": "rajesh@123",
  "stateId": 1,
  "role": "STATE_MANAGER"
}
```

### Response (Success - 200)
```json
{
  "id": 1,
  "name": "Rajesh Kumar",
  "email": "rajesh@state.com",
  "password": "rajesh@123",
  "role": "STATE_MANAGER",
  "stateId": 1,
  "schoolId": null,
  "active": true,
  "createdAt": 1713520800000,
  "updatedAt": 1713520800000
}
```

---

## 🔐 Step 2: Login as State Manager

### API Endpoint
```
POST http://localhost:8080/api/auth/manager-login
```

### Headers Required
```
Content-Type: application/json
```

### Request Body
```json
{
  "username": "rajesh@state.com",
  "password": "rajesh@123"
}
```

### Response (Success - 200)
```json
{
  "id": 1,
  "email": "rajesh@state.com",
  "role": "STATE_MANAGER",
  "name": "Rajesh Kumar",
  "active": true,
  "message": "Login successful",
  "success": true
}
```

---

## 🧪 Test Data Ready to Use

### State Manager 1
```
Name: Rajesh Kumar
Email: rajesh@state.com
Password: rajesh@123
State ID: 1
Role: STATE_MANAGER
```

### State Manager 2
```
Name: Priya Singh
Email: priya.singh@state.com
Password: priya@123
State ID: 2
Role: STATE_MANAGER
```

### State Manager 3
```
Name: Amit Patel
Email: amit.patel@state.com
Password: amit@123
State ID: 3
Role: STATE_MANAGER
```

---

## 📝 Step-by-Step Instructions

### 1️⃣ First, Get Super Admin Token
```
POST /api/auth/login

Body:
{
  "username": "admin@school.com",
  "password": "password"
}

Save the token from response
```

### 2️⃣ Create State Manager Using Token
```
POST /api/superadmin/state-managers/create

Headers:
Authorization: Bearer {token_from_step_1}
Content-Type: application/json

Body:
{
  "name": "Rajesh Kumar",
  "email": "rajesh@state.com",
  "password": "rajesh@123",
  "stateId": 1,
  "role": "STATE_MANAGER"
}

Save the ID from response (will be 1, 2, 3, etc.)
```

### 3️⃣ Login as State Manager
```
POST /api/auth/manager-login

Headers:
Content-Type: application/json

Body:
{
  "username": "rajesh@state.com",
  "password": "rajesh@123"
}

Success! You're logged in as State Manager
```

---

## 🚀 Using cURL Commands

### Create State Manager
```bash
curl -X POST http://localhost:8080/api/superadmin/state-managers/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "Rajesh Kumar",
    "email": "rajesh@state.com",
    "password": "rajesh@123",
    "stateId": 1,
    "role": "STATE_MANAGER"
  }'
```

### State Manager Login
```bash
curl -X POST http://localhost:8080/api/auth/manager-login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "rajesh@state.com",
    "password": "rajesh@123"
  }'
```

---

## 📱 Postman Setup

1. **Create new request → POST**
2. **URL:** `http://localhost:8080/api/superadmin/state-managers/create`
3. **Headers:**
   - `Content-Type: application/json`
   - `Authorization: Bearer {your_token}`
4. **Body (raw JSON):**
```json
{
  "name": "Rajesh Kumar",
  "email": "rajesh@state.com",
  "password": "rajesh@123",
  "stateId": 1,
  "role": "STATE_MANAGER"
}
```
5. **Click Send**
6. **Save response ID**

---

## ✅ Response Codes

| Code | Meaning |
|------|---------|
| 200 | Success |
| 400 | Bad Request |
| 401 | Unauthorized (Token issue) |
| 403 | Forbidden (Not Super Admin) |
| 500 | Server Error |

---

## 🔑 Login Credentials

### To Create State Manager, You Need:
- Super Admin JWT Token (from `/api/auth/login`)
- Super Admin must be logged in

### To Login as State Manager:
- Email: `rajesh@state.com`
- Password: `rajesh@123`
- No token needed (public endpoint)

---

## 📊 Database Fields Reference

```
id              → Auto generated (1, 2, 3, ...)
name            → Manager's full name
email           → Unique email for login
password        → Password (will be encrypted)
role            → Always "STATE_MANAGER"
stateId         → Which state they manage (1, 2, 3, ...)
schoolId        → Optional (null by default)
active          → true = can login, false = cannot login
createdAt       → Auto generated timestamp
updatedAt       → Auto generated timestamp
```

---

## 🎯 Common Issues

**Issue:** "Unauthorized" error on create
**Solution:** Make sure you're logged in as Super Admin first and have valid token

**Issue:** "Email already exists" error
**Solution:** Use a different email - each State Manager must have unique email

**Issue:** "Invalid email or password" on login
**Solution:** Check email and password are exactly correct (case-sensitive)

**Issue:** "stateId is required"
**Solution:** Include `stateId` in request body with valid state ID

---

## ✨ Next Steps

1. Create 2-3 State Managers using the guide above
2. Login as each State Manager to verify
3. Then you can create District Managers for each State

**Ready to start? Copy-paste the requests above in Postman!** 🚀

