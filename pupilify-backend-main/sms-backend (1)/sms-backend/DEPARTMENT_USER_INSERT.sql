-- Sample Department User Data for Testing
-- Purpose: Insert test department records with credentials
-- Date: 2026-04-26

-- ✅ Note: Use hashed passwords! These are example plain passwords shown for reference only
-- Use BCryptPasswordEncoder to hash these before inserting

-- Example 1: Mathematics Department
-- Plain Password: Math@123
-- Hashed: $2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
INSERT INTO users (username, password, role, school_id, department, department_id, active)
VALUES (
  'math_department@school.com',
  '$2a$10$slYQmyNdGzins2HVf1Y3fO0g2Wk2MIHZSZQz/vZyQqMPfDZM2WrFG', -- Math@123
  'ROLE_ADMIN',
  1,
  'Mathematics Department',
  1,
  true
);

-- Example 2: Science Department
-- Plain Password: Science@456
INSERT INTO users (username, password, role, school_id, department, department_id, active)
VALUES (
  'science_department@school.com',
  '$2a$10$x7H4ZxqZYxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', -- Science@456
  'ROLE_ADMIN',
  1,
  'Science Department',
  2,
  true
);

-- Example 3: English Department
-- Plain Password: English@789
INSERT INTO users (username, password, role, school_id, department, department_id, active)
VALUES (
  'english_department@school.com',
  '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', -- English@789
  'ROLE_ADMIN',
  1,
  'English Department',
  3,
  true
);

-- Example 4: Social Studies Department
-- Plain Password: Social@101
INSERT INTO users (username, password, role, school_id, department, department_id, active)
VALUES (
  'social_department@school.com',
  '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', -- Social@101
  'ROLE_ADMIN',
  1,
  'Social Studies Department',
  4,
  true
);

-- Verify inserted records
-- SELECT id, username, department, department_id, role, active FROM users WHERE department_id IS NOT NULL;

-- ⚠️ IMPORTANT: To generate actual BCrypt hashes, use this Java code:

/*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String password1 = "Math@123";
        String password2 = "Science@456";
        String password3 = "English@789";
        String password4 = "Social@101";

        System.out.println("Math@123 hash: " + encoder.encode(password1));
        System.out.println("Science@456 hash: " + encoder.encode(password2));
        System.out.println("English@789 hash: " + encoder.encode(password3));
        System.out.println("Social@101 hash: " + encoder.encode(password4));
    }
}
*/

-- Run the above Java code and replace the hash values in the INSERT statements above

