# Setup Checklist - School State District Integration

## ✅ Step-by-Step Setup Guide

### Phase 1: Database Migration ⏱️ (5 minutes)

- [ ] Open MySQL Workbench or MySQL CLI
- [ ] Connect to `smart_school_pro` database
- [ ] Run SQL from `SQL_MIGRATION_GUIDE.md`:
  ```sql
  ALTER TABLE schools ADD COLUMN state_id BIGINT NULL;
  ALTER TABLE schools ADD COLUMN district_id BIGINT NULL;
  ALTER TABLE schools ADD CONSTRAINT fk_schools_state FOREIGN KEY (state_id) REFERENCES states(id);
  ALTER TABLE schools ADD CONSTRAINT fk_schools_district FOREIGN KEY (district_id) REFERENCES districts(id);
  CREATE INDEX idx_schools_state_id ON schools(state_id);
  CREATE INDEX idx_schools_district_id ON schools(district_id);
  ```
- [ ] Verify with: `SELECT * FROM information_schema.columns WHERE table_name='schools' AND column_name IN ('state_id', 'district_id');`

### Phase 2: Build Backend ⏱️ (3 minutes)

- [ ] Open PowerShell/Terminal
- [ ] Navigate to: `cd C:\smart-school-pro\sms-backend\sms-backend`
- [ ] Clean build: `.\mvnw.cmd clean compile`
- [ ] Check for errors: Should show "BUILD SUCCESS"

### Phase 3: Start Application ⏱️ (5 minutes)

- [ ] Run: `.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local`
- [ ] Wait for: "Started Application in ... seconds"
- [ ] Verify logs: "Tomcat started on port(s): 8080"
- [ ] Keep terminal running

### Phase 4: Test API Integration ⏱️ (10 minutes)

#### 4a. Login (Get Token)
- [ ] Request: `POST http://localhost:8080/api/auth/login`
- [ ] Body: `{"username": "your_admin_email", "password": "your_password"}`
- [ ] Response: Should contain `accessToken`
- [ ] Save token for next steps

#### 4b. Create State
- [ ] Request: `POST http://localhost:8080/api/superadmin/state/create`
- [ ] Header: `Authorization: Bearer {your_token}`
- [ ] Body: `{"name": "Madhya Pradesh", "code": "MP"}`
- [ ] Response: Should contain `"id": 1` (or similar)
- [ ] **SAVE STATE_ID**

#### 4c. Create District
- [ ] Request: `POST http://localhost:8080/api/district/create`
- [ ] Header: `Authorization: Bearer {your_token}`
- [ ] Body: `{"name": "Indore", "code": "IND", "state": {"id": 1}}`
- [ ] Response: Should contain `"id": 1`
- [ ] **SAVE DISTRICT_ID**

#### 4d. Create School
- [ ] Request: `POST http://localhost:8080/api/superadmin/schools/add`
- [ ] Header: `Authorization: Bearer {your_token}`
- [ ] Body (form-data):
  - `schoolData`: `{"schoolName": "Test School", "mailId": "test@school.com", "password": "Pass@123", "address": "Indore", "phoneNumber": "9876543210"}`
  - `currentYearId`: `1`
- [ ] Response: Should contain `"id": (some_number)`
- [ ] **SAVE SCHOOL_ID**

#### 4e. Assign State to School
- [ ] Request: `PUT http://localhost:8080/api/superadmin/schools/{SCHOOL_ID}/assign-state/1`
- [ ] Header: `Authorization: Bearer {your_token}`
- [ ] Response: Should show `"message": "State assigned successfully"`
- [ ] ✅ State assignment working

#### 4f. Assign District to School
- [ ] Request: `PUT http://localhost:8080/api/superadmin/schools/{SCHOOL_ID}/assign-district/1`
- [ ] Header: `Authorization: Bearer {your_token}`
- [ ] Response: Should show `"message": "District assigned successfully"`
- [ ] ✅ District assignment working

#### 4g. Verify School Details
- [ ] Request: `GET http://localhost:8080/api/superadmin/schools/{SCHOOL_ID}`
- [ ] Header: `Authorization: Bearer {your_token}`
- [ ] Response: Should contain both `state` and `district` objects
- [ ] ✅ School hierarchy verified

#### 4h. Test Filtering by State
- [ ] Request: `GET http://localhost:8080/api/superadmin/schools/by-state/1`
- [ ] Header: `Authorization: Bearer {your_token}`
- [ ] Response: Should contain your school
- [ ] ✅ State filtering working

#### 4i. Test Filtering by District
- [ ] Request: `GET http://localhost:8080/api/superadmin/schools/by-district/1`
- [ ] Header: `Authorization: Bearer {your_token}`
- [ ] Response: Should contain your school
- [ ] ✅ District filtering working

### Phase 5: Import Postman Collection ⏱️ (2 minutes)

- [ ] Open Postman
- [ ] Click **Import** (top-left corner)
- [ ] Select file: `postman_collection_school_state_district.json`
- [ ] Click **Import**
- [ ] Collection appears in left sidebar
- [ ] Update `{{token}}` variable in collection settings
- [ ] All requests ready to use

### Phase 6: Documentation Review ⏱️ (5 minutes)

- [ ] Read: `IMPLEMENTATION_COMPLETE.md` - Overview
- [ ] Read: `SCHOOL_STATE_DISTRICT_GUIDE.md` - Detailed guide
- [ ] Read: `QUICK_REFERENCE.md` - Quick API reference
- [ ] Read: `ARCHITECTURE_DIAGRAM.md` - System architecture
- [ ] Bookmark: `SQL_MIGRATION_GUIDE.md` - For future reference

---

## 🔍 Verification Checklist

### Database Verification
- [ ] `schools` table has `state_id` column
- [ ] `schools` table has `district_id` column
- [ ] Foreign key constraints exist
- [ ] Indexes created successfully

### Application Verification
- [ ] Application starts without errors
- [ ] Port 8080 is accessible
- [ ] Authentication works
- [ ] State creation works
- [ ] District creation works
- [ ] School creation works

### API Verification
- [ ] State assignment endpoint returns 200 OK
- [ ] District assignment endpoint returns 200 OK
- [ ] State filtering returns correct schools
- [ ] District filtering returns correct schools
- [ ] School details include state and district

### Postman Verification
- [ ] Collection imported successfully
- [ ] All requests show correct endpoints
- [ ] Variables are properly set
- [ ] Requests execute without errors

---

## 🚨 Troubleshooting

### If Database Migration Fails
- [ ] Check MySQL is running
- [ ] Verify database `smart_school_pro` exists
- [ ] Confirm columns don't already exist
- [ ] Check for syntax errors in SQL
- [ ] See `SQL_MIGRATION_GUIDE.md` for details

### If Application Won't Start
- [ ] Check Java 21+ is installed: `java -version`
- [ ] Check port 8080 is not in use
- [ ] Check database connection in `application-local.properties`
- [ ] Run: `.\mvnw.cmd clean compile` first
- [ ] Check logs for specific error messages

### If APIs Return 401 Unauthorized
- [ ] Verify token in Authorization header
- [ ] Token format should be: `Bearer {token_value}`
- [ ] Login again to get fresh token
- [ ] Check user has correct role (SUPER_ADMIN or STATE_ADMIN)

### If School Assignment Fails
- [ ] Verify state with ID exists (POST /api/superadmin/state/create first)
- [ ] Verify district with ID exists (POST /api/district/create first)
- [ ] Verify school with ID exists (POST /api/superadmin/schools/add first)
- [ ] Check response error message for details

### If Filtering Returns Empty
- [ ] Verify schools were actually assigned to state/district
- [ ] Check school's state_id and district_id in database
- [ ] Verify query parameters are correct

---

## 📞 Quick Reference

| Task | Endpoint | Method |
|------|----------|--------|
| Create State | `/api/superadmin/state/create` | POST |
| Create District | `/api/district/create` | POST |
| Create School | `/api/superadmin/schools/add` | POST |
| Assign State | `/api/superadmin/schools/{id}/assign-state/{stateId}` | PUT |
| Assign District | `/api/superadmin/schools/{id}/assign-district/{districtId}` | PUT |
| List All Schools | `/api/superadmin/schools/all` | GET |
| List by State | `/api/superadmin/schools/by-state/{stateId}` | GET |
| List by District | `/api/superadmin/schools/by-district/{districtId}` | GET |
| Get School Details | `/api/superadmin/schools/{id}` | GET |

---

## ✨ What's Next (Optional)

After completing the setup:

1. **Add State Manager Login**
   - Create user with `ROLE_STATE_ADMIN`
   - Test accessing schools by state

2. **Add District Manager Login**
   - Create user with `ROLE_DISTRICT_ADMIN`
   - Test accessing schools by district

3. **Add Fine-grained Permissions**
   - Restrict managers to their jurisdiction
   - Add @PreAuthorize annotations

4. **Add Dashboard**
   - Statistics per state
   - Statistics per district
   - School distribution

5. **Add Audit Logging**
   - Track who assigned schools
   - Track when assignments changed

---

## ✅ Final Sign-Off

Once you've completed all checks:

- [ ] All database migration steps completed ✅
- [ ] Application builds and starts successfully ✅
- [ ] All API endpoints tested and working ✅
- [ ] Postman collection imported ✅
- [ ] Documentation reviewed ✅

**🎉 SETUP COMPLETE!**

Your school management system now supports:
- ✅ State hierarchy
- ✅ District hierarchy  
- ✅ School-State-District relationships
- ✅ Role-based access control
- ✅ Filtering by state and district

**You're ready to manage your schools hierarchically!** 🚀

---

## 📧 Support

If you encounter any issues:

1. Check the troubleshooting section above
2. Review the detailed guide: `SCHOOL_STATE_DISTRICT_GUIDE.md`
3. Check application logs: `logs/smartschool.log`
4. Verify database with: `SELECT * FROM schools WHERE state_id IS NOT NULL;`

---

**Thank you for using SmartSchool API!**

