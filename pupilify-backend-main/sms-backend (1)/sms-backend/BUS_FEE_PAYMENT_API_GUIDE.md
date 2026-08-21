# Bus Fee Payment API Guide

## Overview
Updated bus fee collection system to properly handle monthly and yearly fee payments with correct amount distribution across months.

---

## New Features

### 1. **Monthly Fee Payment API** (Improved)
**Endpoint:** `POST /api/buses/admin/collect-fee`

**Purpose:** Pay fees with automatic distribution across multiple months

**Query Parameters:**
- `studentId` (Long) - Student ID
- `academicYearId` (Long) - Academic year ID  
- `amount` (double) - Amount to pay
- `paymentMode` (String) - Payment method (e.g., "CASH", "ONLINE", etc.)

**Example:**
```
POST http://localhost:8080/api/buses/admin/collect-fee?studentId=1&academicYearId=1&amount=700&paymentMode=CASH
```

**Response:**
```json
{
    "id": 15,
    "studentId": 1,
    "studentName": "nikunj",
    "academicYearId": 1,
    "monthYear": "April 2026",
    "amountDue": 100.0,
    "amountPaid": 100.0,
    "status": "PAID",
    "paymentDate": "2026-08-21",
    "paymentMode": "CASH"
}
```

**How it works:**
- Automatically finds due months for the student
- Distributes the payment amount across months
- Creates individual payment logs for each month
- Updates the StudentMonthlyFeeStructure to remove paid months
- Recalculates total due amount

---

### 2. **Yearly/Bulk Fee Payment API** (NEW ✨)
**Endpoint:** `POST /api/buses/admin/collect-yearly-fee`

**Purpose:** Pay complete yearly fees at once with detailed month-wise breakdown

**Query Parameters:**
- `studentId` (Long) - Student ID
- `academicYearId` (Long) - Academic year ID
- `amount` (double) - Amount to pay
- `paymentMode` (String) - Payment method

**Example:**
```
POST http://localhost:8080/api/buses/admin/collect-yearly-fee?studentId=1&academicYearId=1&amount=700&paymentMode=CASH
```

**Response:**
```json
{
    "studentId": 1,
    "totalMonthsDue": 7,
    "monthlyFeeAmount": 100.0,
    "totalDueAmount": 700.0,
    "amountPaid": 700.0,
    "paymentDate": "2026-08-21",
    "paymentMode": "CASH",
    "status": "FULL_PAYMENT",
    "remainingDue": 0.0,
    "monthWisePayments": [
        {
            "month": "October",
            "status": "PAID",
            "amountPaid": "100.0"
        },
        {
            "month": "November",
            "status": "PAID",
            "amountPaid": "100.0"
        },
        // ... more months
    ],
    "monthsPaid": ["October", "November", "December", "January", "February", "March", "April"]
}
```

---

## Database Updates After Payment

### Before Payment:
```sql
StudentMonthlyFeeStructure:
- studentId: 1
- selectedMonths: ["October", "November", "December", "January", "February", "March", "April"]
- totalFeeAmount: 700.0
- active: true

transport_fee_logs:
(empty - no payment records)
```

### After Full Payment (700 amount paid):
```sql
StudentMonthlyFeeStructure:
- studentId: 1
- selectedMonths: [] (empty)
- totalFeeAmount: 0.0
- active: false

transport_fee_logs:
+----+------------+-------------+----------------+--------------+--------------+--------+------------------+------------+
| id | amount_due | amount_paid | month_year     | payment_date | payment_mode | status | academic_year_id | student_id |
+----+------------+-------------+----------------+--------------+--------------+--------+------------------+------------+
| 1  |        100 |         100 | October 2026   | 2026-08-21   | CASH         | PAID   |                1 |          1 |
| 2  |        100 |         100 | November 2026  | 2026-08-21   | CASH         | PAID   |                1 |          1 |
| 3  |        100 |         100 | December 2026  | 2026-08-21   | CASH         | PAID   |                1 |          1 |
| 4  |        100 |         100 | January 2026   | 2026-08-21   | CASH         | PAID   |                1 |          1 |
| 5  |        100 |         100 | February 2026  | 2026-08-21   | CASH         | PAID   |                1 |          1 |
| 6  |        100 |         100 | March 2026     | 2026-08-21   | CASH         | PAID   |                1 |          1 |
| 7  |        100 |         100 | April 2026     | 2026-08-21   | CASH         | PAID   |                1 |          1 |
+----+------------+-------------+----------------+--------------+--------------+--------+------------------+------------+
```

---

## Payment Scenarios

### Scenario 1: Pay Full Year at Once (700 due)
```
Payment: 700.0
Result: All 7 months marked as PAID
Total Due: 0.0
Status: FULL_PAYMENT
```

### Scenario 2: Pay Partial Amount (500 out of 700)
```
Payment: 500.0
Distribution:
- Month 1: 100 (PAID)
- Month 2: 100 (PAID)
- Month 3: 100 (PAID)
- Month 4: 100 (PAID)
- Month 5: 100 (PAID)

Remaining: 3 months (300 due)
Status: PARTIAL_PAYMENT
```

### Scenario 3: Multiple Payments
```
Payment 1: 300 → Pays months 1-3
Payment 2: 250 → Pays months 4-5 + 50 of month 6 (PARTIALLY_PAID)
Payment 3: 100 → Pays remaining month 6 + month 7
Result: All paid
```

---

## Key Improvements

✅ **Correct Payment Distribution**
- Payment amount automatically distributed across due months
- No more single-month payment limitation

✅ **Accurate Total Due Tracking**
- Total fee amount recalculated after each payment
- Shows 0.0 when all fees are paid

✅ **Detailed Payment History**
- Each month's payment tracked in `transport_fee_logs`
- Payment date, mode, and status recorded

✅ **Flexible Payment Options**
- Monthly payments
- Yearly/bulk payments
- Partial payments
- Complete payment tracking

✅ **Database Integrity**
- StudentMonthlyFeeStructure updated correctly
- Transaction-safe operations
- Proper status management (PAID, PARTIALLY_PAID)

---

## Testing

### Test Case 1: Full Year Payment
```bash
curl -X POST "http://localhost:8080/api/buses/admin/collect-yearly-fee?studentId=1&academicYearId=1&amount=700&paymentMode=CASH"
```

Expected: All months paid, totalFeeAmount = 0, status = FULL_PAYMENT

### Test Case 2: Check Fee History
```bash
curl -X GET "http://localhost:8080/api/bus-monthly-fees/student/1/history"
```

Expected: Returns payment logs from transport_fee_logs table

### Test Case 3: Due Report
```bash
curl -X GET "http://localhost:8080/api/bus-monthly-fees/due-report/1?academicYearId=1"
```

Expected: Shows 0 due amount after full payment

---

## Files Modified

1. **BusController.java** - Added `collectYearlyBusFee` endpoint
2. **BusService.java** - Added `collectYearlyBusFee` method signature
3. **BusServiceImpl.java** - Improved `collectBusFee` and added `collectYearlyBusFee` implementation

