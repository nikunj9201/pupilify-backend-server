# 🚌 Bus Management Complete Flow - English Guide

## 📋 8-Step Process

```
1. ADD BUS
   ↓
2. ADD DRIVER (Optional)
   ↓
3. CREATE ROUTE
   ↓
4. ADD STOPPAGES
   ↓
5. SET FEE RATES (NEW FEATURE)
   ↓
6. ASSIGN STUDENTS (NEW FEATURE)
   ↓
7. COLLECT FEES
   ↓
8. VIEW REPORTS
```

---

## STEP 1: ADD BUS

**Endpoint:**
```
POST /api/buses/admin/{schoolId}/add
?registrationNo=MH12AB1234&capacity=50
```

**Parameters:**
- `schoolId` - School ID (e.g., 1)
- `registrationNo` - Vehicle registration number
- `capacity` - Number of students (e.g., 50)

**cURL:**
```bash
curl -X POST "http://localhost:8080/api/buses/admin/1/add?registrationNo=MH12AB1234&capacity=50" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Postman:**
- Method: POST
- URL: `http://localhost:8080/api/buses/admin/1/add`
- Query Params: `registrationNo=MH12AB1234`, `capacity=50`
- Click Send

**Response:**
```json
{
  "id": 1,
  "registrationNo": "MH12AB1234",
  "capacity": 50,
  "school": { "id": 1 },
  "driverName": null,
  "active": true,
  "createdAt": "2026-08-20T12:00:00"
}
```

**Save:** Bus ID = 1

---

## STEP 2: ADD DRIVER (OPTIONAL)

**Endpoint:**
```
POST /api/drivers/add
```

**Body:**
```json
{
  "name": "Rajesh Kumar",
  "phoneNumber": "9876543210",
  "busId": 1,
  "schoolId": 1,
  "licenseNumber": "ABC123456"
}
```

OR use:
```
PUT /api/buses/{busId}
?driverName=Rajesh Kumar
```

---

## STEP 3: CREATE ROUTE

**Endpoint:**
```
POST /api/admin/routes/{schoolId}/create
?busId=1&routeName=Route A
```

**Parameters:**
- `schoolId` - School ID
- `busId` - Bus ID from Step 1
- `routeName` - Name of the route

**cURL:**
```bash
curl -X POST "http://localhost:8080/api/admin/routes/1/create?busId=1&routeName=Route A" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Response:**
```json
{
  "id": 1,
  "routeName": "Route A",
  "bus": { "id": 1 },
  "school": { "id": 1 },
  "stoppages": [],
  "isActive": true
}
```

**Save:** Route ID = 1

---

## STEP 4: ADD STOPPAGES

**Endpoint:**
```
POST /api/admin/routes/{schoolId}/{routeId}/stoppages/add
?stopName=Stop 1
```

**Parameters:**
- `schoolId` - School ID
- `routeId` - Route ID from Step 3
- `stopName` - Name of the bus stop

**cURL:**
```bash
curl -X POST "http://localhost:8080/api/admin/routes/1/1/stoppages/add?stopName=Stop 1" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Response:**
```json
{
  "id": 1,
  "stopName": "Stop 1",
  "route": { "id": 1 },
  "isActive": true
}
```

**Repeat for each stop:**
```
Stop 1 → Stoppage ID = 1
Stop 2 → Stoppage ID = 2
Stop 3 → Stoppage ID = 3
Stop 4 → Stoppage ID = 4
Stop 5 → Stoppage ID = 5
```

---

## STEP 5: SET FEE RATES (NEW!)

**Endpoint:**
```
POST /api/bus-monthly-fees/rate-structure/create
```

**Query Parameters:**
```
?stoppageId=1
&busId=1
&schoolId=1
&academicYearId=1
&monthlyFeeAmount=100
&totalMonths=12
&academicYearStartMonth=July
```

**cURL:**
```bash
curl -X POST "http://localhost:8080/api/bus-monthly-fees/rate-structure/create?stoppageId=1&busId=1&schoolId=1&academicYearId=1&monthlyFeeAmount=100&totalMonths=12&academicYearStartMonth=July" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Response:**
```json
{
  "id": 1,
  "stoppage": { "id": 1, "stopName": "Stop 1" },
  "bus": { "id": 1 },
  "school": { "id": 1 },
  "academicYear": { "id": 1 },
  "monthlyFeeAmount": 100,
  "totalMonths": 12,
  "academicYearStartMonth": "July",
  "active": true
}
```

**Repeat for each stoppage:**
```
Stop 1: ₹100/month
Stop 2: ₹120/month
Stop 3: ₹100/month
Stop 4: ₹150/month
Stop 5: ₹100/month
```

---

## STEP 6: ASSIGN STUDENTS (NEW!)

**Endpoint:**
```
POST /api/bus-monthly-fees/assign-student
```

**Body:**
```json
{
  "studentId": 1,
  "stoppageId": 1,
  "busId": 1,
  "schoolId": 1,
  "academicYearId": 1,
  "selectedMonths": ["July", "August", "September", "October", "November", "December", "January", "February", "March", "April"],
  "joiningMonth": "July",
  "paymentFrequency": "MONTHLY"
}
```

**cURL:**
```bash
curl -X POST "http://localhost:8080/api/bus-monthly-fees/assign-student" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": 1,
    "stoppageId": 1,
    "busId": 1,
    "schoolId": 1,
    "academicYearId": 1,
    "selectedMonths": ["July", "August", "September", "October", "November", "December", "January", "February", "March", "April"],
    "joiningMonth": "July",
    "paymentFrequency": "MONTHLY"
  }'
```

**Response:**
```json
{
  "id": 1,
  "student": { "id": 1, "name": "Raj Kumar" },
  "stoppage": { "id": 1, "stopName": "Stop 1" },
  "monthlyFeeAmount": 100,
  "selectedMonths": "[\"July\",\"August\",\"September\",\"October\",\"November\",\"December\",\"January\",\"February\",\"March\",\"April\"]",
  "joiningMonth": "July",
  "paymentFrequency": "MONTHLY",
  "totalFeeAmount": 1000,
  "assignmentDate": "2026-08-20",
  "active": true
}
```

### Late Joining Example:
```json
{
  "studentId": 2,
  "stoppageId": 1,
  "busId": 1,
  "schoolId": 1,
  "academicYearId": 1,
  "selectedMonths": ["October", "November", "December", "January", "February", "March", "April"],
  "joiningMonth": "October",
  "paymentFrequency": "MONTHLY"
}
```
**Result:** Total Fee = 100 × 7 = ₹700

---

## STEP 7: COLLECT FEES

**Endpoint:**
```
POST /api/buses/admin/collect-fee
```

**Query Parameters:**
```
?studentId=1
&academicYearId=1
&amount=100
&paymentMode=CASH
```

**Monthly Collection:**
```
amount=100 (one month)
paymentMode=CASH
```

**Yearly Collection:**
```
amount=1000 (entire year)
paymentMode=ONLINE
```

**cURL:**
```bash
curl -X POST "http://localhost:8080/api/buses/admin/collect-fee?studentId=1&academicYearId=1&amount=100&paymentMode=CASH" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## STEP 8: VIEW REPORTS

### Report 1: Due Report
```
GET /api/bus-monthly-fees/due-report/{schoolId}?academicYearId=1

Shows: All students with pending dues
```

### Report 2: Student Breakdown
```
GET /api/bus-monthly-fees/breakdown/{studentId}?academicYearId=1

Shows: Month-wise fees for specific student
```

### Report 3: Download Excel
```
GET /api/bus-fees-report/download-excel/{schoolId}?academicYearId=1

Returns: Excel file download
```

### Report 4: Count of Dues
```
GET /api/bus-fees-report/count-dues/{schoolId}?academicYearId=1

Response: {"count": 5}
```

### Report 5: Total Dues Amount
```
GET /api/bus-fees-report/total-dues/{schoolId}?academicYearId=1

Response: {"totalDue": 5000.00}
```

---

## 🎯 Complete Example Flow

```
SCHOOL: ABC School
ACADEMIC YEAR: 2025-26

→ ADD BUS
  Bus: MH12AB1234 (50 seater)
  Bus ID = 1

→ CREATE ROUTE
  Route: Route A
  Route ID = 1

→ ADD STOPPAGES
  Stop 1, Stop 2, Stop 3, Stop 4, Stop 5
  Stoppage IDs = 1, 2, 3, 4, 5

→ SET FEES
  Stop 1: ₹100/month
  Stop 2: ₹120/month
  Stop 3: ₹100/month
  Stop 4: ₹150/month
  Stop 5: ₹100/month

→ ASSIGN STUDENTS
  Raj Kumar → Stop 1, July-April (10 months) = ₹1000
  Priya Singh → Stop 2, October-April (7 months) = ₹840
  Akash Patel → Stop 3, July-June (12 months) = ₹1200
  Total = ₹3040

→ COLLECT FEES
  July: Collect ₹100 from each student (3 students)
  August: Collect ₹100 from each student
  Continue...

→ VIEW REPORTS
  Due Report: Shows remaining dues
  Excel Download: All details in formatted file
```

---

## 📋 API Sequence

```
1. POST /api/buses/admin/1/add
   Response: Bus ID = 1

2. POST /api/admin/routes/1/create
   Response: Route ID = 1

3. POST /api/admin/routes/1/1/stoppages/add
   Response: Stoppage ID = 1 (Repeat for each stop)

4. POST /api/bus-monthly-fees/rate-structure/create
   (Repeat for each stoppage)

5. POST /api/bus-monthly-fees/assign-student
   (Repeat for each student)

6. POST /api/buses/admin/collect-fee
   (Multiple times during the year)

7. GET /api/bus-monthly-fees/due-report/1
   (View pending dues)

8. GET /api/bus-fees-report/download-excel/1
   (Download reports)
```

---

## ✅ Pre-requisites

Before you start:
- [ ] School exists in system
- [ ] Academic year is set for the school
- [ ] Student records exist
- [ ] Admin/Manager user with permission

---

## 🆘 Common Errors & Solutions

| Error | Solution |
|-------|----------|
| Bus not found | Add bus first using Step 1 |
| Route not found | Create route first using Step 3 |
| Stoppage not found | Add stoppages first using Step 4 |
| Academic year not found | Ensure school has academic year set |
| Student not found | Student must exist in database |
| Fee rate not found | Set fee rates using Step 5 before assigning students |

---

## 💡 Pro Tips

1. **Always follow the order** - Don't skip any step
2. **Keep IDs handy** - Note down IDs from each step
3. **Use Postman** - Test APIs before integration
4. **Verify data** - Check database after each step
5. **Bulk operations** - For multiple students, automate via scripts

---

## 🎯 Summary Table

| Step | Action | Endpoint | Key Output |
|------|--------|----------|-----------|
| 1 | Add Bus | POST /buses | Bus ID |
| 2 | Add Driver | POST /drivers | Driver ID |
| 3 | Create Route | POST /routes | Route ID |
| 4 | Add Stoppages | POST /stoppages | Stoppage IDs |
| 5 | Set Fees | POST /rate-structure | Fee IDs |
| 6 | Assign Students | POST /assign-student | Assignment IDs |
| 7 | Collect Fees | POST /collect-fee | Receipt/Log |
| 8 | View Reports | GET /reports | Excel/Data |

---

**Ready to implement? Follow these 8 steps! 🚀**

