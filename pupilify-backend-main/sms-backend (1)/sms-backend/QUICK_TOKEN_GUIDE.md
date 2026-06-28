# 🚀 Quick Start - Login & Refresh Token

## Super Admin Login करने के लिए

### Step 1: Login API को call करें
```
POST http://localhost:8080/api/auth/login

Body (JSON):
{
  "username": "super_admin@school.com",
  "password": "your_password"
}
```

### Step 2: Response में Token मिलेगा
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "role": "ROLE_SUPER_ADMIN",
  "userId": 1,
  "name": "Super Admin",
  "message": "Login successful"
}
```

### Step 3: Token को frontend में store करो
```javascript
// Postman में automatically save हो जाएगा
// JavaScript में:
localStorage.setItem('authToken', data.token);
```

---

## Token Refresh करने के लिए

### Step 1: Refresh Token API को call करें
```
POST http://localhost:8080/api/auth/refresh-token

Header:
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Step 2: Naya Token मिलेगा
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "message": "Token refreshed successfully"
}
```

---

## ✅ क्या Fix किया गया?

### 1. Token अब properly return हो रहा है
- Login के बाद token response में जाएगा
- Token को frontend भेजेगा

### 2. Message field सेट होगी
- "Login successful" message आएगा

### 3. Refresh Token काम कर रहा है
- Expired token को नया token से replace कर सकते हो

### 4. Error handling बेहतर है
- Clear error messages मिलेंगे

---

## 🧪 Postman से Test करें

1. **postman_auth_login_refresh.json** को import करो
2. "Super Admin Login" को run करो
3. Token automatically save हो जाएगा
4. "Refresh Token" को run करो
5. Token refresh हो जाएगा

---

## Frontend में use करो

```javascript
// Login
const res = await fetch('http://localhost:8080/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    username: 'super_admin@school.com',
    password: 'password'
  })
});

const data = await res.json();
const token = data.token; // ✅ Token यहाँ मिलेगा

// दूसरे API calls में token भेजो
const apiRes = await fetch('http://localhost:8080/api/some-endpoint', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});
```

---

## ⚠️ अगर Token नहीं मिल रहा तो

1. ✅ Username और password सही हो
2. ✅ User database में exist करता हो
3. ✅ User account active हो
4. ✅ Server running हो

---

**Status:** ✅ Ready to Use
**Date:** May 19, 2026

