# 🔧 Quick Fix - Copy & Paste SQL

## Run This SQL to Fix the Error

```sql
-- Fix state_managers table
ALTER TABLE state_managers 
MODIFY phone_number VARCHAR(20) NULL;

-- Fix district_managers table
ALTER TABLE district_managers
MODIFY phone_number VARCHAR(20) NULL;
```

---

## How to Apply

1. Open MySQL Workbench or MySQL Client
2. Select your database: `smartschool` (or whatever your DB name is)
3. Copy and paste the SQL above
4. Click Execute
5. Done!

---

## Verify Fix

```sql
-- Check state_managers
DESC state_managers;

-- Check district_managers  
DESC district_managers;
```

Should show `phone_number` with `Null: YES`

---

## Then Rebuild Java

```bash
cd C:\smart-school-pro\sms-backend\sms-backend
mvn clean compile
mvn spring-boot:run
```

---

## Now Create State Manager

Use Postman and try again - it should work! ✅

**Optional:** Include phone_number in request:
```json
{
  "name": "Rajesh Kumar",
  "email": "rajesh@state.com",
  "password": "rajesh@123",
  "stateId": 1,
  "phoneNumber": "9876543210",
  "role": "STATE_MANAGER"
}
```

**Or** leave it out - it's optional now:
```json
{
  "name": "Rajesh Kumar",
  "email": "rajesh@state.com",
  "password": "rajesh@123",
  "stateId": 1,
  "role": "STATE_MANAGER"
}
```

Both work! ✅

