# Principal Notes API Documentation

## Overview
The Principal Notes API allows principals to create, read, update, and delete notes for their school. Notes are school-specific and only the creator can edit or delete their own notes.

## Features
- Create personal notes for school
- Read notes created by you or any principal in your school
- Update notes (only creator can edit)
- Soft delete notes (mark as inactive)
- Hard delete notes (permanent deletion)

## Base URL
```
http://localhost:8080/api/admin/notes
```

## Authentication
All endpoints require JWT token in Authorization header:
```
Authorization: Bearer {your_jwt_token}
```

## Endpoints

### 1. Create a Note
**POST** `/school/{schoolId}`

**Description:** Create a new note for the school

**Path Parameters:**
- `schoolId` (Long): ID of the school

**Request Body:**
```json
{
    "title": "Budget Planning for 2024",
    "content": "Need to plan the budget for the new academic year. Consider increasing teacher salaries and infrastructure development."
}
```

**Response (201 Created):**
```json
{
    "id": 1,
    "school": {
        "id": 1,
        "schoolName": "My School"
    },
    "createdBy": {
        "id": 1,
        "username": "principal@school.com"
    },
    "title": "Budget Planning for 2024",
    "content": "Need to plan the budget for the new academic year...",
    "createdAt": "2026-05-25T10:30:00",
    "updatedAt": "2026-05-25T10:30:00",
    "isActive": true
}
```

**Error Response (400):**
```json
{
    "error": "Error: Note title is required"
}
```

---

### 2. Get All Notes for a School
**GET** `/school/{schoolId}`

**Description:** Retrieve all active notes for the school

**Path Parameters:**
- `schoolId` (Long): ID of the school

**Response (200 OK):**
```json
[
    {
        "id": 1,
        "school": {
            "id": 1,
            "schoolName": "My School"
        },
        "createdBy": {
            "id": 1,
            "username": "principal@school.com"
        },
        "title": "Budget Planning",
        "content": "Content here...",
        "createdAt": "2026-05-25T10:30:00",
        "updatedAt": "2026-05-25T10:30:00",
        "isActive": true
    },
    {
        "id": 2,
        "school": {
            "id": 1,
            "schoolName": "My School"
        },
        "createdBy": {
            "id": 1,
            "username": "principal@school.com"
        },
        "title": "Staff Meeting Agenda",
        "content": "Content here...",
        "createdAt": "2026-05-25T11:00:00",
        "updatedAt": "2026-05-25T11:00:00",
        "isActive": true
    }
]
```

---

### 3. Get a Specific Note
**GET** `/school/{schoolId}/note/{noteId}`

**Description:** Retrieve a specific note by ID

**Path Parameters:**
- `schoolId` (Long): ID of the school
- `noteId` (Long): ID of the note

**Response (200 OK):**
```json
{
    "id": 1,
    "school": {
        "id": 1,
        "schoolName": "My School"
    },
    "createdBy": {
        "id": 1,
        "username": "principal@school.com"
    },
    "title": "Budget Planning for 2024",
    "content": "Need to plan the budget for the new academic year...",
    "createdAt": "2026-05-25T10:30:00",
    "updatedAt": "2026-05-25T10:30:00",
    "isActive": true
}
```

**Error Response (404):**
```json
{
    "error": "Note not found"
}
```

---

### 4. Update a Note
**PUT** `/school/{schoolId}/note/{noteId}`

**Description:** Update an existing note (only creator can edit)

**Path Parameters:**
- `schoolId` (Long): ID of the school
- `noteId` (Long): ID of the note

**Request Body:**
```json
{
    "title": "Budget Planning for 2024 - REVISED",
    "content": "Updated content for budget planning..."
}
```

**Response (200 OK):**
```json
{
    "id": 1,
    "school": {
        "id": 1,
        "schoolName": "My School"
    },
    "createdBy": {
        "id": 1,
        "username": "principal@school.com"
    },
    "title": "Budget Planning for 2024 - REVISED",
    "content": "Updated content for budget planning...",
    "createdAt": "2026-05-25T10:30:00",
    "updatedAt": "2026-05-25T10:35:00",
    "isActive": true
}
```

**Error Response (403):**
```json
{
    "error": "Error: Only the creator can edit this note"
}
```

---

### 5. Delete a Note (Soft Delete)
**DELETE** `/school/{schoolId}/note/{noteId}`

**Description:** Soft delete a note (marks as inactive, data remains in DB)

**Path Parameters:**
- `schoolId` (Long): ID of the school
- `noteId` (Long): ID of the note

**Response (200 OK):**
```json
"Note deleted successfully"
```

**Error Response (403):**
```json
{
    "error": "Error: Only the creator can delete this note"
}
```

---

### 6. Permanently Delete a Note (Hard Delete)
**DELETE** `/school/{schoolId}/note/{noteId}/hard`

**Description:** Permanently delete a note from the database

**Path Parameters:**
- `schoolId` (Long): ID of the school
- `noteId` (Long): ID of the note

**Response (200 OK):**
```json
"Note permanently deleted"
```

**Error Response (403):**
```json
{
    "error": "Error: Only the creator can delete this note"
}
```

---

## Permissions & Security

### Who Can Access?
- **Principals** of a school can create, read, update, and delete their own notes
- **School Admins** can view all notes in their school
- Users from different schools cannot access each other's notes

### Authorization Rules
1. **Create Note:** User must belong to the school
2. **Read Notes:** User must belong to the school (can see all active notes)
3. **Update Note:** Only the creator can edit
4. **Delete Note:** Only the creator can delete

### Database Security
- Foreign key constraints ensure data integrity
- School_id and User_id are non-nullable
- Soft deletes preserve audit trail
- Created timestamps are immutable

---

## Error Codes

| Status Code | Description |
|---|---|
| 200 | Success |
| 201 | Created |
| 400 | Bad Request (validation error) |
| 403 | Forbidden (permission denied) |
| 404 | Not Found |
| 500 | Internal Server Error |

---

## Example Workflow

### 1. Create a Note
```bash
curl -X POST http://localhost:8080/api/admin/notes/school/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Important Meeting",
    "content": "Meeting scheduled for next Monday at 10 AM"
  }'
```

### 2. Get All Notes
```bash
curl -X GET http://localhost:8080/api/admin/notes/school/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 3. Update a Note
```bash
curl -X PUT http://localhost:8080/api/admin/notes/school/1/note/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Important Meeting - UPDATED",
    "content": "Updated content here"
  }'
```

### 4. Delete a Note (Soft Delete)
```bash
curl -X DELETE http://localhost:8080/api/admin/notes/school/1/note/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 5. Permanently Delete a Note
```bash
curl -X DELETE http://localhost:8080/api/admin/notes/school/1/note/1/hard \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## Database Schema

```sql
CREATE TABLE principal_notes (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    school_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    content LONGTEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_principal_notes_school_id (school_id),
    INDEX idx_principal_notes_user_id (user_id),
    INDEX idx_principal_notes_is_active (is_active),
    INDEX idx_principal_notes_created_at (created_at)
);
```

---

## Implementation Summary

The Notes feature has been fully implemented with:

1. **Entity (Note.java)** - JPA entity with all required fields
2. **Repository (NoteRepository.java)** - Custom queries for CRUD operations
3. **Service (NoteService.java)** - Business logic interface
4. **Service Implementation (NoteServiceImpl.java)** - Complete business logic
5. **Controller (NoteController.java)** - REST API endpoints
6. **Database Migration (V3__add_principal_notes_table.sql)** - Flyway migration

All endpoints are authenticated with JWT and authorized based on school ownership.

