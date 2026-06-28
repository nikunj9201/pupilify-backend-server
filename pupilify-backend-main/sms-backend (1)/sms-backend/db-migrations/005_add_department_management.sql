-- Department Management Schema
-- Created for multi-department support (Admission, Exam, Bus)

-- ==================== DEPARTMENTS TABLE ====================
CREATE TABLE IF NOT EXISTS departments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    school_id BIGINT NOT NULL,
    dept_name VARCHAR(100) NOT NULL,
    dept_type ENUM('ADMISSION', 'EXAM', 'BUS') NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (school_id) REFERENCES schools(id),
    UNIQUE KEY unique_dept_type_school (dept_type, school_id)
);

-- ==================== DEPARTMENT HEADS TABLE ====================
CREATE TABLE IF NOT EXISTS department_heads (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    department_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    assigned_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY unique_dept_head (department_id)
);

-- ==================== DEPARTMENT PERMISSIONS TABLE ====================
CREATE TABLE IF NOT EXISTS department_permissions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    department_id BIGINT NOT NULL,
    permission_name VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE CASCADE,
    UNIQUE KEY unique_permission (department_id, permission_name)
);

-- ==================== CREATE INDEXES ====================
CREATE INDEX idx_departments_school ON departments(school_id);
CREATE INDEX idx_department_heads_dept ON department_heads(department_id);
CREATE INDEX idx_department_heads_user ON department_heads(user_id);
CREATE INDEX idx_dept_permissions_dept ON department_permissions(department_id);

-- ==================== DEFAULT PERMISSIONS ====================
-- These will be inserted when a department is created
--
-- ADMISSION DEPARTMENT PERMISSIONS:
-- - STUDENT_ENROLLMENT
-- - ID_CARD_PRINT
-- - STUDENT_RECORDS_VIEW
--
-- EXAM DEPARTMENT PERMISSIONS:
-- - MARKS_UPLOAD
-- - MARKS_VERIFY
-- - RESULT_PRINT
-- - REPORT_CARD_GENERATE
--
-- BUS DEPARTMENT PERMISSIONS:
-- - BUS_MANAGEMENT
-- - ROUTE_MANAGEMENT
-- - STUDENT_ASSIGNMENT
-- - BUS_TRACKING

