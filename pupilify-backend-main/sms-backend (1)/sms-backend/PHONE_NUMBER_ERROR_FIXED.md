# ✅ phone_number Column Error - FIXED

## ❌ Error You Had
```
Field 'phone_number' doesn't have a default value
could not execute statement [Field 'phone_number' doesn't have a default value] 
[insert into state_managers (...) values (...)]
```

## 🔍 Root Cause
Your database table `state_managers` had a `phone_number` column, but:
1. The Java entity didn't have this field
2. The database column doesn't allow NULL and has no default value
3. When trying to insert, Hibernate didn't include phone_number, causing error

## ✅ Solution Applied

### 1. Added phone_number Field to StateManager Entity
```java
@Column(name = "phone_number")
private String phoneNumber;
```

### 2. Added phone_number Field to DistrictManager Entity
```java
@Column(name = "phone_number")
private String phoneNumber;
```

### 3. Created SQL Migration to Fix Database
```sql
ALTER TABLE state_managers 
MODIFY phone_number VARCHAR(20) NULL;

ALTER TABLE district_managers
MODIFY phone_number VARCHAR(20) NULL;
```

---

## 📁 Files Modified

### StateManager.java
- Added `phoneNumber` field
- Column name: `phone_number`
- Type: String (optional)

### DistrictManager.java
- Added `phoneNumber` field
- Column name: `phone_number`
- Type: String (optional)

---

## 🗄️ Database Fix

### Option 1: Run SQL Migration (Recommended)
Execute this SQL in your MySQL client:

```sql
ALTER TABLE state_managers 
MODIFY phone_number VARCHAR(20) NULL;

ALTER TABLE district_managers
MODIFY phone_number VARCHAR(20) NULL;
```

**File:** `db-migrations/002_fix_phone_number_columns.sql`

### Option 2: Complete Table Recreate (Fresh Start)
```sql
-- Drop old tables
DROP TABLE IF EXISTS district_managers;
DROP TABLE IF EXISTS state_managers;

-- Create with correct schema
CREATE TABLE state_managers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    state_id BIGINT NOT NULL,
    school_id BIGINT,
    phone_number VARCHAR(20),
    active BOOLEAN DEFAULT TRUE,
    created_at BIGINT NOT NULL,
    updated_at BIGINT
);

CREATE TABLE district_managers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    state_id BIGINT NOT NULL,
    district_id BIGINT NOT NULL,
    school_id BIGINT,
    phone_number VARCHAR(20),
    active BOOLEAN DEFAULT TRUE,
    created_at BIGINT NOT NULL,
    updated_at BIGINT
);
```

---

## 📝 Updated Entity Schema

### StateManager Fields (Complete)
```
id              → Long (auto-generated)
name            → String (required)
email           → String (unique, required)
password        → String (required)
role            → String (required)
stateId         → Long (required)
schoolId        → Long (optional, nullable)
phoneNumber     → String (optional, nullable) ✨ NEW
active          → Boolean (default: true)
createdAt       → Long (auto-generated)
updatedAt       → Long (auto-generated)
```

### DistrictManager Fields (Complete)
```
id              → Long (auto-generated)
name            → String (required)
email           → String (unique, required)
password        → String (required)
role            → String (required)
stateId         → Long (required)
districtId      → Long (required)
schoolId        → Long (optional, nullable)
phoneNumber     → String (optional, nullable) ✨ NEW
active          → Boolean (default: true)
createdAt       → Long (auto-generated)
updatedAt       → Long (auto-generated)
```

---

## 🔄 After Fix, Your API Requests

### Create State Manager (Now Works!)
```json
POST /api/superadmin/state-managers/create

{
  "name": "Rajesh Kumar",
  "email": "rajesh@state.com",
  "password": "rajesh@123",
  "stateId": 1,
  "phoneNumber": "9876543210",
  "role": "STATE_MANAGER"
}
```

### Create District Manager (Now Works!)
```json
POST /api/superadmin/district-managers/create

{
  "name": "Priya Singh",
  "email": "priya@district.com",
  "password": "priya@123",
  "stateId": 1,
  "districtId": 1,
  "phoneNumber": "9876543210",
  "role": "DISTRICT_MANAGER"
}
```

---

## ✨ Steps to Fix Your Database

### Step 1: Run SQL Migration
```sql
-- Copy this into your MySQL client
ALTER TABLE state_managers 
MODIFY phone_number VARCHAR(20) NULL;

ALTER TABLE district_managers
MODIFY phone_number VARCHAR(20) NULL;
```

### Step 2: Rebuild Java Project
```bash
mvn clean compile
mvn clean install
```

### Step 3: Restart Application
```bash
mvn spring-boot:run
```

### Step 4: Try Creating State Manager Again
Use Postman with the request above - should work now!

---

## 🧪 Test It

### Create State Manager (Now Works)
```
POST http://localhost:8080/api/superadmin/state-managers/create

Headers:
Authorization: Bearer {super_admin_token}
Content-Type: application/json

Body:
{
  "name": "Rajesh Kumar",
  "email": "rajesh@state.com",
  "password": "rajesh@123",
  "stateId": 1,
  "phoneNumber": "9876543210",
  "role": "STATE_MANAGER"
}

Response: 200 OK ✅
```

---

## 📊 Database Verification

After running SQL, verify with:
```sql
DESC state_managers;
DESC district_managers;
```

You should see:
```
Field           | Type           | Null | Key | Default
phone_number    | varchar(20)    | YES  |     | NULL
```

---

## ✅ Status

- [x] StateManager entity updated
- [x] DistrictManager entity updated
- [x] SQL migration created
- [x] Phone number is optional (nullable)
- [x] Ready to create managers

---

## 🚀 Next Steps

1. Run the SQL migration
2. Rebuild and restart
3. Try creating State Manager again
4. Phone number is optional - you can include it or leave it out

**Error fixed! Ready to create State Managers! 🎉**

