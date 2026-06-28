# 🎯 Student Update API - POSTMAN GUIDE (हिंदी में)

## Postman Collection कहाँ से Import करें?

**File:** `postman_student_update.json` 

**Steps:**
1. Postman खोलो
2. Top-left में **Import** button दबाओ
3. **File** tab से `postman_student_update.json` select करो
4. **Import** दबाओ ✅

अब सभी requests ready हैं!

---

## 🔴 COMPULSORY (हमेशा देना पड़ता है)

### 1. **Student ID (URL में)**
```
PUT http://localhost:8080/api/admin/students/1
                                              ↑
                                        यह ID compulsory है
```

**क्यों?** Server को पता चलना चाहिए कि कौन सा student update करना है।

**उदाहरण:**
- `/update/1` ← Student ID = 1
- `/update/5` ← Student ID = 5
- `/update/100` ← Student ID = 100

---

### 2. **studentData Parameter (JSON में)**

```
Body → form-data
━━━━━━━━━━━━━━━━

Key: studentData
Value: {"phoneNo":"9876543210","gender":"Male"}
Type: Text (ध्यान दो: File नहीं, Text होना चाहिए!)
```

**क्यों compulsory है?**
- API को पता चलना चाहिए कि कौन से fields update करने हैं
- भले ही empty object {} भेजो, parameter तो देना ही पड़ता है

**ठीक है (सब काम करेगा):**
```json
{"phoneNo":"9876543210"}  ✅ एक field
{"phoneNo":"9876543210","gender":"Male"}  ✅ दो fields
{}  ✅ खाली भी ठीक है
```

**गलत है (error देगा):**
```
बिना studentData के request भेजना  ❌
"name":"John" डालना (read-only है)  ❌
```

---

## 🟡 OPTIONAL (चाहो तो दो, न चाहो तो न दो)

### File Parameters

```
Body → form-data
━━━━━━━━━━━━━━━━

Key: photo          Type: File  ← OPTIONAL
Key: marksheet      Type: File  ← OPTIONAL
Key: tc             Type: File  ← OPTIONAL
Key: aadharImg      Type: File  ← OPTIONAL
Key: samagraImg     Type: File  ← OPTIONAL
Key: passbookImg    Type: File  ← OPTIONAL
Key: apaarImg       Type: File  ← OPTIONAL
```

**क्यों optional हैं?**
- हर बार सभी files upload करने की ज़रूरत नहीं है
- सिर्फ वो files दो जो update करने हैं
- बाकी files unchanged रहेंगी

**उदाहरण:**

| Scenario | क्या करें | Result |
|----------|---------|--------|
| सिर्फ photo update | photo field fill करो, बाकी skip | सिर्फ photo change होगा |
| सभी documents | सभी 7 files upload करो | सभी update होंगे |
| कोई file नहीं | सभी file fields skip करो | कोई file change नहीं |

---

## 📝 JSON FIELDS - कौन से लगेंगे?

### studentData JSON में ये सब OPTIONAL हैं:

```json
{
  "phoneNo": "9876543210",           // Phone number - optional
  "gender": "Male",                   // Gender - optional
  "address": "123 Main Street",       // Address - optional
  "caste": "General",                 // Caste - optional
  "fatherName": "Ramesh Kumar",       // Father's name - optional
  "motherName": "Priya Sharma",       // Mother's name - optional
  "fatherContactNumber": "9988776655",// Father's phone - optional
  "aadharCardNo": "123456789012",     // Aadhar - optional
  "samagraId": "SAM123456",           // Samagra ID - optional
  "rollNumber": "A001"                // Roll number - optional
}
```

**तुम्हें क्या करना है:**

✅ **सिर्फ वो fields दो जो update करने हो:**
```json
{"phoneNo": "9876543210"}
```

✅ **या एक साथ कई fields:**
```json
{
  "phoneNo": "9876543210",
  "gender": "Male",
  "address": "456 Oak Avenue"
}
```

✅ **या खाली भी:**
```json
{}
```

---

## ❌ ये FIELDS UPDATE नहीं कर सकते

```json
{
  "name": "John Doe",           // ❌ Read-only (onboarding में set)
  "email": "john@email.com",    // ❌ Read-only (onboarding में set)
  "password": "pass123",        // ❌ Read-only (encrypted है)
  "dateOfBirth": "2010-01-01",  // ❌ Read-only (onboarding में set)
  "schoolId": 1,                // ❌ Read-only (promotion से change करो)
  "classId": 2,                 // ❌ Read-only (promotion से change करो)
  "sectionId": 3,               // ❌ Read-only (promotion से change करो)
  "apaarId": "APP123",          // ❌ Read-only (auto-generated)
  "enrollmentId": "ENR001"      // ❌ Read-only (auto-generated)
}
```

**अगर ये change करने हैं तो:**
- **Name/Email/DOB:** Contact Admin
- **Class/Section:** Promotion API use करो
- **School:** New enrollment create करो

---

## 📋 QUICK REFERENCE TABLE

| Parameter | कहाँ | Compulsory? | Type | Example |
|-----------|-----|-----------|------|---------|
| Student ID | URL | 🔴 हाँ | Integer | `/update/1` |
| studentData | Body/form-data | 🔴 हाँ | JSON Text | `{"phoneNo":"9876543210"}` |
| photo | Body/form-data | 🟡 नहीं | File | student.jpg |
| marksheet | Body/form-data | 🟡 नहीं | File | marksheet.pdf |
| tc | Body/form-data | 🟡 नहीं | File | tc.pdf |
| aadharImg | Body/form-data | 🟡 नहीं | File | aadhar.jpg |
| samagraImg | Body/form-data | 🟡 नहीं | File | samagra.jpg |
| passbookImg | Body/form-data | 🟡 नहीं | File | passbook.pdf |
| apaarImg | Body/form-data | 🟡 नहीं | File | apaar.pdf |

---

## 🚀 POSTMAN में Steps

### Step 1: Import करो
1. Postman खोलो
2. **Import** → `postman_student_update.json` चुनो
3. **Import** दबाओ

### Step 2: Request चुनो
- "1️⃣ Update Student - BASIC (JSON Only)" - JSON के बिना files
- "2️⃣ Update Student - WITH PHOTO" - Photo के साथ
- "3️⃣ Update Student - ALL DOCUMENTS" - सभी documents के साथ

### Step 3: Data भरो

**JSON Data के लिए:**
```
Body → form-data tab

Key: studentData
Value: {"phoneNo":"9876543210","gender":"Male"}
Type: Text
```

**Files के लिए:**
```
Body → form-data tab

Key: photo
Type: File
Click Select Files → अपनी image चुनो
```

### Step 4: Send करो
**Send** button दबाओ 🚀

### Step 5: Response देखो
```json
{
  "studentId": 1,
  "name": "John Doe",
  "phoneNo": "9876543210",
  "gender": "Male",
  "studentPhoto": "http://localhost:8080/api/admin/students/files/STU_1234567890_photo.jpg"
}
```

---

## 📊 EXAMPLES

### Example 1️⃣: Basic Update (सिर्फ JSON)
```
Method: PUT
URL: http://localhost:8080/api/admin/students/1

Body (form-data):
- studentData: {"phoneNo":"9876543210","gender":"Male"}

Result: सिर्फ phone और gender update होंगे
```

### Example 2️⃣: Photo के साथ
```
Method: PUT
URL: http://localhost:8080/api/admin/students/1

Body (form-data):
- studentData: {"phoneNo":"9876543210"}
- photo: [student.jpg file]

Result: Phone update + Photo upload
```

### Example 3️⃣: Complete Update (All Documents)
```
Method: PUT
URL: http://localhost:8080/api/admin/students/1

Body (form-data):
- studentData: {
    "phoneNo":"9876543210",
    "fatherName":"Ramesh",
    "motherName":"Priya"
  }
- photo: [student.jpg]
- marksheet: [marksheet.pdf]
- tc: [tc.pdf]
- aadharImg: [aadhar.jpg]
- samagraImg: [samagra.jpg]
- passbookImg: [passbook.pdf]
- apaarImg: [apaar.jpg]

Result: सभी चीजें update
```

---

## ✅ SUCCESS RESPONSE (200 OK)

```json
{
  "studentId": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "phoneNo": "9876543210",
  "gender": "Male",
  "address": "123 Main Street",
  "caste": "General",
  "fatherName": "Ramesh Kumar",
  "motherName": "Priya Sharma",
  "fatherContactNumber": "9988776655",
  "aadharCardNo": "123456789012",
  "samagraId": "SAM123456",
  "rollNumber": "A001",
  "studentPhoto": "http://localhost:8080/api/admin/students/files/STU_1234567890_photo.jpg",
  "lastClassMarksheet": "http://localhost:8080/api/admin/students/files/MRK_1234567890_marksheet.pdf",
  "tcImage": "http://localhost:8080/api/admin/students/files/TC_1234567890_tc.pdf",
  "aadharCardImage": null,
  "samagraIdImage": null,
  "bankPassbookImage": null,
  "apaarCardImage": null
}
```

---

## ❌ ERROR RESPONSES

### 400 BAD REQUEST
```json
{"Error": "Invalid student data JSON - Unrecognized field 'name'"}
```
**मतलब:** कोई गलत field दिया है (name edit-only है)

### 404 NOT FOUND
```json
{"Error": "Student not found with ID: 99999"}
```
**मतलब:** वह student database में नहीं है

### 500 INTERNAL SERVER ERROR
```json
{"Error": "File processing failed - ..."}
```
**मतलब:** File upload में कोई problem है

---

## 🎓 CHEATSHEET

```
🔴 COMPULSORY:
  ✓ studentData parameter (भले ही empty {})
  ✓ Student ID in URL

🟡 OPTIONAL:
  ✓ सभी file parameters
  ✓ JSON के अंदर सभी fields

📝 RULES:
  ✓ Content-Type: multipart/form-data
  ✓ studentData को Text में रखो, File नहीं
  ✓ Files को File type में रखो
  ✓ Read-only fields को skip करो

✅ SUCCESS:
  ✓ HTTP 200 = अच्छे से हुआ
  ✓ Response में updated values आएंगे

❌ ERRORS:
  ✓ 400 = Bad request (गलत data)
  ✓ 404 = Student not found
  ✓ 500 = Server error
```

---

## 🎯 FINAL ANSWER (तुम्हारे सवाल का)

### "कया compulsory है?"
1. **studentData** - हमेशा भेजना पड़ता है (भले ही `{}` भी हो)
2. **Student ID** - URL में देना पड़ता है

### "कया नहीं है?"
1. **Files** - सभी optional हैं, सिर्फ जो update करने हो वही दो
2. **JSON fields** - सभी optional हैं, जो update करने हो वही दो

### "क्यों compulsory हैं?"
- **studentData**: बताने के लिए कि update क्या करना है
- **Student ID**: बताने के लिए कि किस student को update करना है

### "क्यों optional हैं?"
- **Files**: हर बार सभी documents upload करने की ज़रूरत नहीं
- **JSON fields**: सभी fields हमेशा update नहीं होते

---

**अब Postman में चला लो! Sabkuch काम करेगा!** ✅

