# 🚌 Bus Management System - Complete Flow Guide (हिंदी)

## 📋 Bus Setup Ka Pura Flow

```
STEP 1: BUS ADD KARO
    ↓
STEP 2: DRIVER ADD KARO (Bus ke liye)
    ↓
STEP 3: ROUTE CREATE KARO (Bus ke liye)
    ↓
STEP 4: STOPPAGE ADD KARO (Route ke andar)
    ↓
STEP 5: FEE RATE SET KARO (Har stoppage ke liye)
    ↓
STEP 6: STUDENT ASSIGN KARO (Bus + Stoppage + Fee)
    ↓
STEP 7: FEES COLLECT KARO (Month ke based)
    ↓
STEP 8: REPORTS DEKHO (Due, Collection, etc)
```

---

## 🚌 STEP 1: BUS ADD KARO

**Kya karo:** School ko pehla bus add karna hai

**API:**
```
POST /api/buses/admin/{schoolId}/add
Parameters:
  - registrationNo: "MH12AB1234" (Number plate)
  - capacity: 50 (Kitne students fit ho sakte hain)

Example:
POST http://localhost:8080/api/buses/admin/1/add
?registrationNo=MH12AB1234&capacity=50
```

**Response:**
```json
{
  "id": 1,
  "registrationNo": "MH12AB1234",
  "capacity": 50,
  "school": { "id": 1 },
  "active": true
}
```

**Postman:**
```
1. New Request → POST
2. URL: http://localhost:8080/api/buses/admin/1/add
3. Params:
   - registrationNo: MH12AB1234
   - capacity: 50
4. Send
```

---

## 👨‍✔️ STEP 2: DRIVER ADD KARO

**Kya karo:** Bus ke liye driver assign karna hai

**Note:** Driver add karne ki API mujhe exact nahi pata, lekin check karo:

**Possible API:**
```
POST /api/drivers/add
Body:
{
  "name": "Rajesh Kumar",
  "phoneNumber": "9876543210",
  "busId": 1,
  "schoolId": 1,
  "licenseNumber": "ABC123456"
}
```

**Ya existing endpoint use karo:**
```
PUT /api/buses/{busId}/assign-driver
Body:
{
  "driverId": 1
}
```

---

## 🗺️ STEP 3: ROUTE CREATE KARO

**Kya karo:** Bus ke liye route banana hai (jisme stops honge)

**API:**
```
POST /api/admin/routes/{schoolId}/create
Parameters:
  - busId: 1
  - routeName: "Route A" (Koi bhi naam)

Example:
POST http://localhost:8080/api/admin/routes/1/create
?busId=1&routeName=Route A
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

---

## 🛑 STEP 4: STOPPAGE ADD KARO

**Kya karo:** Route ke andar stoppages add karne hain (jahan bus rukta hai)

**API:**
```
POST /api/admin/routes/{schoolId}/{routeId}/stoppages/add
Parameters:
  - stopName: "Stop 1" (Stop ka naam)

Example:
POST http://localhost:8080/api/admin/routes/1/1/stoppages/add
?stopName=Stop 1
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

**Repeat karo multiple stops ke liye:**
```
Stop 1 → Stop 2 → Stop 3 → Stop 4 → Stop 5
```

---

## 💰 STEP 5: FEE RATE SET KARO (NAYA!)

**Kya karo:** Har stoppage ke liye monthly fee set karna hai

**API:**
```
POST /api/bus-monthly-fees/rate-structure/create
Parameters:
  - stoppageId: 1
  - busId: 1
  - schoolId: 1
  - academicYearId: 1
  - monthlyFeeAmount: 100 (₹100 per month)
  - totalMonths: 12 (July se June tak)
  - academicYearStartMonth: July

Example:
POST http://localhost:8080/api/bus-monthly-fees/rate-structure/create
?stoppageId=1&busId=1&schoolId=1&academicYearId=1
&monthlyFeeAmount=100&totalMonths=12&academicYearStartMonth=July
```

**Response:**
```json
{
  "id": 1,
  "stoppage": { "id": 1, "stopName": "Stop 1" },
  "bus": { "id": 1 },
  "monthlyFeeAmount": 100,
  "totalMonths": 12,
  "academicYearStartMonth": "July",
  "active": true
}
```

**Repeat karo har stoppage ke liye!**

---

## 👨‍🎓 STEP 6: STUDENT ASSIGN KARO

**Kya karo:** Student ko bus + stoppage assign karna hai with fee

**API:**
```
POST /api/bus-monthly-fees/assign-student
Body:
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

Example:
POST http://localhost:8080/api/bus-monthly-fees/assign-student
```

**Response:**
```json
{
  "id": 1,
  "student": { "id": 1, "name": "Raj Kumar" },
  "stoppage": { "id": 1, "stopName": "Stop 1" },
  "monthlyFeeAmount": 100,
  "selectedMonths": ["July", "August", "September", "October", "November", "December", "January", "February", "March", "April"],
  "joiningMonth": "July",
  "paymentFrequency": "MONTHLY",
  "totalFeeAmount": 1000
}
```

**Late Joining Example:**
```json
{
  "studentId": 2,
  "stoppageId": 1,
  "selectedMonths": ["October", "November", "December", "January", "February", "March", "April"],
  "joiningMonth": "October",
  "paymentFrequency": "MONTHLY"
}

Result: Total Fee = 100 × 7 = 700
```

---

## 💳 STEP 7: FEES COLLECT KARO

**Kya karo:** Student se bus fees lene hain

### Option A: Monthly Collection
```
POST /api/buses/admin/collect-fee
Parameters:
  - studentId: 1
  - academicYearId: 1
  - amount: 100 (₹100 for 1 month)
  - paymentMode: CASH (ya ONLINE, CHEQUE, etc)
```

### Option B: Yearly Collection
```
POST /api/buses/admin/collect-fee
Parameters:
  - studentId: 1
  - academicYearId: 1
  - amount: 1000 (₹1000 for 10 months)
  - paymentMode: ONLINE
```

---

## 📊 STEP 8: REPORTS DEKHO

### A. Due Report (Kitna due hai?)
```
GET /api/bus-monthly-fees/due-report/{schoolId}
Query:
  - academicYearId: 1

Response: List of students with pending dues
```

### B. Student Wise Breakdown
```
GET /api/bus-monthly-fees/breakdown/{studentId}
Query:
  - academicYearId: 1

Shows: July - ₹100, August - ₹100, etc
```

### C. Download Excel
```
GET /api/bus-fees-report/download-excel/{schoolId}
Query:
  - academicYearId: 1

Downloads: Excel file with all dues
```

### D. Total Dues
```
GET /api/bus-fees-report/total-dues/{schoolId}
Query:
  - academicYearId: 1

Response: {"totalDue": 5000}
```

---

## 🔄 COMPLETE EXAMPLE (START TO END)

### Step 1: Add Bus
```
Bus: MH12AB1234, Capacity: 50
Result: Bus ID = 1
```

### Step 2: Add Driver (Optional)
```
Driver: Rajesh Kumar, License: ABC123456
Assigned to: Bus 1
```

### Step 3: Create Route
```
Route Name: "Route A"
Bus: 1
Result: Route ID = 1
```

### Step 4: Add Stoppages
```
Stop 1 → Stop ID = 1
Stop 2 → Stop ID = 2
Stop 3 → Stop ID = 3
Stop 4 → Stop ID = 4
Stop 5 → Stop ID = 5
```

### Step 5: Set Fees
```
Stop 1: ₹100/month
Stop 2: ₹120/month
Stop 3: ₹100/month
Stop 4: ₹150/month
Stop 5: ₹100/month
```

### Step 6: Assign Students
```
Raj Kumar → Stop 1, July-April (10 months), ₹1000
Priya Singh → Stop 2, October-April (7 months), ₹840
Akash Patel → Stop 3, July-June (12 months), ₹1200
```

### Step 7: Collect Fees
```
Raj Kumar: Pay ₹100/month (10 times)
Priya Singh: Pay ₹840 (one time yearly)
Akash Patel: Pay ₹100/month (12 times)
```

### Step 8: Check Reports
```
Due Report: Shows who has pending dues
Total Dues: ₹2040 (example)
Breakdown: Month-wise fees for each student
```

---

## 📱 Postman Order

```
1. POST /api/buses/admin/1/add
   → Get Bus ID

2. POST /api/admin/routes/1/create
   → Get Route ID

3. POST /api/admin/routes/1/1/stoppages/add
   → Get Stoppage ID (repeat for each stop)

4. POST /api/bus-monthly-fees/rate-structure/create
   → Set fees (repeat for each stoppage)

5. POST /api/bus-monthly-fees/assign-student
   → Assign students (repeat for each student)

6. POST /api/buses/admin/collect-fee
   → Collect payments

7. GET /api/bus-monthly-fees/due-report/1
   → View dues

8. GET /api/bus-fees-report/download-excel/1
   → Download reports
```

---

## 🎯 Key Points

✅ **Bus ID** → Get from Step 1
✅ **Route ID** → Get from Step 3
✅ **Stoppage ID** → Get from Step 4
✅ **Student ID** → Should already exist
✅ **Academic Year ID** → Usually 1
✅ **School ID** → Usually 1

---

## 📋 Checklist

- [ ] Bus added (registrationNo, capacity)
- [ ] Driver assigned (optional)
- [ ] Route created (routeName)
- [ ] Stoppages added (at least 1-5)
- [ ] Fee rates set (monthly amount)
- [ ] Students have bus assignments
- [ ] Students exist in database
- [ ] Academic Year exists
- [ ] School registered in system

---

## 🆘 Common Issues

### "Stoppage not found"
**Solution:** Pehle stoppage add karo, fir fee set karo

### "Bus not found"
**Solution:** Pehle bus add karo

### "Student not found"
**Solution:** Student enrollment mein hona chahiye

### "Academic year not found"
**Solution:** School ke current academic year set karo

---

## 💡 Tips

1. **Sequential Order:** Always follow the order (Bus → Route → Stoppage → Fees → Students)
2. **IDs Track:** Har step se ID note karo
3. **Repeat:** Fees set karo sabhi stoppages ke liye
4. **Test:** Postman se test karo pehle
5. **Check:** Database mein data verify karo

---

**Ab Samjh gaye na? 🎉**

**Order:**
1. Bus ✅
2. Driver ✅
3. Route ✅
4. Stoppages ✅
5. Fees ✅
6. Students ✅
7. Collections ✅
8. Reports ✅

**Sab kuch complete flow mein! 🚀**

