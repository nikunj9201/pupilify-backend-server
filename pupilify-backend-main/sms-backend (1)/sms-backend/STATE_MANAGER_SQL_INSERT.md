# 🗄️ State Manager - Database Insert Statements

## Direct SQL Insert (Alternative Method)

If you want to insert State Manager test data directly in database:

### Before Using This

⚠️ **Important:** If using SQL, you MUST encrypt passwords!

For testing, you can use plain passwords. But in production, use BCrypt.

---

## SQL Commands to Insert Test Data

### Option 1: Insert All 3 State Managers

```sql
-- Insert State Manager 1 - Rajesh Kumar
INSERT INTO state_managers 
(name, email, password, role, state_id, school_id, active, created_at, updated_at)
VALUES 
('Rajesh Kumar', 'rajesh@state.com', 'rajesh@123', 'STATE_MANAGER', 1, NULL, true, UNIX_TIMESTAMP(NOW())*1000, UNIX_TIMESTAMP(NOW())*1000);

-- Insert State Manager 2 - Priya Singh
INSERT INTO state_managers 
(name, email, password, role, state_id, school_id, active, created_at, updated_at)
VALUES 
('Priya Singh', 'priya.singh@state.com', 'priya@123', 'STATE_MANAGER', 2, NULL, true, UNIX_TIMESTAMP(NOW())*1000, UNIX_TIMESTAMP(NOW())*1000);

-- Insert State Manager 3 - Amit Patel
INSERT INTO state_managers 
(name, email, password, role, state_id, school_id, active, created_at, updated_at)
VALUES 
('Amit Patel', 'amit.patel@state.com', 'amit@123', 'STATE_MANAGER', 3, NULL, true, UNIX_TIMESTAMP(NOW())*1000, UNIX_TIMESTAMP(NOW())*1000);
```

---

## Verify Insert

After inserting, verify with:

```sql
SELECT * FROM state_managers;
```

You should see:
```
id | name | email | password | role | state_id | active
1  | Rajesh Kumar | rajesh@state.com | rajesh@123 | STATE_MANAGER | 1 | 1
2  | Priya Singh | priya.singh@state.com | priya@123 | STATE_MANAGER | 2 | 1
3  | Amit Patel | amit.patel@state.com | amit@123 | STATE_MANAGER | 3 | 1
```

---

## Login with Direct SQL Insert Data

After inserting directly with SQL, you can still login:

```
POST /api/auth/manager-login

{
  "username": "rajesh@state.com",
  "password": "rajesh@123"
}
```

**Note:** Passwords in SQL are plain text. When application encrypts them, login still works because it compares plain text password with what you enter.

---

## Update State Manager (SQL)

To update a State Manager:

```sql
UPDATE state_managers 
SET name = 'Rajesh Kumar Updated', 
    email = 'rajesh.updated@state.com',
    updated_at = UNIX_TIMESTAMP(NOW())*1000
WHERE id = 1;
```

---

## Delete State Manager (SQL)

To delete a State Manager:

```sql
DELETE FROM state_managers WHERE id = 1;
```

---

## Check Active Status

```sql
-- Get all active State Managers
SELECT * FROM state_managers WHERE active = true;

-- Get all inactive State Managers
SELECT * FROM state_managers WHERE active = false;
```

---

## Disable State Manager Login

```sql
UPDATE state_managers SET active = false WHERE id = 1;
```

Now Rajesh Kumar cannot login even with correct password.

---

## Enable State Manager Login Again

```sql
UPDATE state_managers SET active = true WHERE id = 1;
```

---

## Get State Manager by Email

```sql
SELECT * FROM state_managers WHERE email = 'rajesh@state.com';
```

---

## Get All State Managers for a State

```sql
SELECT * FROM state_managers WHERE state_id = 1;
```

---

## Count Total State Managers

```sql
SELECT COUNT(*) as total_state_managers FROM state_managers;
```

---

## Clear All State Managers (⚠️ DANGEROUS!)

```sql
DELETE FROM state_managers;
```

⚠️ **WARNING:** This deletes all State Managers. Only use for testing/development.

---

## Complete Reset (Fresh Start)

```sql
-- Drop and recreate table
DROP TABLE IF EXISTS state_managers;

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

-- Insert fresh test data
INSERT INTO state_managers 
(name, email, password, role, state_id, school_id, active, created_at, updated_at)
VALUES 
('Rajesh Kumar', 'rajesh@state.com', 'rajesh@123', 'STATE_MANAGER', 1, NULL, true, UNIX_TIMESTAMP(NOW())*1000, UNIX_TIMESTAMP(NOW())*1000),
('Priya Singh', 'priya.singh@state.com', 'priya@123', 'STATE_MANAGER', 2, NULL, true, UNIX_TIMESTAMP(NOW())*1000, UNIX_TIMESTAMP(NOW())*1000),
('Amit Patel', 'amit.patel@state.com', 'amit@123', 'STATE_MANAGER', 3, NULL, true, UNIX_TIMESTAMP(NOW())*1000, UNIX_TIMESTAMP(NOW())*1000);
```

---

## Using with Encrypted Passwords

If you want to use encrypted passwords, use BCrypt hash:

```sql
-- Example with encrypted password
-- BCrypt hash of "rajesh@123" 
INSERT INTO state_managers 
(name, email, password, role, state_id, school_id, active, created_at, updated_at)
VALUES 
('Rajesh Kumar', 'rajesh@state.com', '$2a$10$...bcrypt_hash_here...', 'STATE_MANAGER', 1, NULL, true, UNIX_TIMESTAMP(NOW())*1000, UNIX_TIMESTAMP(NOW())*1000);
```

To generate BCrypt hash, use online tool: https://bcrypt.online

---

## ⚠️ Important Notes

1. **Testing vs Production:**
   - Testing: Plain text passwords OK
   - Production: Always use encrypted passwords

2. **Email Unique:**
   - Each State Manager must have unique email
   - Cannot insert same email twice

3. **State ID Reference:**
   - Make sure state_id exists in your states table
   - Otherwise might violate foreign key

4. **Timestamps:**
   - created_at: When manager was created
   - updated_at: Last time manager was updated
   - Use `UNIX_TIMESTAMP(NOW())*1000` for current time in milliseconds

5. **Active Status:**
   - true (1): Manager can login
   - false (0): Manager cannot login

---

## Which Method to Use?

| Method | When to Use | Pros | Cons |
|--------|------------|------|------|
| **API Create** | Always in dev/prod | Follows business logic, encrypted password | More steps |
| **SQL Insert** | Quick testing | Fast, direct | No encryption, bypasses validation |

**Recommendation:** Use API method for proper implementation.

---

**Happy testing! 🚀**

