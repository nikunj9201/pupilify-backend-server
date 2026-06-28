# SmartSchool API - Complete Guide

## Overview
This is a complete school management system API with support for Super Admin, State Manager, District Manager, School Principal, Teachers, and Students.

## Database Setup

### Create Tables for State and District Managers

```sql
-- State Managers Table
CREATE TABLE state_managers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'STATE_MANAGER',
    state_id BIGINT NOT NULL,
    school_id BIGINT,
    active BOOLEAN DEFAULT TRUE,
    created_at BIGINT NOT NULL,
    updated_at BIGINT
);

-- District Managers Table
CREATE TABLE district_managers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'DISTRICT_MANAGER',
    state_id BIGINT NOT NULL,
    district_id BIGINT NOT NULL,
    school_id BIGINT,
    active BOOLEAN DEFAULT TRUE,
    created_at BIGINT NOT NULL,
    updated_at BIGINT
);
```

---

## API Authentication

### 1. User Login (School Admin/Teacher/Student)
**Endpoint:** `POST /api/auth/login`

**Request:**
```json
{
  "username": "principal@school.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "jwt_token_here",
  "role": "ROLE_ADMIN",
  "userId": 1,
  "schoolId": 1,
  "active": true,
  "name": "Principal Name",
  "schoolName": "School Name",
  "academicYear": "2025-26"
}
```

---

### 2. State Manager Login
**Endpoint:** `POST /api/auth/manager-login`

**Request:**
```json
{
  "username": "state_manager@example.com",
  "password": "statepass123"
}
```

**Response:**
```json
{
  "id": 1,
  "email": "state_manager@example.com",
  "role": "STATE_MANAGER",
  "name": "Rajesh Kumar",
  "active": true,
  "message": "Login successful",
  "success": true
}
```

---

### 3. District Manager Login
**Endpoint:** `POST /api/auth/manager-login`

**Request:**
```json
{
  "username": "district_manager@example.com",
  "password": "districtpass123"
}
```

**Response:**
```json
{
  "id": 1,
  "email": "district_manager@example.com",
  "role": "DISTRICT_MANAGER",
  "name": "Priya Singh",
  "active": true,
  "message": "Login successful",
  "success": true
}
```

---

## State Manager APIs

### Create State Manager
**Endpoint:** `POST /api/superadmin/state-managers/create`

**Required Role:** SUPER_ADMIN

**Request:**
```json
{
  "name": "Rajesh Kumar",
  "email": "rajesh@state.com",
  "password": "statepass123",
  "stateId": 1,
  "role": "STATE_MANAGER"
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Rajesh Kumar",
  "email": "rajesh@state.com",
  "role": "STATE_MANAGER",
  "stateId": 1,
  "schoolId": null,
  "active": true,
  "createdAt": 1650000000000,
  "updatedAt": 1650000000000
}
```

### Get All State Managers
**Endpoint:** `GET /api/superadmin/state-managers/all`

**Headers:**
```
Authorization: Bearer {token}
```

### Get State Manager by ID
**Endpoint:** `GET /api/superadmin/state-managers/{id}`

**Headers:**
```
Authorization: Bearer {token}
```

### Update State Manager
**Endpoint:** `PUT /api/superadmin/state-managers/update/{id}`

**Request:**
```json
{
  "name": "Rajesh Kumar Updated",
  "email": "rajesh.updated@state.com",
  "password": "newpass123",
  "active": true
}
```

### Delete State Manager
**Endpoint:** `DELETE /api/superadmin/state-managers/delete/{id}`

---

## District Manager APIs

### Create District Manager
**Endpoint:** `POST /api/superadmin/district-managers/create`

**Required Role:** SUPER_ADMIN

**Request:**
```json
{
  "name": "Priya Singh",
  "email": "priya@district.com",
  "password": "districtpass123",
  "stateId": 1,
  "districtId": 1,
  "role": "DISTRICT_MANAGER"
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Priya Singh",
  "email": "priya@district.com",
  "role": "DISTRICT_MANAGER",
  "stateId": 1,
  "districtId": 1,
  "schoolId": null,
  "active": true,
  "createdAt": 1650000000000,
  "updatedAt": 1650000000000
}
```

### Get All District Managers
**Endpoint:** `GET /api/superadmin/district-managers/all`

### Get District Manager by ID
**Endpoint:** `GET /api/superadmin/district-managers/{id}`

### Get District Managers by State
**Endpoint:** `GET /api/superadmin/district-managers/state/{stateId}`

### Get District Managers by District
**Endpoint:** `GET /api/superadmin/district-managers/district/{districtId}`

### Update District Manager
**Endpoint:** `PUT /api/superadmin/district-managers/update/{id}`

**Request:**
```json
{
  "name": "Priya Singh Updated",
  "email": "priya.updated@district.com",
  "password": "newpass123",
  "active": true
}
```

### Delete District Manager
**Endpoint:** `DELETE /api/superadmin/district-managers/delete/{id}`

---

## Bus Management APIs

### Create Bus
**Endpoint:** `POST /api/buses`

**Request:**
```json
{
  "registrationNo": "MP09AB1234",
  "routeName": "North Route",
  "monthlyFee": 1500,
  "schoolId": 1
}
```

### Get All Buses
**Endpoint:** `GET /api/buses`

**Query Parameters:**
- `includeStudents=true` - To get buses with assigned students

### Assign Student to Bus
**Endpoint:** `POST /api/buses/{busId}/students`

**Request:**
```json
{
  "studentId": 1,
  "boardingPoint": "MG Road",
  "busFee": 1500
}
```

---

## Access Control & Permissions

### State Manager Permissions:
- Can view all schools in their state
- Can manage all students in their state
- Can manage all district managers in their state
- Cannot access other state's data

### District Manager Permissions:
- Can view all schools in their district
- Can manage all students in their district
- Cannot access other district's data

### Principal Permissions:
- Can manage staff and departments
- Can manage students in their school
- Can assign departments to staff members

---

## Response Status Codes

| Code | Meaning |
|------|---------|
| 200 | Success |
| 201 | Created |
| 400 | Bad Request |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Not Found |
| 500 | Server Error |

---

## Error Response Example

```json
{
  "message": "Invalid email or password",
  "success": false
}
```

---

## Setup Instructions

1. **Import Postman Collection:**
   - Download `postman_collection_complete.json`
   - Open Postman → Import → Select the file

2. **Set Base URL:**
   - In Postman, update the `baseUrl` variable to `http://localhost:8080`

3. **Create Test Data:**
   - Login as Super Admin first
   - Create State Manager
   - Create District Manager
   - Create Schools
   - Create Students
   - Assign Students to Buses

---

## Notes

- All passwords are stored encrypted in database
- Tokens expire after a set time (configure in JWT settings)
- School ID is optional for managers (can be null)
- Active status determines if manager can login
- Timestamps are stored as milliseconds since epoch


