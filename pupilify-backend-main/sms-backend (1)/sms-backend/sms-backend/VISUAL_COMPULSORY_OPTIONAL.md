# 📊 VISUAL GUIDE - Kya Compulsory Hai Aur Kya Nahi

## 🔴 COMPULSORY vs 🟡 OPTIONAL - DIAGRAM

```
╔════════════════════════════════════════════════════════════════╗
║          PUT /api/admin/students/update/1                      ║
║                                              ↑                 ║
║                                    🔴 COMPULSORY              ║
║                                    (Student ID)               ║
╚════════════════════════════════════════════════════════════════╝

┌─── BODY (multipart/form-data) ──────────────────────────────────┐
│                                                                   │
│ ┌─────────────────────────────────────────────────────────────┐  │
│ │ KEY: studentData          🔴 COMPULSORY (हमेशा)           │  │
│ │ TYPE: Text                                                 │  │
│ │ VALUE: {                                                   │  │
│ │   "phoneNo": "9876543210",   🟡 OPTIONAL                  │  │
│ │   "gender": "Male",          🟡 OPTIONAL                  │  │
│ │   "address": "123 St"        🟡 OPTIONAL                  │  │
│ │ }                                                          │  │
│ └─────────────────────────────────────────────────────────────┘  │
│                                                                   │
│ ┌─────────────────────────────────────────────────────────────┐  │
│ │ KEY: photo               🟡 OPTIONAL (चाहो तो दो)         │  │
│ │ TYPE: File                                                 │  │
│ │ VALUE: [select file]                                       │  │
│ └─────────────────────────────────────────────────────────────┘  │
│                                                                   │
│ ┌─────────────────────────────────────────────────────────────┐  │
│ │ KEY: marksheet           🟡 OPTIONAL (चाहो तो दो)         │  │
│ │ TYPE: File                                                 │  │
│ │ VALUE: [select file]                                       │  │
│ └─────────────────────────────────────────────────────────────┘  │
│                                                                   │
│ ┌─────────────────────────────────────────────────────────────┐  │
│ │ KEY: tc                  🟡 OPTIONAL (चाहो तो दो)         │  │
│ │ TYPE: File                                                 │  │
│ │ VALUE: [select file]                                       │  │
│ └─────────────────────────────────────────────────────────────┘  │
│                                                                   │
│ [और 4 और files भी optional हैं: aadharImg, samagraImg,        │
│  passbookImg, apaarImg]                                        │
│                                                                   │
└───────────────────────────────────────────────────────────────────┘
```

---

## 📝 FIELDS - कौन से Optional हैं?

```
EDITABLE (तुम update कर सकते हो):
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Person Details:
├─ phoneNo          🟡 OPTIONAL
├─ gender           🟡 OPTIONAL
├─ address          🟡 OPTIONAL
├─ caste            🟡 OPTIONAL

Family Details:
├─ fatherName           🟡 OPTIONAL
├─ motherName           🟡 OPTIONAL
├─ fatherContactNumber  🟡 OPTIONAL

ID Numbers:
├─ aadharCardNo     🟡 OPTIONAL
├─ samagraId        🟡 OPTIONAL
├─ rollNumber       🟡 OPTIONAL

Documents (Files):
├─ photo            🟡 OPTIONAL
├─ marksheet        🟡 OPTIONAL
├─ tc               🟡 OPTIONAL
├─ aadharImg        🟡 OPTIONAL
├─ samagraImg       🟡 OPTIONAL
├─ passbookImg      🟡 OPTIONAL
└─ apaarImg         🟡 OPTIONAL


NON-EDITABLE (तुम update नहीं कर सकते):
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Core Details:
├─ name             ❌ READ-ONLY (onboarding में set)
├─ email            ❌ READ-ONLY (onboarding में set)
├─ password         ❌ READ-ONLY (encrypted)
├─ dateOfBirth      ❌ READ-ONLY (onboarding में set)

School Info:
├─ schoolId         ❌ READ-ONLY (promotion API से)
├─ classId          ❌ READ-ONLY (promotion API से)
├─ sectionId        ❌ READ-ONLY (promotion API से)
├─ academicYear     ❌ READ-ONLY (special API से)

Auto-Generated:
├─ studentId        ❌ READ-ONLY (primary key)
├─ enrollmentId     ❌ READ-ONLY (auto-generated)
└─ apaarId          ❌ READ-ONLY (auto-generated)
```

---

## 🎯 REQUEST STRUCTURE

```
HTTP METHOD:  PUT
URL:          http://localhost:8080/api/admin/students/update/1
                                                          ↑
                                             🔴 COMPULSORY (ID)

HEADERS:
- Content-Type: multipart/form-data

BODY (multipart/form-data):

┌─ studentData (🔴 COMPULSORY)
│  Type: Text
│  Value: JSON string with optional fields
│  Example: {"phoneNo":"9876543210"}
│
├─ photo (🟡 OPTIONAL)
│  Type: File
│  Value: Image file (JPG/PNG)
│
├─ marksheet (🟡 OPTIONAL)
│  Type: File
│  Value: Document file (PDF/JPG)
│
├─ tc (🟡 OPTIONAL)
│  Type: File
│  Value: Document file (PDF/JPG)
│
├─ aadharImg (🟡 OPTIONAL)
│  Type: File
│  Value: Image file (JPG/PNG)
│
├─ samagraImg (🟡 OPTIONAL)
│  Type: File
│  Value: Image file (JPG/PNG)
│
├─ passbookImg (🟡 OPTIONAL)
│  Type: File
│  Value: Document file (PDF/JPG)
│
└─ apaarImg (🟡 OPTIONAL)
   Type: File
   Value: Image file (JPG/PNG)
```

---

## ✅ VALID REQUESTS (तुम ये भेज सकते हो)

```
REQUEST 1: सिर्फ JSON
━━━━━━━━━━━━━━━━━━━
Body:
  studentData: {"phoneNo":"9876543210"}

Result: ✅ 200 OK
         सिर्फ phone update होगा
         कोई file update नहीं


REQUEST 2: JSON + 1 File
━━━━━━━━━━━━━━━━━━━━━━━
Body:
  studentData: {"phoneNo":"9876543210","gender":"Male"}
  photo: [student.jpg]

Result: ✅ 200 OK
         Phone, gender, photo सब update होंगे


REQUEST 3: JSON + All Files
━━━━━━━━━━━━━━━━━━━━━━━━━━
Body:
  studentData: {"phoneNo":"9876543210"}
  photo: [file]
  marksheet: [file]
  tc: [file]
  aadharImg: [file]
  samagraImg: [file]
  passbookImg: [file]
  apaarImg: [file]

Result: ✅ 200 OK
         सभी files update होंगे


REQUEST 4: खाली JSON (कुछ नहीं)
━━━━━━━━━━━━━━━━━━━━━━━━━━━
Body:
  studentData: {}

Result: ✅ 200 OK
         कोई field update नहीं होगा
         लेकिन request successful है


REQUEST 5: JSON + Selective Fields
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Body:
  studentData: {
    "fatherName": "Ramesh Kumar",
    "motherName": "Priya",
    "fatherContactNumber": "9988776655"
  }

Result: ✅ 200 OK
         सिर्फ ये 3 fields update होंगे
```

---

## ❌ INVALID REQUESTS (ये error देगा)

```
REQUEST 1: studentData नहीं दिया
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Body:
  photo: [file]

Result: ❌ 400 BAD REQUEST
         "Required parameter 'studentData' not found"


REQUEST 2: गलत Field (read-only) add किया
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Body:
  studentData: {"name":"John","phoneNo":"9876543210"}
                         ↑
                    read-only है!

Result: ❌ 400 BAD REQUEST
         "Unrecognized field 'name'"


REQUEST 3: गलत Student ID
━━━━━━━━━━━━━━━━━━━━━━━━
PUT /api/admin/students/update/99999
                              ↑
                         student नहीं है

Body:
  studentData: {"phoneNo":"9876543210"}

Result: ❌ 404 NOT FOUND
         "Student not found with ID: 99999"


REQUEST 4: Invalid JSON Format
━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Body:
  studentData: {'phoneNo':'9876543210'}
                   ↑ ↑        ↑ ↑
                 single quotes!

Result: ❌ 400 BAD REQUEST
         "Invalid JSON data"


REQUEST 5: serverData की जगह studentData लिखना भूल गया
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Body:
  serverData: {"phoneNo":"9876543210"}  ❌ गलत!

Result: ❌ 400 BAD REQUEST
         Parameter नहीं मिला
```

---

## 📊 COMPULSORY vs OPTIONAL - QUICK COMPARISON

```
┌───────────┬──────────────────┬─────────────────┐
│  Element  │   Compulsory?    │     Why?        │
├───────────┼──────────────────┼─────────────────┤
│ Student   │ 🔴 Yes (in URL)  │ किस student     │
│ ID        │                  │ को update करना  │
│           │                  │ है बताने के लिए │
├───────────┼──────────────────┼─────────────────┤
│ student   │ 🔴 Yes (always)  │ क्या update करना│
│ Data      │                  │ है बताने के लिए │
│           │                  │ (भले ही empty) │
├───────────┼──────────────────┼─────────────────┤
│ JSON      │ 🟡 No (all opt)  │ सभी fields      │
│ Fields    │                  │ optional हैं    │
├───────────┼──────────────────┼─────────────────┤
│ File      │ 🟡 No (all opt)  │ सभी files       │
│ Parameters│                  │ optional हैं    │
└───────────┴──────────────────┴─────────────────┘
```

---

## 🚀 POSTMAN में STEP-BY-STEP

### Step 1: Request Type सेट करो
```
Method: PUT (dropdown से चुनो)
```

### Step 2: URL दो
```
http://localhost:8080/api/admin/students/update/1
```

### Step 3: Body सेट करो
```
1. "Body" tab पर जाओ
2. "form-data" radio button select करो
3. Key-Value pairs add करो:
   - Key: "studentData"
     Type: "Text" (फुंक्शन icon से)
     Value: {"phoneNo":"9876543210"}
   
   - Key: "photo"
     Type: "File" (फुंक्शन icon से)
     Value: [file select करो]
```

### Step 4: Send करो
```
"Send" button दबाओ
```

### Step 5: Response देखो
```
Status: 200 OK
Body में updated student data मिलेगा
```

---

## 💡 KEY POINTS TO REMEMBER

```
🔴 COMPULSORY:
   1. studentData parameter ALWAYS दो
   2. Student ID in URL दो

🟡 OPTIONAL:
   1. सभी JSON fields optional हैं
   2. सभी file parameters optional हैं
   3. जो update करना हो वही दो, बाकी skip करो

📝 FORMAT:
   1. studentData को Text में रखो (File नहीं!)
   2. Files को File type में रखो
   3. JSON format सही हो (double quotes)
   4. Content-Type: multipart/form-data

✅ SUCCESS:
   1. HTTP 200 = successful
   2. Response में updated values आएंगे

❌ ERRORS:
   1. 400 = Invalid request
   2. 404 = Student not found
   3. 500 = Server error
```

---

## 🎓 FINAL SUMMARY

| What? | Compulsory? | Why? | Where? |
|-------|-----------|------|--------|
| Student ID | 🔴 Yes | किस student? | URL |
| studentData | 🔴 Yes | क्या update? | Body |
| JSON fields | 🟡 No | सब optional | Body JSON |
| Files | 🟡 No | सब optional | Body files |

**तो कुल मिलाकर:**
- ✅ 2 चीजें compulsory हैं (Student ID + studentData)
- ✅ बाकी सब optional है
- ✅ जो चाहिए वही दो! 🚀

