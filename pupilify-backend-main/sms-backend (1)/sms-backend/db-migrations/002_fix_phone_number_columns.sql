-- Fix phone_number column in state_managers table
-- Make it nullable (optional field)

ALTER TABLE state_managers
MODIFY phone_number VARCHAR(20) NULL;

-- Fix phone_number column in district_managers table
-- Make it nullable (optional field)

ALTER TABLE district_managers
MODIFY phone_number VARCHAR(20) NULL;

-- Verify the changes
DESC state_managers;
DESC district_managers;

