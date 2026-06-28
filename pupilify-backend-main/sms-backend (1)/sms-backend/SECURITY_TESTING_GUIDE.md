# 🧪 TESTING THE SECURITY CONFIG FIX - POSTMAN GUIDE

## ⚡ QUICK START

### Step 1: Get JWT Token
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "Login successful"
}
```

### Step 2: Copy the Token
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Step 3: Test State Create
```
POST http://localhost:8080/api/superadmin/state/create
Authorization: Bearer <YOUR_TOKEN>
Content-Type: application/json

{
  "name": "Maharashtra",
  "code": "MH"
}

Response: ✅ 200 OK
{
  "id": 1,
  "name": "Maharashtra",
  "code": "MH"
}
```

---

## 📋 COMPLETE TEST SUITE

### Test 1: CREATE STATE ✅
```bash
curl -X POST http://localhost:8080/api/superadmin/state/create \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Karnataka",
    "code": "KA"
  }'
```

**Expected:** 200 OK with state object

---

### Test 2: GET ALL STATES ✅
```bash
curl -X GET http://localhost:8080/api/superadmin/state/all \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected:** 200 OK with array of states

---

### Test 3: GET STATE BY ID ✅
```bash
curl -X GET http://localhost:8080/api/superadmin/state/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected:** 200 OK with single state object

---

### Test 4: UPDATE STATE ✅
```bash
curl -X PUT http://localhost:8080/api/superadmin/state/update/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Karnataka Updated",
    "code": "KA"
  }'
```

**Expected:** 200 OK with updated state

---

### Test 5: DELETE STATE ✅
```bash
curl -X DELETE http://localhost:8080/api/superadmin/state/delete/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected:** 200 OK with success message

---

## 🔐 SECURITY TESTS

### Test 6: Request WITHOUT Token ❌ (Should Fail)
```bash
curl -X GET http://localhost:8080/api/superadmin/state/all
```

**Expected:** 401 Unauthorized

---

### Test 7: Request WITH Invalid Token ❌ (Should Fail)
```bash
curl -X GET http://localhost:8080/api/superadmin/state/all \
  -H "Authorization: Bearer invalid_token_xyz"
```

**Expected:** 401 Unauthorized

---

### Test 8: Request WITH Wrong Role ❌ (Should Fail)
If user has `STUDENT` role but tries to access `SUPER_ADMIN` endpoint:

```bash
curl -X GET http://localhost:8080/api/superadmin/state/all \
  -H "Authorization: Bearer STUDENT_JWT_TOKEN"
```

**Expected:** 403 Forbidden

---

## 📊 POSTMAN COLLECTION FORMAT

```json
{
  "info": {
    "name": "State Management - Security Test",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "1. Login (Get Token)",
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
          "raw": "{\n  \"username\": \"admin\",\n  \"password\": \"password123\"\n}"
        },
        "url": {
          "raw": "http://localhost:8080/api/auth/login",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "auth", "login"]
        }
      }
    },
    {
      "name": "2. Create State",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Authorization",
            "value": "Bearer {{token}}"
          },
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"name\": \"Maharashtra\",\n  \"code\": \"MH\"\n}"
        },
        "url": {
          "raw": "http://localhost:8080/api/superadmin/state/create",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "superadmin", "state", "create"]
        }
      }
    },
    {
      "name": "3. Get All States",
      "request": {
        "method": "GET",
        "header": [
          {
            "key": "Authorization",
            "value": "Bearer {{token}}"
          }
        ],
        "url": {
          "raw": "http://localhost:8080/api/superadmin/state/all",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "superadmin", "state", "all"]
        }
      }
    },
    {
      "name": "4. Get State By ID",
      "request": {
        "method": "GET",
        "header": [
          {
            "key": "Authorization",
            "value": "Bearer {{token}}"
          }
        ],
        "url": {
          "raw": "http://localhost:8080/api/superadmin/state/1",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "superadmin", "state", "1"]
        }
      }
    },
    {
      "name": "5. Update State",
      "request": {
        "method": "PUT",
        "header": [
          {
            "key": "Authorization",
            "value": "Bearer {{token}}"
          },
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"name\": \"Maharashtra Updated\",\n  \"code\": \"MH\"\n}"
        },
        "url": {
          "raw": "http://localhost:8080/api/superadmin/state/update/1",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "superadmin", "state", "update", "1"]
        }
      }
    },
    {
      "name": "6. Delete State",
      "request": {
        "method": "DELETE",
        "header": [
          {
            "key": "Authorization",
            "value": "Bearer {{token}}"
          }
        ],
        "url": {
          "raw": "http://localhost:8080/api/superadmin/state/delete/1",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "superadmin", "state", "delete", "1"]
        }
      }
    }
  ]
}
```

---

## 🎯 EXPECTED RESULTS

### All Tests Should Pass ✅

| Test | Method | Endpoint | Auth | Expected Status | Notes |
|------|--------|----------|------|-----------------|-------|
| Login | POST | /api/auth/login | ❌ No | 200 OK | Returns JWT token |
| Create State | POST | /api/superadmin/state/create | ✅ Yes | 200 OK | Creates new state |
| Get All | GET | /api/superadmin/state/all | ✅ Yes | 200 OK | Returns list |
| Get By ID | GET | /api/superadmin/state/1 | ✅ Yes | 200 OK | Returns one state |
| Update | PUT | /api/superadmin/state/update/1 | ✅ Yes | 200 OK | Updates state |
| Delete | DELETE | /api/superadmin/state/delete/1 | ✅ Yes | 200 OK | Deletes state |

---

## 🚨 TROUBLESHOOTING

### Problem 1: Still Getting "No static resource" Error
**Solution:**
1. Clean and rebuild project: `mvn clean compile`
2. Stop and restart Spring Boot application
3. Clear browser cache (if using browser)
4. Check that URL has `/api/` prefix

### Problem 2: Getting 401 Unauthorized
**Solution:**
1. Make sure you have a valid JWT token
2. Check that token is not expired
3. Copy token correctly (without extra spaces)
4. Format header correctly: `Authorization: Bearer TOKEN`

### Problem 3: Getting 403 Forbidden
**Solution:**
1. Check user role in database
2. Ensure user has required role (SUPER_ADMIN, STATE_ADMIN, or DISTRICT_ADMIN)
3. Verify role in SecurityConfig matches user role

### Problem 4: Getting 404 Not Found
**Solution:**
1. Check URL spelling
2. Verify endpoint exists in controller
3. Check HTTP method (POST, GET, PUT, DELETE)
4. Ensure state ID exists for GET/PUT/DELETE operations

---

## 🔍 DEBUG CHECKLIST

Before reporting an issue, verify:

- [ ] Application is running on localhost:8080
- [ ] Database is connected and accessible
- [ ] JWT token is valid and not expired
- [ ] User has correct role assigned
- [ ] Request URL has `/api/` prefix
- [ ] HTTP method is correct (POST, GET, PUT, DELETE)
- [ ] Headers are properly formatted
- [ ] JSON body is valid (for POST/PUT)
- [ ] SecurityConfig.java is modified correctly
- [ ] Application restarted after code changes

---

## 📝 POSTMAN ENVIRONMENT SETUP

Create a Postman environment with:

```json
{
  "name": "State Management",
  "values": [
    {
      "key": "base_url",
      "value": "http://localhost:8080",
      "enabled": true
    },
    {
      "key": "token",
      "value": "",
      "enabled": true
    },
    {
      "key": "state_id",
      "value": "1",
      "enabled": true
    }
  ]
}
```

Then use in requests:
```
{{base_url}}/api/superadmin/state/create
Authorization: Bearer {{token}}
```

---

## ✅ VERIFICATION COMPLETE

After running all tests:

```
✅ Authentication working
✅ Authorization working
✅ Create endpoint working
✅ Read endpoints working
✅ Update endpoint working
✅ Delete endpoint working
✅ Security restrictions enforced
✅ CORS enabled
✅ JWT validation working

STATUS: 🎉 ALL SYSTEMS GO!
```

---

**Created:** May 19, 2026
**Purpose:** Validate Security Config Fix
**Status:** ✅ READY TO TEST

🚀 Start testing now!


