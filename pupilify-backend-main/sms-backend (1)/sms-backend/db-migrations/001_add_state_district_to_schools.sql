-- Migration: Add state_id and district_id to schools table
-- Run this SQL in your MySQL database

ALTER TABLE schools ADD COLUMN state_id BIGINT NULL;
ALTER TABLE schools ADD COLUMN district_id BIGINT NULL;

-- Add foreign key constraints
ALTER TABLE schools
ADD CONSTRAINT fk_schools_state FOREIGN KEY (state_id) REFERENCES states(id);

ALTER TABLE schools
ADD CONSTRAINT fk_schools_district FOREIGN KEY (district_id) REFERENCES districts(id);

-- Create index for faster queries
CREATE INDEX idx_schools_state_id ON schools(state_id);
CREATE INDEX idx_schools_district_id ON schools(district_id);

