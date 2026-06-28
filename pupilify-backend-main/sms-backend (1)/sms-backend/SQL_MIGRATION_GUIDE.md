# SQL Migration Script - Add State and District to Schools

Run these SQL commands in your MySQL database to add state and district support:

```sql
-- 1. Add columns to schools table
ALTER TABLE schools ADD COLUMN state_id BIGINT NULL;
ALTER TABLE schools ADD COLUMN district_id BIGINT NULL;

-- 2. Add foreign key constraints
ALTER TABLE schools 
ADD CONSTRAINT fk_schools_state FOREIGN KEY (state_id) REFERENCES states(id);

ALTER TABLE schools 
ADD CONSTRAINT fk_schools_district FOREIGN KEY (district_id) REFERENCES districts(id);

-- 3. Create indexes for faster queries
CREATE INDEX idx_schools_state_id ON schools(state_id);
CREATE INDEX idx_schools_district_id ON schools(district_id);

-- 4. (OPTIONAL) Verify the changes
SELECT * FROM information_schema.columns WHERE table_name='schools' AND column_name IN ('state_id', 'district_id');
```

## How to Run:

1. Open MySQL Workbench or MySQL CLI
2. Connect to your database (smart_school_pro)
3. Copy and paste the SQL commands above
4. Execute them

After running these migrations, your schools table will have state_id and district_id columns linked to the states and districts tables.

