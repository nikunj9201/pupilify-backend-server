# 🔥 Quick API Testing Guide

## Copy-Paste Ready URLs & Requests

---

## 1️⃣ CREATE STATE MANAGER

**URL:**
```
POST http://localhost:8080/api/superadmin/state-managers/create
```

**Headers:**
```
Content-Type: application/json
Authorization: Bearer YOUR_TOKEN_HERE
```

**Body:**
```json
{
  "name": "Rajesh Kumar",
  "email": "rajesh@state.com",
  "password": "statepass123",
  "stateId": 1,
  "role": "STATE_MANAGER"
}
```

---

## 2️⃣ STATE MANAGER LOGIN

**URL:**
```
POST http://localhost:8080/api/auth/manager-login
```

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "username": "rajesh@state.com",
  "password": "statepass123"
}
```

**Expected Response:**
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

## 3️⃣ CREATE DISTRICT MANAGER

**URL:**
```
POST http://localhost:8080/api/superadmin/district-managers/create
```

**Headers:**
```
Content-Type: application/json
Authorization: Bearer YOUR_TOKEN_HERE
```

**Body:**
```json
{
  "name": "Priya Singh",
  "email": "priya@district.com",
  "password": "districtpass123",
  "stateId": 1,
  "districtId": 1,
  "role": "DISTRICT_MANAGER"
}
```

---

## 4️⃣ DISTRICT MANAGER LOGIN

**URL:**
```
POST http://localhost:8080/api/auth/manager-login
```

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "username": "priya@district.com",
  "password": "districtpass123"
}
```

**Expected Response:**
```json
{
  "id": 1,
  "email": "priya@district.com",
  "role": "DISTRICT_MANAGER",
  "name": "Priya Singh",
  "active": true,
  "message": "Login successful",
  "success": true
}
```

---

## 5️⃣ GET ALL STATE MANAGERS

**URL:**
```
GET http://localhost:8080/api/superadmin/state-managers/all
```

**Headers:**
```
Authorization: Bearer YOUR_TOKEN_HERE
```

---

## 6️⃣ GET STATE MANAGER BY ID

**URL:**
```
GET http://localhost:8080/api/superadmin/state-managers/1
```

**Headers:**
```
Authorization: Bearer YOUR_TOKEN_HERE
```

---

## 7️⃣ UPDATE STATE MANAGER

**URL:**
```
PUT http://localhost:8080/api/superadmin/state-managers/update/1
```

**Headers:**
```
Content-Type: application/json
Authorization: Bearer YOUR_TOKEN_HERE
```

**Body:**
```json
{
  "name": "Rajesh Kumar Updated",
  "email": "rajesh.updated@state.com",
  "password": "newpass123",
  "active": true
}
```

---

## 8️⃣ DELETE STATE MANAGER

**URL:**
```
DELETE http://localhost:8080/api/superadmin/state-managers/delete/1
```

**Headers:**
```
Authorization: Bearer YOUR_TOKEN_HERE
```

---

## 9️⃣ GET ALL DISTRICT MANAGERS

**URL:**
```
GET http://localhost:8080/api/superadmin/district-managers/all
```

**Headers:**
```
Authorization: Bearer YOUR_TOKEN_HERE
```

---

## 🔟 GET DISTRICT MANAGERS BY STATE

**URL:**
```
GET http://localhost:8080/api/superadmin/district-managers/state/1
```

**Headers:**
```
Authorization: Bearer YOUR_TOKEN_HERE
```

(Replace `1` with actual state ID)

---

## 1️⃣1️⃣ GET DISTRICT MANAGERS BY DISTRICT

**URL:**
```
GET http://localhost:8080/api/superadmin/district-managers/district/1
```

**Headers:**
```
Authorization: Bearer YOUR_TOKEN_HERE
```

(Replace `1` with actual district ID)

---

## 1️⃣2️⃣ UPDATE DISTRICT MANAGER

**URL:**
```
PUT http://localhost:8080/api/superadmin/district-managers/update/1
```

**Headers:**
```
Content-Type: application/json
Authorization: Bearer YOUR_TOKEN_HERE
```

**Body:**
```json
{
  "name": "Priya Singh Updated",
  "email": "priya.updated@district.com",
  "password": "newpass123",
  "active": true
}
```

---

## 1️⃣3️⃣ DELETE DISTRICT MANAGER

**URL:**
```
DELETE http://localhost:8080/api/superadmin/district-managers/delete/1
```

**Headers:**
```
Authorization: Bearer YOUR_TOKEN_HERE
```

---

## 🎯 Testing Order (Recommended)

1. Create State Manager (#1)
2. State Manager Login (#2) → Save response
3. Create District Manager (#3)
4. District Manager Login (#4) → Save response
5. Get All State Managers (#5)
6. Get All District Managers (#9)
7. Get District Managers by State (#10)
8. Update State Manager (#7)
9. Get State Manager by ID (#6)
10. Delete operations (#8, #13) - Last!

---

## 🛠️ Using cURL Commands

### Create State Manager
```bash
curl -X POST http://localhost:8080/api/superadmin/state-managers/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "name": "Rajesh Kumar",
    "email": "rajesh@state.com",
    "password": "statepass123",
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
    "password": "statepass123"
  }'
```

### Get All State Managers
```bash
curl -X GET http://localhost:8080/api/superadmin/state-managers/all \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## ✅ Success Response Format

All successful requests return HTTP 200 with JSON body:

```json
{
  "id": 1,
  "email": "user@example.com",
  "role": "STATE_MANAGER",
  "name": "User Name",
  "active": true,
  "message": "Operation successful",
  "success": true
}
```

---

## ❌ Error Response Format

Failed requests return HTTP 4xx/5xx with error message:

```json
{
  "message": "Invalid email or password",
  "success": false
}
```

---

## 📱 Postman Setup

1. **Import Collection:**
   - File → Import → Select `postman_collection_complete.json`

2. **Set Variables:**
   - Click "Variables" tab
   - Update `baseUrl`: `http://localhost:8080`
   - Update `token`: Your JWT token

3. **Run Requests:**
   - Click any request
   - Click "Send"
   - View response below

---

## 🔒 Get JWT Token

### Option 1: User Login
```
POST /api/auth/login
{
  "username": "admin@school.com",
  "password": "password"
}
```
Response includes `token` field.

### Option 2: Use in tests
For manager endpoints, use token from user login or principal login.

---

## 🐛 Debugging Tips

1. **Check Status Code:**
   - 200 = Success ✅
   - 201 = Created ✅
   - 400 = Bad Request ❌
   - 401 = Unauthorized ❌
   - 404 = Not Found ❌
   - 500 = Server Error ❌

2. **Check Response Body:**
   - Look for "message" field
   - Check "success" flag
   - Verify data fields

3. **Common Mistakes:**
   - Wrong URL (typo in endpoint)
   - Missing Authorization header
   - Invalid JSON in body
   - Wrong HTTP method (POST vs GET)
   - Invalid email/password
   - Manager inactive (active=false)

---

## 📝 Sample Test Workflow

```
# 1. Login as Super Admin
POST /api/auth/login
→ Get JWT token

# 2. Create State Manager (use token)
POST /api/superadmin/state-managers/create
→ Get manager ID

# 3. State Manager can now login
POST /api/auth/manager-login
→ Email: rajesh@state.com

# 4. Create District Manager
POST /api/superadmin/district-managers/create
→ Get district manager ID

# 5. District Manager can now login
POST /api/auth/manager-login
→ Email: priya@district.com

# 6. List all managers
GET /api/superadmin/state-managers/all
GET /api/superadmin/district-managers/all
```

---

## 🎉 Everything Ready!

You now have:
- ✅ 13 API endpoints
- ✅ Complete CRUD operations
- ✅ Manager login system
- ✅ State/District filtering
- ✅ Database schema
- ✅ Ready-to-use Postman collection

**Start testing now! 🚀**

