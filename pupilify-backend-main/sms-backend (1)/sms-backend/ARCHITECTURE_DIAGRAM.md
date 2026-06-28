# School-State-District Hierarchy Architecture

## Database Relationship Diagram

```
┌─────────────────┐
│   STATES        │
├─────────────────┤
│ id (PK)         │
│ name            │
│ code            │
│ createdAt       │
└────────┬────────┘
         │
         │ 1:N
         │
┌────────▼────────────┐
│   DISTRICTS         │
├─────────────────────┤
│ id (PK)             │
│ name                │
│ code                │
│ state_id (FK)   ───┤─────> STATES
│ createdAt           │
└────────┬────────────┘
         │
         │ 1:N
         │
┌────────▼────────────┐
│   SCHOOLS           │
├─────────────────────┤
│ id (PK)             │
│ schoolName          │
│ mailId              │
│ address             │
│ phoneNumber         │
│ schoolLogo          │
│ state_id (FK)   ───┤─────> STATES
│ district_id (FK)───┤─────> DISTRICTS
│ currentYearId       │
│ subscriptionStatus  │
│ createdAt           │
└────────┬────────────┘
         │
         │ 1:N
         │
┌────────▼────────────┐
│   USERS             │
│   STUDENTS          │
│   TEACHERS          │
│   ... (other data)  │
└─────────────────────┘
```

---

## API Endpoint Hierarchy

```
ROOT: /api/superadmin/schools

├── School Management
│   ├── POST /add
│   │   └─> Create new school
│   ├── PUT /update/{id}
│   │   └─> Update school details
│   ├── GET /all
│   │   └─> List all schools
│   ├── GET /{id}
│   │   └─> Get single school
│   └── DELETE /delete/{id}
│       └─> Delete school
│
├── State & District Assignment (NEW)
│   ├── PUT /{schoolId}/assign-state/{stateId}
│   │   └─> Link school to state
│   └── PUT /{schoolId}/assign-district/{districtId}
│       └─> Link school to district
│
└── Hierarchy Query (NEW)
    ├── GET /by-state/{stateId}
    │   └─> Get schools in state
    └── GET /by-district/{districtId}
        └─> Get schools in district
```

---

## Data Flow: Creating Hierarchical Setup

```
User (Super Admin)
       │
       ├─> 1. Create State
       │        │
       │        └─> POST /api/superadmin/state/create
       │             ├─> State DB: (id=1, name="MP", code="MP")
       │             └─> Response: State object
       │
       ├─> 2. Create District
       │        │
       │        └─> POST /api/district/create
       │             ├─> District DB: (id=1, state_id=1, name="Indore")
       │             └─> Response: District object
       │
       ├─> 3. Create School
       │        │
       │        └─> POST /api/superadmin/schools/add
       │             ├─> School DB: (id=5, schoolName="ABC School")
       │             └─> Response: School object
       │
       ├─> 4. Assign State to School
       │        │
       │        └─> PUT /api/superadmin/schools/5/assign-state/1
       │             ├─> UPDATE schools SET state_id=1 WHERE id=5
       │             └─> Response: {message: "assigned", stateId: 1}
       │
       ├─> 5. Assign District to School
       │        │
       │        └─> PUT /api/superadmin/schools/5/assign-district/1
       │             ├─> UPDATE schools SET district_id=1 WHERE id=5
       │             └─> Response: {message: "assigned", districtId: 1}
       │
       └─> 6. Verify Assignment
                │
                ├─> GET /api/superadmin/schools/5
                │    └─> Response: {id: 5, state: {id:1, name:"MP"}, district: {id:1, name:"Indore"}}
                │
                ├─> GET /api/superadmin/schools/by-state/1
                │    └─> Response: [List of schools in MP state]
                │
                └─> GET /api/superadmin/schools/by-district/1
                     └─> Response: [List of schools in Indore district]
```

---

## Permission & Access Control

```
┌─────────────────────────────────────────────────────────┐
│                    SUPER_ADMIN                          │
│  Can manage: States, Districts, Schools (all levels)    │
│  Endpoints:  /api/superadmin/* (all endpoints)          │
└─────────────────────────────────────────────────────────┘
                          │
        ┌─────────────────┴──────────────────┐
        │                                    │
┌───────▼──────────────────┐   ┌────────────▼────────────────┐
│     STATE_ADMIN          │   │     DISTRICT_ADMIN          │
├──────────────────────────┤   ├─────────────────────────────┤
│ Can manage:              │   │ Can manage:                 │
│ • States (own)           │   │ • Districts (own)           │
│ • Districts (in state)   │   │ • Schools (in district)     │
│ • Schools (in state)     │   │ • Users (in schools)        │
│                          │   │                             │
│ Endpoints:               │   │ Endpoints:                  │
│ /api/superadmin/state/*  │   │ /api/district/*             │
│ /api/district/           │   │ /api/superadmin/schools/    │
│ .../by-state/{stateId}   │   │ .../by-district/{distId}    │
└──────────────────────────┘   └─────────────────────────────┘
        │                                     │
        └──────────────────┬──────────────────┘
                           │
            ┌──────────────▼──────────────┐
            │    ADMIN / PRINCIPAL        │
            ├─────────────────────────────┤
            │ Can manage:                 │
            │ • Schools (own school)      │
            │ • Students                  │
            │ • Teachers                  │
            │ • Classes                   │
            │ • Fees                      │
            │ • Attendance                │
            │ • Results                   │
            │                             │
            │ Endpoints:                  │
            │ /api/admin/*                │
            │ /api/superadmin/schools/{id}│
            └─────────────────────────────┘
```

---

## Complete Request-Response Flow

### Example: Get Schools by State

**Request:**
```http
GET /api/superadmin/schools/by-state/1 HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Processing:**
```
1. JWT Validation
   ├─> Extract token
   ├─> Validate signature
   └─> Check user has STATE_ADMIN or SUPER_ADMIN role

2. Authorization Check
   ├─> Path: /api/superadmin/schools/** (matches /api/superadmin/**)
   ├─> Required Role: SUPER_ADMIN or STATE_ADMIN
   ├─> User Role: STATE_ADMIN ✓

3. Business Logic
   ├─> Call: SchoolService.getSchoolsByState(1)
   ├─> Execute: SchoolRepository.findByStateId(1)
   ├─> SQL: SELECT * FROM schools WHERE state_id = 1
   └─> Fetch relationships: State, District

4. Response Building
   ├─> Convert entities to DTOs
   ├─> Add nested relationships
   └─> JSON serialization
```

**Response:**
```json
HTTP/1.1 200 OK
Content-Type: application/json

[
  {
    "id": 5,
    "schoolName": "ABC Public School",
    "mailId": "abc@school.com",
    "address": "Indore",
    "phoneNumber": "9876543210",
    "schoolLogo": "logo_abc.png",
    "subscriptionStatus": "ACTIVE",
    "state": {
      "id": 1,
      "name": "Madhya Pradesh",
      "code": "MP",
      "createdAt": "2026-04-17T10:00:00"
    },
    "district": {
      "id": 1,
      "name": "Indore",
      "code": "IND",
      "state": { "id": 1, "name": "Madhya Pradesh", "code": "MP" },
      "createdAt": "2026-04-17T10:05:00"
    }
  }
]
```

---

## Implementation Checklist

```
Database Layer
├─ ✅ Add state_id column to schools
├─ ✅ Add district_id column to schools
├─ ✅ Add foreign key constraints
└─ ✅ Create indexes

Entity Layer
├─ ✅ Add @ManyToOne State relationship
├─ ✅ Add @ManyToOne District relationship
└─ ✅ Add JoinColumn annotations

Repository Layer
├─ ✅ Add findByStateId() method
└─ ✅ Add findByDistrictId() method

Service Layer
├─ ✅ Add getSchoolsByState() method
├─ ✅ Add getSchoolsByDistrict() method
├─ ✅ Add assignStateToSchool() method
└─ ✅ Add assignDistrictToSchool() method

Controller Layer
├─ ✅ Add GET /by-state/{stateId} endpoint
├─ ✅ Add GET /by-district/{districtId} endpoint
├─ ✅ Add PUT /{schoolId}/assign-state/{stateId} endpoint
└─ ✅ Add PUT /{schoolId}/assign-district/{districtId} endpoint

Documentation
├─ ✅ Create SCHOOL_STATE_DISTRICT_GUIDE.md
├─ ✅ Create QUICK_REFERENCE.md
├─ ✅ Create SQL_MIGRATION_GUIDE.md
└─ ✅ Create postman_collection_school_state_district.json
```

---

## Performance Considerations

### Query Optimization
```sql
-- Indexed queries (FAST)
SELECT * FROM schools WHERE state_id = 1;           -- O(log n)
SELECT * FROM schools WHERE district_id = 1;        -- O(log n)
SELECT * FROM schools WHERE state_id = 1 AND district_id = 1; -- O(log n)

-- Composite query (for advanced filtering)
SELECT s.*, st.name AS state_name, d.name AS district_name
FROM schools s
LEFT JOIN states st ON s.state_id = st.id
LEFT JOIN districts d ON s.district_id = d.id
WHERE s.state_id = 1;  -- Still fast due to index
```

### Database Indexes
```sql
-- Primary Key (default)
schools.id

-- Foreign Key Indexes (created)
INDEX idx_schools_state_id ON schools(state_id);      -- For state queries
INDEX idx_schools_district_id ON schools(district_id); -- For district queries

-- Composite Index (optional, for advanced use)
INDEX idx_schools_state_district ON schools(state_id, district_id);
```

---

## Summary

✅ **Schools are now hierarchically organized**
- Connected to States (1:N relationship)
- Connected to Districts (1:N relationship)
- Full CRUD operations supported
- Optimized queries with proper indexing
- Complete API documentation
- Ready for production deployment

🎉 **Implementation Complete!**

