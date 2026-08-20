# 🚌 QUICK BUS FLOW REFERENCE

## KAUNSE STEPS KARNE HAI?

### ✅ STEP BY STEP ORDER

**1. BUS ADD KARO**
```
POST /api/buses/admin/1/add?registrationNo=MH12AB1234&capacity=50
Get: Bus ID
```

**2. DRIVER ADD KARO (Optional)**
```
Bus ke liye driver set karo
```

**3. ROUTE CREATE KARO**
```
POST /api/admin/routes/1/create?busId=1&routeName=Route A
Get: Route ID
```

**4. STOPPAGE ADD KARO (Multiple)**
```
POST /api/admin/routes/1/1/stoppages/add?stopName=Stop 1
POST /api/admin/routes/1/1/stoppages/add?stopName=Stop 2
POST /api/admin/routes/1/1/stoppages/add?stopName=Stop 3
Repeat for all stops
Get: Stoppage IDs
```

**5. FEE RATE SET KARO (Har stoppage ke liye)**
```
POST /api/bus-monthly-fees/rate-structure/create
?stoppageId=1&busId=1&schoolId=1&academicYearId=1&monthlyFeeAmount=100&totalMonths=12&academicYearStartMonth=July

Repeat for each stoppage
Get: Fee Rate IDs
```

**6. STUDENT ASSIGN KARO (Bus + Stoppage + Fee)**
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

Repeat for each student
Get: Assignment IDs
```

**7. FEES COLLECT KARO (Monthly ya Yearly)**
```
POST /api/buses/admin/collect-fee
?studentId=1&academicYearId=1&amount=100&paymentMode=CASH

Collect every month or yearly
```

**8. REPORTS DEKHO**
```
GET /api/bus-monthly-fees/due-report/1?academicYearId=1
GET /api/bus-fees-report/download-excel/1?academicYearId=1
GET /api/bus-fees-report/total-dues/1?academicYearId=1
```

---

## 🔄 FLOW DIAGRAM

```
BUS ADDED (₹0)
     ↓
ROUTE CREATED (₹0)
     ↓
STOPPAGES ADDED (₹0)
     ↓
FEE RATES SET (₹100/month, etc)
     ↓
STUDENT ASSIGNED (₹1000 total)
     ↓
FEES COLLECTED (₹100 monthly)
     ↓
REPORTS GENERATED (Due = ₹900)
```

---

## 📊 EXAMPLE DATA

```
School: ABC School
Bus: MH12AB1234 (50 students)
Route: Route A
Stops: Stop 1, Stop 2, Stop 3

Stop 1: ₹100/month → 5 students → ₹500/month
Stop 2: ₹120/month → 3 students → ₹360/month
Stop 3: ₹100/month → 2 students → ₹200/month

Total Monthly: ₹1060
Total Yearly (10 months): ₹10,600

Collection:
Month 1: Collect ₹1060
Month 2: Collect ₹1060
... Continue
```

---

## 📚 DETAILED GUIDES

**Hindi Guide:** `BUS_COMPLETE_FLOW_HINDI.md`
**English Guide:** `BUS_COMPLETE_FLOW_ENGLISH.md`

---

## ✅ CHECKLIST

- [ ] Bus added
- [ ] Driver assigned (optional)
- [ ] Route created
- [ ] All stoppages added
- [ ] Fee rates set for each stoppage
- [ ] Students assigned to bus
- [ ] Fees collected
- [ ] Reports generated

---

## ❌ COMMON MISTAKES (AVOID!)

❌ **WRONG:** Add stoppage, then immediately assign student
✅ **RIGHT:** Add stoppage → Set fees → Then assign student

❌ **WRONG:** Assign student without setting fee rates
✅ **RIGHT:** Set fee rates FIRST, then assign student

❌ **WRONG:** Multiple buses without checking which one has which route
✅ **RIGHT:** Track Bus ID, Route ID, Stoppage ID clearly

❌ **WRONG:** Collect fee without assigning student
✅ **RIGHT:** Assign student FIRST, then collect fee

---

**Ye 8 steps follow karo, SABB DONE! 🎉**

Details ke liye documentation padho:
- `BUS_COMPLETE_FLOW_HINDI.md` (Aasaan)
- `BUS_COMPLETE_FLOW_ENGLISH.md` (Detailed)

