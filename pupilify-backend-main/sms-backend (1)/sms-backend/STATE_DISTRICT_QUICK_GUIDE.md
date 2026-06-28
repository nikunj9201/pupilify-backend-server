# 🗺️ State & District Management API Guide

## Quick Start

### State Create करने के लिए

**Endpoint:**
```
POST http://localhost:8080/api/superadmin/state/create
```

**Body (JSON):**
```json
{
  "name": "Maharashtra",
  "code": "MH"
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Maharashtra",
  "code": "MH",
  "createdAt": "2026-05-19T15:30:00"
}
```

---

### District Create करने के लिए

**Endpoint:**
```
POST http://localhost:8080/api/district/create
```

**Body (JSON):**
```json
{
  "name": "Pune",
  "code": "PUN",
  "state": {
    "id": 1
  }
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Pune",
  "code": "PUN",
  "state": {
    "id": 1,
    "name": "Maharashtra",
    "code": "MH"
  },
  "createdAt": "2026-05-19T15:30:00"
}
```

---

## सभी APIs

### STATE APIs

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/superadmin/state/all` | सभी states देखें |
| GET | `/api/superadmin/state/{id}` | एक state देखें |
| POST | `/api/superadmin/state/create` | नया state create करें |
| PUT | `/api/superadmin/state/update/{id}` | State update करें |
| DELETE | `/api/superadmin/state/delete/{id}` | State delete करें |

### DISTRICT APIs

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/district/all` | सभी districts देखें |
| GET | `/api/district/all?stateId=1` | किसी state के districts देखें |
| GET | `/api/district/{id}` | एक district देखें |
| POST | `/api/district/create` | नया district create करें |
| PUT | `/api/district/update/{id}` | District update करें |
| DELETE | `/api/district/delete/{id}` | District delete करें |

---

## 📋 Example Data

### States to Create

```
Maharashtra (MH)
Gujarat (GJ)
Uttar Pradesh (UP)
Karnataka (KA)
Tamil Nadu (TN)
```

### Districts (Maharashtra)

```
Pune
Mumbai
Nagpur
Aurangabad
Nashik
```

### Districts (Gujarat)

```
Ahmedabad
Surat
Vadodara
Rajkot
```

---

## 🧪 Postman में कैसे Test करें

### Step 1: Collection Import करें
1. Postman खोलें
2. Collections → Import
3. `postman_state_district_management.json` select करें
4. Import करें

### Step 2: QUICK SETUP Section चलाएं
1. "QUICK SETUP (Run in Order)" section खोलें
2. सभी requests को एक-एक करके चलाएं:
   - 1. Create Maharashtra
   - 2. Create Pune District
   - 3. Create Mumbai District
   - 4. View All States
   - 5. View All Districts

### Step 3: अपने States & Districts Create करें
1. "STATE MANAGEMENT" section में जाएं
2. "Create State" request को customize करें
3. अपना state का नाम और code डालें
4. Send करें

---

## 💡 Tips

### State Code क्या होना चाहिए?
- 2-3 अक्षरों का code (जैसे: MH, GJ, UP, KA)
- Unique होना चाहिए
- Uppercase में लिखें

### District Create करते समय
- State का ID जरूर दें (state.id)
- State पहले create हो चुका हो तो ही district create करें
- District का नाम unique होना चाहिए

### Error आ रहा है?
1. Server चल रहा है? (Port 8080)
2. State ID सही है?
3. JSON format सही है?
4. Database connection है?

---

## 📊 Example Request/Response

### State Create Request
```bash
curl -X POST http://localhost:8080/api/superadmin/state/create \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Maharashtra",
    "code": "MH"
  }'
```

### State Create Response
```json
{
  "id": 1,
  "name": "Maharashtra",
  "code": "MH",
  "createdAt": "2026-05-19T15:30:45.123456"
}
```

### District Create Request
```bash
curl -X POST http://localhost:8080/api/district/create \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Pune",
    "code": "PUN",
    "state": {
      "id": 1
    }
  }'
```

### District Create Response
```json
{
  "id": 1,
  "name": "Pune",
  "code": "PUN",
  "state": {
    "id": 1,
    "name": "Maharashtra",
    "code": "MH",
    "createdAt": "2026-05-19T15:30:45.123456"
  },
  "createdAt": "2026-05-19T15:31:12.789456"
}
```

---

## 🗂️ Database Schema

### states table
```sql
CREATE TABLE states (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  code VARCHAR(10),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### districts table
```sql
CREATE TABLE districts (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  code VARCHAR(10),
  state_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (state_id) REFERENCES states(id)
);
```

---

## ✅ Checklist

- [ ] Postman collection import किया
- [ ] Server चल रहा है
- [ ] QUICK SETUP चलाया
- [ ] 1-2 states create किए
- [ ] 2-3 districts create किए
- [ ] सभी states/districts को view किया
- [ ] Editing और deletion भी test की

---

**File:** `postman_state_district_management.json`
**Status:** ✅ Ready to Use
**Date:** May 19, 2026

