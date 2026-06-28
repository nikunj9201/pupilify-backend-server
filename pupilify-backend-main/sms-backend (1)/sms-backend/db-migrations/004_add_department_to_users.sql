-- Migration: Add Department fields to users table
-- Purpose: Enable department login functionality
-- Date: 2026-04-26

ALTER TABLE users ADD COLUMN department VARCHAR(255) NULL;
ALTER TABLE users ADD COLUMN department_id BIGINT NULL;

-- Create index for department_id for faster queries
CREATE INDEX idx_department_id ON users(department_id);
CREATE INDEX idx_department ON users(department);

-- Add unique constraint for department_id (each department should have only one user)
ALTER TABLE users ADD CONSTRAINT uq_department_id UNIQUE(department_id);

