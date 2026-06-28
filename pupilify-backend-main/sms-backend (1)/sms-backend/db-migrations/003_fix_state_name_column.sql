-- Fix state_name and state_name columns - make them nullable
ALTER TABLE state_managers
MODIFY state_name VARCHAR(255) NULL;

