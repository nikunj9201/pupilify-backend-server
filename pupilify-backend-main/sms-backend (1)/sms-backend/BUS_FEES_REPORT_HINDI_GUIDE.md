# 🚌 Bus Fees Due Report - Quick Reference (हिंदी)

## Feature Kya Hai?

**Jab academic year change hota hai, toh automatic Excel file generate hoti hai**
- Sabhi students ke bus fees dues ke saath
- Excel file school ke registered email pe bhej di jati hai
- Koi manual action nahi lagta - sab automatic ho jata hai!

---

## Kaise Kaam Karta Hai?

```
Academic Year Change
    ↓
System checks: "Kaunse students ke bus fees due hain?"
    ↓
Excel file banati hai sab dues ke saath
    ↓
Email bhejti hai: admin@school.com
    ↓
School wale instantly due dekh sakte hain!
```

---

## Excel Mein Kya Hota Hai?

### Column Details

| Column | Matlab |
|--------|--------|
| Student ID | Student ka unique ID |
| Student Name | Student ka naam |
| Enrollment ID | Enrollment number |
| Class | Class/Grade |
| Section | Section |
| Stoppage | Bus stop ka naam |
| Monthly Fee | Mahine ka fee (₹100 etc) |
| No. of Months | Kitne months ke liye |
| Total Due | Monthly Fee × Months = Total |
| Payment Frequency | MONTHLY ya YEARLY |
| Joining Month | Jab se student bus le raha hai |
| **TOTAL DUE** | **Sab ka sum** |

### Example
```
Name: Raj Kumar
Monthly Fee: ₹100
Months: 10 (July-April)
Total Due: ₹1000

Name: Priya Singh
Monthly Fee: ₹120
Months: 7 (October-April)
Total Due: ₹840

====================
TOTAL DUE: ₹1840
```

---

## Email Kab Bhejta Hai?

**Sirf ek hi time:** Jab academic year change hota hai!

### Example Timeline
```
June 2026: Year 2025-26 chal raha hai
         → Students ke bus dues: ₹5000, ₹3000, ₹2500

June 30, 2026: Admin ne "Change Year" button click kiya
              ↓
              System tray karta hai year change
              ↓
              Check: "Bus fees due hain?"
              ↓
              Haan! 3 students ke due hain
              ↓
              Excel banato (formatted, colorful)
              ↓
              admin@school.com ko email bhejo
              ↓
              Done! Excel file attached!

July 1, 2026: Year 2026-27 shuru ho gaya
            School admin ke paas excel hai
            Sab details clear hain
            Collection shuru kar sakte hain
```

---

## Email Mein Kya Likha Hota Hai?

```
Subject: Bus Fees Due Report - [School Name] - Academic Year 2025-26

Body:
Dear Administrator,

Please find attached the Bus Fees Due Report for academic year 2025-26.

Total Students with Dues: 3
Generated On: 30-06-2026 14:30:00

This is an automated report. Please do not reply to this email.

Regards,
Smart School System

---
Attachment: BusFeesReport_[timestamp].xlsx
```

---

## Files Banaye Gaye

### 1. **BusFeeReportService.java**
```
Ye interface hai - kya karna hai ye define karta hai
Location: service/ folder
```

### 2. **BusFeeReportServiceImpl.java**
```
Ye actual code hai jo Excel banata hai aur email bhejta hai
Location: serviceImpl/ folder
```

### 3. **AcademicYearServiceImpl.java**
```
Ye purani file tha, usmein integration ki
Jab year change hota hai, ye service call hoti hai
```

---

## Setup Kya Lagta Hai?

### Email Configuration (application.properties)

```properties
# Gmail setup (agar Gmail use kar rahe ho)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Ya apna mail server use kar sakte ho
spring.mail.host=your-mail-server.com
spring.mail.port=587
spring.mail.username=admin@school.com
spring.mail.password=password
```

---

## Test Kaise Karo?

### Database mein check karo pehle
```sql
-- Students with bus dues dekho
SELECT * FROM student_monthly_fee_structure 
WHERE school_id = 1 
AND academic_year_id = 1 
AND is_active = true;

-- Agar data hai toh email bhejega
```

### Year Change API call karo
```
POST http://localhost:8080/api/admin/academic-year/change

Body:
{
  "schoolId": 1,
  "newYear": "2026-27",
  "confirmDelete": "CONFIRM",
  "adminUserId": 1
}
```

### Check karo
```
1. Email inbox mein dekho
2. Excel file download karo
3. Data sahi hai na check karo
```

---

## Fayda Kya Hai?

✅ **Automatic** - Manual email nahi bhejni padti
✅ **On Time** - Exactly year change ke time mein jata hai
✅ **Professional** - Formatted Excel with colors aur borders
✅ **Complete Data** - Sab student details ek jagah
✅ **Easy Follow-up** - School instantly collection shuru kar sakte hain

---

## Agar Problem Ho?

### Problem: Email nahi aa raha
**Samadhan:** Mail configuration check karo application.properties mein

### Problem: Excel khaali hai
**Samadhan:** Database mein students ke bus fee assignments hone chahiye

### Problem: Galat email par ja raha hai
**Samadhan:** School ke profile mein mailId update karo

---

## API (Direct Use Ke Liye)

### 1. Students with Dues dekho
```
GET /api/bus-fees-report/students-with-dues/1?academicYearId=1
```

### 2. Excel download karo
```
GET /api/bus-fees-report/download-excel/1?academicYearId=1
```

### 3. Email manually bhejo
```
POST /api/bus-fees-report/send-email/1?academicYearId=1&toEmail=admin@school.com
```

---

## Summary

```
🎯 Purpose: Jab year change ho, sab bus fees dues ka auto email

📧 How: Academic Year Change → Excel Generate → Email Send

📊 Content: Student name, enrollment, class, section, stoppage, fee details, total due

⏰ When: Automatically on year change

✅ Status: Ready to use!
```

**Sab kuch ready hai! Jab year change ho, Excel automatically bhej jayega! 🚀**

---

## Quick Checklist

- [ ] Email configuration set up kiya?
- [ ] School ka mailId database mein correct hai?
- [ ] Students ke bus fee assignments hain?
- [ ] Development server pe test kiya?
- [ ] Production pe deploy kiya?

**Done? Great! Feature ab ready hai! ✨**

