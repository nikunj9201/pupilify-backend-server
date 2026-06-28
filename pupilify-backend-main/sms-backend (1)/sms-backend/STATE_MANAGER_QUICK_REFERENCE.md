# 🎯 State Manager - Quick Reference Card

## ⚡ Copy-Paste Ready Test Data

### State Manager 1 - Rajesh Kumar
```
ID: 1 (auto-generated after create)
Name: Rajesh Kumar
Email: rajesh@state.com
Password: rajesh@123
State ID: 1
Role: STATE_MANAGER
Active: true
```

### State Manager 2 - Priya Singh
```
ID: 2 (auto-generated after create)
Name: Priya Singh
Email: priya.singh@state.com
Password: priya@123
State ID: 2
Role: STATE_MANAGER
Active: true
```

### State Manager 3 - Amit Patel
```
ID: 3 (auto-generated after create)
Name: Amit Patel
Email: amit.patel@state.com
Password: amit@123
State ID: 3
Role: STATE_MANAGER
Active: true
```

---

## 📋 Step-by-Step Process

### Step 1: Get Super Admin Token (REQUIRED FIRST)
```json
POST http://localhost:8080/api/auth/login

{
  "username": "admin@school.com",
  "password": "password"
}

Response will contain "token": "xxxx"
Copy this token!
```

### Step 2: Create State Manager #1
```json
POST http://localhost:8080/api/superadmin/state-managers/create

Headers:
Authorization: Bearer {paste_token_here}
Content-Type: application/json

Body:
{
  "name": "Rajesh Kumar",
  "email": "rajesh@state.com",
  "password": "rajesh@123",
  "stateId": 1,
  "role": "STATE_MANAGER"
}

Response: id = 1
```

### Step 3: Create State Manager #2
```json
POST http://localhost:8080/api/superadmin/state-managers/create

Headers:
Authorization: Bearer {paste_token_here}
Content-Type: application/json

Body:
{
  "name": "Priya Singh",
  "email": "priya.singh@state.com",
  "password": "priya@123",
  "stateId": 2,
  "role": "STATE_MANAGER"
}

Response: id = 2
```

### Step 4: Create State Manager #3
```json
POST http://localhost:8080/api/superadmin/state-managers/create

Headers:
Authorization: Bearer {paste_token_here}
Content-Type: application/json

Body:
{
  "name": "Amit Patel",
  "email": "amit.patel@state.com",
  "password": "amit@123",
  "stateId": 3,
  "role": "STATE_MANAGER"
}

Response: id = 3
```

### Step 5: Login as Rajesh Kumar
```json
POST http://localhost:8080/api/auth/manager-login

Headers:
Content-Type: application/json

Body:
{
  "username": "rajesh@state.com",
  "password": "rajesh@123"
}

Response:
{
  "id": 1,
  "email": "rajesh@state.com",
  "role": "STATE_MANAGER",
  "name": "Rajesh Kumar",
  "active": true,
  "success": true,
  "message": "Login successful"
}
```

### Step 6: Login as Priya Singh
```json
POST http://localhost:8080/api/auth/manager-login

Headers:
Content-Type: application/json

Body:
{
  "username": "priya.singh@state.com",
  "password": "priya@123"
}

Response:
{
  "id": 2,
  "email": "priya.singh@state.com",
  "role": "STATE_MANAGER",
  "name": "Priya Singh",
  "active": true,
  "success": true,
  "message": "Login successful"
}
```

### Step 7: Login as Amit Patel
```json
POST http://localhost:8080/api/auth/manager-login

Headers:
Content-Type: application/json

Body:
{
  "username": "amit.patel@state.com",
  "password": "amit@123"
}

Response:
{
  "id": 3,
  "email": "amit.patel@state.com",
  "role": "STATE_MANAGER",
  "name": "Amit Patel",
  "active": true,
  "success": true,
  "message": "Login successful"
}
```

---

## 🛠️ Postman Setup (2 Minutes)

1. Open Postman
2. File → Import
3. Select `postman_state_manager.json`
4. Set variables:
   - baseUrl: `http://localhost:8080`
   - superAdminToken: (leave blank, will fill after login)
5. Run requests in order:
   - 1. Super Admin Login → Copy token to superAdminToken variable
   - 2-4. Create 3 State Managers
   - 5-7. Login as each State Manager

---

## ✅ Checklist

- [ ] Application running on port 8080
- [ ] Super Admin user exists (admin@school.com)
- [ ] Got JWT token from Super Admin login
- [ ] Created State Manager 1 (Rajesh)
- [ ] Created State Manager 2 (Priya)
- [ ] Created State Manager 3 (Amit)
- [ ] Logged in as Rajesh Kumar
- [ ] Logged in as Priya Singh
- [ ] Logged in as Amit Patel

---

## 🔑 Login Credentials (Final)

| Name | Email | Password | State |
|------|-------|----------|-------|
| Rajesh Kumar | rajesh@state.com | rajesh@123 | 1 |
| Priya Singh | priya.singh@state.com | priya@123 | 2 |
| Amit Patel | amit.patel@state.com | amit@123 | 3 |

---

## 💡 Tips

- Each State Manager manages one State ID
- Email is unique - cannot create 2 managers with same email
- Password is encrypted in database
- Active = true means can login
- Active = false means cannot login
- No need for any token to login as State Manager

---

## 📞 If You Get Error

| Error | Solution |
|-------|----------|
| "Unauthorized" | Make sure you have valid Super Admin token |
| "Email already exists" | Use different email |
| "Invalid email or password" | Check credentials exactly |
| "stateId is required" | Include stateId in request body |
| "No qualifying bean" | Application might not be running |

---

**Ready to create State Managers? Start with Step 1! 🚀**

