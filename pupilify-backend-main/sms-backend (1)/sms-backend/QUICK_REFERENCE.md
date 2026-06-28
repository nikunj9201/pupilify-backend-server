# Quick Reference - School-State-District API

## 🔑 Setup (Run Once)

1. **Run SQL Migration**
   ```sql
   ALTER TABLE schools ADD COLUMN state_id BIGINT NULL;
   ALTER TABLE schools ADD COLUMN district_id BIGINT NULL;
   ALTER TABLE schools ADD CONSTRAINT fk_schools_state FOREIGN KEY (state_id) REFERENCES states(id);
   ALTER TABLE schools ADD CONSTRAINT fk_schools_district FOREIGN KEY (district_id) REFERENCES districts(id);
   ```

2. **Rebuild & Start Backend**
   ```bash
   mvn clean compile
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   ```

---

## 📝 API Commands

### Authentication
```
POST http://localhost:8080/api/auth/login
Body: {"username": "your_admin@example.com", "password": "password"}
```

### Create State
```
POST http://localhost:8080/api/superadmin/state/create
Headers: Authorization: Bearer {token}
Body: {"name": "Madhya Pradesh", "code": "MP"}
```

### Create District
```
POST http://localhost:8080/api/district/create
Headers: Authorization: Bearer {token}
Body: {"name": "Indore", "code": "IND", "state": {"id": 1}}
```

### Create School
```
POST http://localhost:8080/api/superadmin/schools/add
Headers: Authorization: Bearer {token}
Body (form-data):
  - schoolData: {"schoolName": "ABC School", "mailId": "abc@example.com", "password": "Pass@123", "address": "Delhi", "phoneNumber": "9876543210"}
  - currentYearId: 1
```

### Assign State to School
```
PUT http://localhost:8080/api/superadmin/schools/{schoolId}/assign-state/{stateId}
Headers: Authorization: Bearer {token}
Example: PUT http://localhost:8080/api/superadmin/schools/5/assign-state/1
```

### Assign District to School
```
PUT http://localhost:8080/api/superadmin/schools/{schoolId}/assign-district/{districtId}
Headers: Authorization: Bearer {token}
Example: PUT http://localhost:8080/api/superadmin/schools/5/assign-district/1
```

### Get All Schools
```
GET http://localhost:8080/api/superadmin/schools/all
Headers: Authorization: Bearer {token}
```

### Get Schools by State
```
GET http://localhost:8080/api/superadmin/schools/by-state/{stateId}
Headers: Authorization: Bearer {token}
Example: GET http://localhost:8080/api/superadmin/schools/by-state/1
```

### Get Schools by District
```
GET http://localhost:8080/api/superadmin/schools/by-district/{districtId}
Headers: Authorization: Bearer {token}
Example: GET http://localhost:8080/api/superadmin/schools/by-district/1
```

### Get School Details
```
GET http://localhost:8080/api/superadmin/schools/{schoolId}
Headers: Authorization: Bearer {token}
Example: GET http://localhost:8080/api/superadmin/schools/5
```

---

## 🧪 Test Workflow (Step-by-Step)

```bash
# 1. Login to get token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin@example.com","password":"password123"}'

# Save the token from response as TOKEN

# 2. Create a state
curl -X POST http://localhost:8080/api/superadmin/state/create \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Madhya Pradesh","code":"MP"}'
# Save state ID (e.g., 1)

# 3. Create a district
curl -X POST http://localhost:8080/api/district/create \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Indore","code":"IND","state":{"id":1}}'
# Save district ID (e.g., 1)

# 4. Create a school
curl -X POST http://localhost:8080/api/superadmin/schools/add \
  -H "Authorization: Bearer TOKEN" \
  -F 'schoolData={"schoolName":"ABC School","mailId":"abc@example.com","password":"Pass@123","address":"Delhi","phoneNumber":"9876543210"}' \
  -F 'currentYearId=1'
# Save school ID (e.g., 5)

# 5. Assign state to school
curl -X PUT http://localhost:8080/api/superadmin/schools/5/assign-state/1 \
  -H "Authorization: Bearer TOKEN"

# 6. Assign district to school
curl -X PUT http://localhost:8080/api/superadmin/schools/5/assign-district/1 \
  -H "Authorization: Bearer TOKEN"

# 7. Get schools by state
curl -X GET http://localhost:8080/api/superadmin/schools/by-state/1 \
  -H "Authorization: Bearer TOKEN"

# 8. Get schools by district
curl -X GET http://localhost:8080/api/superadmin/schools/by-district/1 \
  -H "Authorization: Bearer TOKEN"
```

---

## 📊 Response Examples

### Successful State Assignment
```json
{
  "message": "State assigned successfully",
  "schoolId": 5,
  "stateId": 1,
  "stateName": "Madhya Pradesh"
}
```

### Successful District Assignment
```json
{
  "message": "District assigned successfully",
  "schoolId": 5,
  "districtId": 1,
  "districtName": "Indore"
}
```

### School List Response
```json
[
  {
    "id": 5,
    "schoolName": "ABC School",
    "mailId": "abc@example.com",
    "address": "Delhi",
    "phoneNumber": "9876543210",
    "state": {
      "id": 1,
      "name": "Madhya Pradesh",
      "code": "MP"
    },
    "district": {
      "id": 1,
      "name": "Indore",
      "code": "IND",
      "state": { "id": 1, ... }
    }
  }
]
```

---

## ⚠️ Common Errors

| Error | Cause | Solution |
|-------|-------|----------|
| "State not found with ID: X" | State doesn't exist | Create state first using `/api/superadmin/state/create` |
| "District not found with ID: X" | District doesn't exist | Create district first using `/api/district/create` |
| "Unauthorized" | No valid token | Login first and use the token in Authorization header |
| "Foreign key constraint fails" | Migration not applied | Run SQL migration commands |
| "Duplicate entry" | Email already exists | Use different email for school |

---

## 🔗 Import Postman Collection

File: `postman_collection_school_state_district.json`

**Steps:**
1. Open Postman
2. Click "Import" (top-left)
3. Select the JSON file
4. All API requests will be imported
5. Update {{token}} variable in collection settings

---

Done! Your school-state-district hierarchy is ready! 🎉

