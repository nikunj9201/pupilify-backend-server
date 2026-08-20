# 🚌 Bus Fees Due Report Feature - Automatic Email on Year Change

## Feature Overview

When an academic year is changed in the system, an **Excel report** of all students with pending **bus fees dues** is automatically generated and sent to the **school's registered email**.

---

## How It Works

### 1. **Trigger Point**
```
When Academic Year Changes
    ↓
Check all students with pending bus fees for that year
    ↓
Generate Excel report with all dues
    ↓
Send Excel to school's registered email (mailId)
```

### 2. **What Gets Sent**

#### Email Subject
```
Bus Fees Due Report - [School Name] - Academic Year [Year]
```

#### Email Content
```
Dear Administrator,

Please find attached the Bus Fees Due Report for academic year [Year].

Total Students with Dues: [Count]
Generated On: [Date & Time]

This is an automated report. Please do not reply to this email.

Regards,
Smart School System
```

#### Excel Report Contains

| Column | Description |
|--------|-------------|
| Student ID | Unique student identifier |
| Student Name | Full name of student |
| Enrollment ID | Enrollment number |
| Class | Class/Grade of student |
| Section | Section of student |
| Stoppage | Bus stoppage name |
| Monthly Fee | Fee per month (₹) |
| No. of Months | Number of months selected |
| Total Due Amount | Monthly Fee × Number of Months |
| Payment Frequency | MONTHLY or YEARLY |
| Joining Month | Month when student joined bus |
| **TOTAL DUE** | **Sum of all dues** |

---

## Implementation Details

### New Classes Created

#### 1. **BusFeeReportService.java** (Interface)
```java
Location: src/main/java/com/smartschool/api/service/

Methods:
- generateBusFeesDueExcel(Long schoolId, Long academicYearId): ByteArrayOutputStream
- sendBusFeesReportEmail(Long schoolId, Long academicYearId, String toEmail): void
- getStudentsWithDues(Long schoolId, Long academicYearId): List<StudentMonthlyFeeStructure>
```

#### 2. **BusFeeReportServiceImpl.java** (Implementation)
```java
Location: src/main/java/com/smartschool/api/serviceImpl/

Features:
- Generates Excel with proper formatting (colors, borders, currency format)
- Automatically calculates totals
- Sends email via JavaMailSender
- Includes error handling
```

### Integration Point

**File Modified:** `AcademicYearServiceImpl.java`

**Method:** `changeAcademicYear()`

**When Called:**
```java
// Step 3.1: After sending main year-end emails
busFeeReportService.sendBusFeesReportEmail(
    school.getId(), 
    oldYearId, 
    school.getMailId()
);
```

---

## Email Configuration Required

For emails to be sent, ensure your `application.properties` has:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.from=noreply@smartschool.com
```

---

## Example Scenario

### Before Year Change
```
Academic Year: 2025-26
Students with Bus:
- Raj Kumar: 100 × 10 months = ₹1000 due
- Priya Singh: 120 × 7 months = ₹840 due
- Akash Patel: 100 × 12 months = ₹1200 due

Total Outstanding: ₹3040
```

### On Year Change (June 2026 → July 2026)
```
1. System detects year change
2. Checks all students with bus fees
3. Finds 3 students with dues
4. Generates Excel with all details
5. Sends to: admin@school.com
6. Email includes formatted Excel report
```

---

## Features

✅ **Automatic Generation**
- No manual action needed
- Triggered automatically on year change

✅ **Professional Formatting**
- Blue header with white text
- Currency formatting (₹)
- Borders and alignment
- Automatic total calculation

✅ **Error Handling**
- If no students have dues, email is skipped
- Email failure doesn't fail the year change
- Proper logging of success/failure

✅ **Comprehensive Data**
- All student details included
- All fee information included
- Total due amount calculated
- Easy to track and collect fees

---

## File Structure

```
src/main/java/com/smartschool/api/
├── service/
│   └── BusFeeReportService.java .......................... NEW (Interface)
├── serviceImpl/
│   ├── BusFeeReportServiceImpl.java ....................... NEW (Implementation)
│   └── AcademicYearServiceImpl.java ....................... MODIFIED (Added integration)
```

---

## Database Query Flow

```
1. Academic Year Change Called
   ↓
2. Get all StudentMonthlyFeeStructure records
   WHERE school_id = ? AND academic_year_id = ? AND is_active = true
   ↓
3. Extract student details:
   - Student name, enrollment ID, class, section
   - Stoppage name
   - Monthly fee amount
   - Selected months (from JSON)
   - Total fee amount
   ↓
4. Create Excel with formatted data
   ↓
5. Send email with school's mailId
   ↓
6. Log success/failure
```

---

## Testing

### To Test This Feature

```sql
-- First, ensure you have students with bus fees
SELECT * FROM student_monthly_fee_structure 
WHERE school_id = 1 AND academic_year_id = 1 AND is_active = true;

-- Then trigger year change
POST /api/admin/academic-year/change
{
  "schoolId": 1,
  "newYear": "2026-27",
  "confirmDelete": "CONFIRM",
  "adminUserId": 1
}
```

### Expected Email
- Should receive Excel file at school's email
- File name: `BusFeesReport_[timestamp].xlsx`
- Contains formatted data with all dues

---

## API Endpoints (Direct Access)

### 1. **Get Students with Dues**
```
GET /api/bus-fees-report/students-with-dues/{schoolId}
Query: academicYearId=1

Returns: List of students with pending bus fees
```

### 2. **Generate & Download Excel**
```
GET /api/bus-fees-report/download-excel/{schoolId}
Query: academicYearId=1

Returns: Excel file download
```

### 3. **Send Report Email**
```
POST /api/bus-fees-report/send-email/{schoolId}
Query: academicYearId=1&toEmail=admin@school.com

Sends: Email with Excel attachment
```

---

## Benefits

1. **Automated Process** - No manual email sending needed
2. **Timely Reminder** - Sent exactly when year changes
3. **Complete Information** - All dues details in one place
4. **Professional Format** - Formatted Excel with calculations
5. **Easy Follow-up** - School can immediately follow up on dues
6. **Audit Trail** - Log of what was sent when

---

## Future Enhancements

Possible improvements:
- [ ] Send individual emails to parents
- [ ] Include payment links in email
- [ ] Add late joining penalties
- [ ] Send reminders periodically (not just on year change)
- [ ] Generate PDF in addition to Excel
- [ ] Add SMS notifications option

---

## Troubleshooting

### Issue: Emails not being sent
**Solution:** Check mail configuration in application.properties

### Issue: Excel file is empty
**Solution:** Verify students have active bus fee assignments

### Issue: Wrong email address
**Solution:** Update school's mailId field in database

---

**Implementation Complete! ✅**

Now when academic year changes, all students with bus fee dues will automatically receive an Excel report at the school's email. 🚀

