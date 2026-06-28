# ✅ DEPLOYMENT CHECKLIST - Hibernate Fix

## Pre-Deployment Checks

- [x] Code changes completed in `SubjectController.java`
- [x] All 3 GET methods return `SubjectDTO` instead of `Subject`
- [x] Helper method `convertToDTO()` created and working
- [x] Build compilation successful (0 errors)
- [x] JAR file generated: `target/sms-backend-0.0.1-SNAPSHOT.jar`
- [x] No breaking changes to API contracts

---

## Deployment Steps (Execute in Order)

### Step 1: Stop Current Application ⏹️
```bash
taskkill /F /IM java.exe
# Wait 2-3 seconds for process to terminate
Start-Sleep -Seconds 3
```
- [ ] Old Java process killed
- [ ] Port 8080 is now free

### Step 2: Navigate to Project Directory 📂
```bash
cd "C:\Users\nikun\Downloads\sms-backend (1)\sms-backend\sms-backend"
```
- [ ] Current directory is: `.../sms-backend/sms-backend`

### Step 3: Clean Build (if not done) 🔨
```bash
$env:JAVA_HOME = "C:\Program Files\Java\jdk-22"
.\mvnw clean package -DskipTests
# Should see: BUILD SUCCESS
```
- [ ] Build completed successfully
- [ ] JAR file exists in `target/` folder

### Step 4: Start New Application 🚀
```bash
java -jar target/sms-backend-0.0.1-SNAPSHOT.jar
# Monitor console for startup messages
```
- [ ] Application starts without errors
- [ ] Logs show Spring Boot startup messages
- [ ] No "Exception" or "Error" in startup logs
- [ ] See message: "Started [AppName]"

### Step 5: Wait for Startup ⏳
```
⏱️ Wait approximately 15-20 seconds
```
- [ ] Application fully initialized
- [ ] Database connections established
- [ ] Ready to accept requests

### Step 6: Test the Problematic Endpoint 🧪
```bash
# In a new PowerShell window:
Invoke-WebRequest -Uri "http://localhost:8080/api/admin/subjects/school/1/class/1?academicYearId=1" `
  -Headers @{"Authorization"="Bearer YOUR_JWT_TOKEN"} `
  -Method Get
```

**Expected Results:**
- [ ] HTTP Status Code: **200 OK** (not 500)
- [ ] Response contains JSON array
- [ ] No "ByteBuddyInterceptor" errors
- [ ] Response looks like:
```json
[
  {
    "id": 1,
    "subjectName": "...",
    "schoolClassId": 1,
    "sectionId": null,  // ← OK to be null
    ...
  }
]
```

### Step 7: Test Additional Endpoints 🔄
```bash
# Test endpoint with section
Invoke-WebRequest -Uri "http://localhost:8080/api/admin/subjects/school/1/class/1/section/2?academicYearId=1" `
  -Headers @{"Authorization"="Bearer YOUR_JWT_TOKEN"}
# Expected: 200 OK

# Test all subjects endpoint
Invoke-WebRequest -Uri "http://localhost:8080/api/admin/subjects/all/1?academicYearId=1" `
  -Headers @{"Authorization"="Bearer YOUR_JWT_TOKEN"}
# Expected: 200 OK
```

- [ ] `/school/{id}/class/{id}` endpoint works
- [ ] `/school/{id}/class/{id}/section/{id}` endpoint works
- [ ] `/all/{id}` endpoint works

---

## Rollback Plan (If Needed)

**If something goes wrong:**

1. **Stop the application:**
   ```bash
   taskkill /F /IM java.exe
   ```

2. **Revert to previous JAR** (if available)
   ```bash
   # Or rebuild from git commit before the change
   git checkout HEAD~1
   ./mvnw clean package
   java -jar target/sms-backend-0.0.1-SNAPSHOT.jar
   ```

3. **Check logs for errors:**
   ```bash
   # Application will print errors to console
   # Look for: "Exception", "Error", "Failed"
   ```

---

## Post-Deployment Verification

- [ ] All 3 Subject endpoints return 200 OK
- [ ] No 500 errors in logs
- [ ] JSON responses are valid
- [ ] sectionId can be null (properly handled)
- [ ] Frontend can fetch and display subjects
- [ ] No performance degradation

---

## Monitoring After Deployment

**First 10 minutes:**
- Monitor application logs
- Check for any new errors
- Verify database connections stable

**First hour:**
- Monitor endpoint response times
- Check memory usage stable
- Verify no repeated errors

**First day:**
- Monitor error logs for patterns
- Check application metrics
- Gather feedback from QA team

---

## Success Criteria

✅ **All of the following must be true:**

1. Application starts without errors
2. Port 8080 is accessible
3. Subject endpoints return HTTP 200
4. Response contains valid JSON
5. No "ByteBuddyInterceptor" errors
6. No "Type definition error" exceptions
7. sectionId properly handled as null
8. All three GET endpoints work

---

## Communication

**Notify the team:**
- [ ] Fixed the Hibernate proxy serialization error
- [ ] Subject endpoints now return clean DTO objects
- [ ] No API contract changes (same endpoints, better responses)
- [ ] Ready for testing

---

## Notes

```
⚠️ IMPORTANT:
- Must kill old Java process before starting new one
- Must use Java 21 or 22 (not older versions)
- JWT token required for testing (ROLE_ADMIN)
- sectionId can be NULL - this is expected behavior
```

---

## Checklist Sign-Off

| Item | Done | Time | Notes |
|------|------|------|-------|
| Build successful | [ ] | | |
| Old app stopped | [ ] | | |
| New app started | [ ] | | |
| Test endpoint | [ ] | | |
| All tests pass | [ ] | | |
| Logs clean | [ ] | | |
| Ready for QA | [ ] | | |

---

**Deployment Date:** [DATE]  
**Deployed By:** [NAME]  
**Status:** [ ] IN PROGRESS [ ] COMPLETED [ ] FAILED  

---

# Quick Command Reference

```powershell
# Kill old app
taskkill /F /IM java.exe

# Navigate to project
cd "C:\Users\nikun\Downloads\sms-backend (1)\sms-backend\sms-backend"

# Set Java home (if needed)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-22"

# Build (if not done)
.\mvnw clean package -DskipTests

# Start app
java -jar target/sms-backend-0.0.1-SNAPSHOT.jar

# Test in another window
$headers = @{"Authorization"="Bearer YOUR_TOKEN"}
Invoke-WebRequest -Uri "http://localhost:8080/api/admin/subjects/school/1/class/1?academicYearId=1" -Headers $headers -Method Get
```

---

✅ **Ready for Deployment!**

