# Bus Management System - Complete Guide

## Overview

The Bus Management System has been completely refactored to support a flexible monthly fee structure with late joining support. This guide explains the new structure and how to use it.

## Key Changes

### 1. **Stoppage Model Fix**
- ❌ **Removed**: `fee` field from Stoppage entity
- ✅ **Why**: Fees are now managed separately in dedicated fee structure tables, preventing duplication

### 2. **New Fee Structure Tables**

#### `BusFeeRateStructure` Table
Stores the monthly fee rate for each stoppage per bus and academic year.

**Columns:**
- `id` - Primary key
- `stoppage_id` - Reference to stoppage
- `bus_id` - Reference to bus
- `school_id` - Reference to school
- `academic_year_id` - Reference to academic year
- `monthly_fee_amount` - Fee per month (e.g., 100 rupees)
- `total_months` - Total months in academic year (e.g., 12)
- `academic_year_start_month` - When academic year starts (e.g., "July")
- `is_active` - Active/inactive status

**Example:**
- Stoppage: "Stop 1"
- Bus: "MH12AB1234"
- Monthly Fee: ₹100
- Total Months: 12 (July to June)

#### `StudentMonthlyFeeStructure` Table
Tracks individual student fee assignments with month selections and late joining support.

**Columns:**
- `id` - Primary key
- `student_id` - Reference to student
- `stoppage_id` - Reference to stoppage
- `bus_id` - Reference to bus
- `school_id` - Reference to school
- `academic_year_id` - Reference to academic year
- `monthly_fee_amount` - Fee per month (inherited from rate structure)
- `selected_months` - JSON array of months student is enrolled for (e.g., `["July", "August", "September", ...]`)
- `joining_month` - Month when student joined (for late joining calculation)
- `joining_year` - Academic year
- `payment_frequency` - MONTHLY or YEARLY payment option
- `total_fee_amount` - Calculated total (monthly_fee × number of months)
- `assignment_date` - When assigned
- `is_active` - Active/inactive status

## How It Works

### Scenario 1: Student Joins at Beginning (July)
```
Monthly Fee: ₹100
Selected Months: [July, August, September, October, November, December, January, February, March, April]
Joining Month: July
Total Fee: 100 × 10 = ₹1000
Due Report Shows: 1000 as total due across all 10 months
```

### Scenario 2: Student Joins Late (October - 3 months late)
```
Monthly Fee: ₹100
Selected Months: [October, November, December, January, February, March, April]
Joining Month: October
Total Fee: 100 × 7 = ₹700
Due Report Shows: 700 as total due (automatically calculated from joining month)
```

## API Endpoints

### 1. Bus Fee Rate Structure Management

#### Create Fee Rate Structure
```http
POST /api/bus-monthly-fees/rate-structure/create
Query Parameters:
- stoppageId: 1
- busId: 1
- schoolId: 1
- academicYearId: 1
- monthlyFeeAmount: 100
- totalMonths: 12
- academicYearStartMonth: July
```

#### Get Fee Rate Structures by Bus
```http
GET /api/bus-monthly-fees/rate-structure/bus/{busId}
Query Parameters:
- schoolId: 1
- academicYearId: 1
```

#### Update Fee Rate Structure
```http
PUT /api/bus-monthly-fees/rate-structure/{rateStructureId}
Query Parameters:
- monthlyFeeAmount: 120
```

### 2. Student Monthly Fee Assignment

#### Assign Student to Bus with Monthly Fees (New System)
```http
POST /api/bus-monthly-fees/assign-student
Content-Type: application/json

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

#### Assign Student with Late Joining
```http
POST /api/bus-monthly-fees/assign-student
Content-Type: application/json

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

#### Get Student Fee Assignment
```http
GET /api/bus-monthly-fees/assignment/{studentId}
Query Parameters:
- academicYearId: 1
```

#### Update Student Fee Assignment (Change months or payment frequency)
```http
PUT /api/bus-monthly-fees/assignment/{assignmentId}
Query Parameters:
- selectedMonths: July&selectedMonths=August&selectedMonths=September
- paymentFrequency: YEARLY
```

### 3. Reports and Due Collection

#### Get Monthly Fees Due Report
```http
GET /api/bus-monthly-fees/due-report/{schoolId}
Query Parameters:
- academicYearId: 1

Response Example:
[
  {
    "studentId": 1,
    "studentName": "John Doe",
    "enrollmentId": "E001",
    "stoppage": "Stop 1",
    "monthlyFeeAmount": 100,
    "selectedMonths": ["July", "August", "September", "October"],
    "joiningMonth": "July",
    "totalMonthsSelected": 4,
    "totalFeeAmount": 400,
    "paymentFrequency": "MONTHLY"
  }
]
```

#### Get Student Monthly Fees Breakdown
```http
GET /api/bus-monthly-fees/breakdown/{studentId}
Query Parameters:
- academicYearId: 1

Response Example:
[
  {
    "month": "July",
    "feeAmount": 100,
    "stoppage": "Stop 1",
    "status": "DUE"
  },
  {
    "month": "August",
    "feeAmount": 100,
    "stoppage": "Stop 1",
    "status": "DUE"
  }
]
```

## Payment Options

### 1. Monthly Payment
- Student pays ₹100 per month
- More flexible for families
- Easier to handle partial payments

### 2. Yearly Payment
- Student pays entire amount upfront (e.g., ₹1000 for 10 months)
- Better for cash flow management
- Can offer discount if needed

## Database Migration

If you have existing data with fees in the `Stoppage` table, you need to migrate it:

```sql
-- Step 1: Create new tables (already done by JPA)
-- Step 2: Migrate existing fee data
INSERT INTO bus_fee_rate_structure (stoppage_id, bus_id, school_id, academic_year_id, monthly_fee_amount, total_months, academic_year_start_month, is_active)
SELECT 
  s.id,
  r.bus_id,
  r.school_id,
  1, -- Default academic year, adjust as needed
  s.fee,
  12,
  'July',
  s.is_active
FROM bus_stoppages s
JOIN bus_routes r ON s.route_id = r.id
WHERE s.fee IS NOT NULL AND s.fee > 0;
```

## Postman Collection

A complete Postman collection is available at: `postman_bus_complete_collection.json`

### Steps to Use:
1. Import the JSON file into Postman
2. Set environment variables:
   - `base_url`: http://localhost:8080
   - `token`: Your JWT token
   - `schoolId`: Your school ID
   - `busId`: Your bus ID
   - Other IDs as needed
3. Execute requests in order

## Complete Workflow Example

### Step 1: Create Bus and Route
```http
POST /api/buses/admin/{schoolId}/add
- registrationNo: MH12AB1234
- capacity: 50
```

### Step 2: Create Route
```http
POST /api/admin/routes/{schoolId}/create
- busId: 1
- routeName: Route A
```

### Step 3: Add Stoppages
```http
POST /api/admin/routes/{schoolId}/{routeId}/stoppages/add
- stopName: Stop 1
```
(Repeat for multiple stops)

### Step 4: Create Fee Rate Structure
```http
POST /api/bus-monthly-fees/rate-structure/create
- stoppageId: 1
- busId: 1
- schoolId: 1
- academicYearId: 1
- monthlyFeeAmount: 100
- totalMonths: 12
- academicYearStartMonth: July
```

### Step 5: Assign Students with Monthly Fees
```http
POST /api/bus-monthly-fees/assign-student
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

### Step 6: Get Due Reports
```http
GET /api/bus-monthly-fees/due-report/{schoolId}
- academicYearId: 1
```

### Step 7: Collect Payments
```http
POST /api/buses/admin/collect-fee
- studentId: 1
- academicYearId: 1
- amount: 100
- paymentMode: CASH
```

## Key Features

✅ **No More Duplication**: Fees are removed from Stoppage, managed in separate tables
✅ **Monthly Flexibility**: Students can select which months they need
✅ **Late Joining Support**: Automatically calculates fees from joining month
✅ **Dual Payment Options**: Both monthly and yearly payment options
✅ **Comprehensive Reports**: Due reports with month-wise breakdown
✅ **Easy Calculations**: Fee × Number of months = Total due

## Important Notes

1. **Always create fee rate structure before assigning students**
2. **Joining month must match the month in selectedMonths**
3. **Late joining: If student joins Oct, only include Oct onwards in selectedMonths**
4. **Payment frequency doesn't affect the calculation, only the collection method**
5. **Use the Postman collection for easy testing and reference**

## Troubleshooting

**Issue**: Student assignment fails with "Fee rate structure not found"
**Solution**: Create the fee rate structure first using `/api/bus-monthly-fees/rate-structure/create`

**Issue**: Total fee amount seems wrong
**Solution**: Check that selectedMonths count matches the expected number. Total = monthlyFeeAmount × selectedMonths.size()

**Issue**: Can't update stoppage fee
**Solution**: Fees are now managed in BusFeeRateStructure, not in Stoppage. Use `/api/bus-monthly-fees/rate-structure/{rateStructureId}` to update.

## API Request/Response Examples

### Create Fee Rate Structure
**Request:**
```json
POST /api/bus-monthly-fees/rate-structure/create?stoppageId=1&busId=1&schoolId=1&academicYearId=1&monthlyFeeAmount=100&totalMonths=12&academicYearStartMonth=July
```

**Response:**
```json
{
  "id": 1,
  "stoppage": {
    "id": 1,
    "stopName": "Stop 1",
    "isActive": true
  },
  "bus": {
    "id": 1,
    "registrationNo": "MH12AB1234"
  },
  "school": {
    "id": 1
  },
  "academicYear": {
    "id": 1,
    "year": 2026
  },
  "monthlyFeeAmount": 100,
  "totalMonths": 12,
  "academicYearStartMonth": "July",
  "isActive": true
}
```

### Assign Student with Monthly Fees
**Request:**
```json
POST /api/bus-monthly-fees/assign-student

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

**Response:**
```json
{
  "id": 1,
  "student": {
    "id": 1,
    "name": "John Doe",
    "enrollmentId": "E001"
  },
  "stoppage": {
    "id": 1,
    "stopName": "Stop 1"
  },
  "bus": {
    "id": 1,
    "registrationNo": "MH12AB1234"
  },
  "school": {
    "id": 1
  },
  "academicYear": {
    "id": 1,
    "year": 2026
  },
  "monthlyFeeAmount": 100,
  "selectedMonths": "[\"July\",\"August\",\"September\",\"October\",\"November\",\"December\",\"January\",\"February\",\"March\",\"April\"]",
  "joiningMonth": "July",
  "joiningYear": 2026,
  "paymentFrequency": "MONTHLY",
  "totalFeeAmount": 1000,
  "assignmentDate": "2026-08-20",
  "isActive": true
}
```

### Get Due Report
**Response:**
```json
[
  {
    "studentId": 1,
    "studentName": "John Doe",
    "enrollmentId": "E001",
    "stoppage": "Stop 1",
    "monthlyFeeAmount": 100,
    "selectedMonths": ["July", "August", "September", "October", "November", "December", "January", "February", "March", "April"],
    "joiningMonth": "July",
    "totalMonthsSelected": 10,
    "totalFeeAmount": 1000,
    "paymentFrequency": "MONTHLY"
  }
]
```

## Summary

This new system provides:
- **Cleaner Architecture**: Separate fee management from stoppage data
- **Flexibility**: Support for partial year enrollment and late joining
- **Better Reporting**: Month-wise fee breakdown
- **Scalability**: Easy to extend for discounts, refunds, etc.

Happy coding! 🚌📊

