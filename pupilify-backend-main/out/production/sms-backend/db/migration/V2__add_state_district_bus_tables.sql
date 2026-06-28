-- Flyway migration: add state, district, bus, boarding points, student assignments, bus fees, principal department assignment

CREATE TABLE IF NOT EXISTS states (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(200) NOT NULL,
  code VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS districts (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(200) NOT NULL,
  code VARCHAR(50),
  state_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_district_state FOREIGN KEY (state_id) REFERENCES states(id)
);

CREATE TABLE IF NOT EXISTS buses (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  registration_no VARCHAR(100) NOT NULL UNIQUE,
  capacity INT,
  driver_name VARCHAR(200),
  school_id BIGINT NOT NULL,
  active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_bus_school FOREIGN KEY (school_id) REFERENCES schools(id)
);

CREATE TABLE IF NOT EXISTS bus_boarding_points (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  bus_id BIGINT NOT NULL,
  name VARCHAR(255) NOT NULL,
  latitude DECIMAL(10,7),
  longitude DECIMAL(10,7),
  seq SMALLINT,
  CONSTRAINT fk_boarding_bus FOREIGN KEY (bus_id) REFERENCES buses(id)
);

CREATE TABLE IF NOT EXISTS student_bus_assignments (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  student_id BIGINT NOT NULL,
  bus_id BIGINT NOT NULL,
  boarding_point_id BIGINT,
  academic_year_id BIGINT NOT NULL,
  assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  active BOOLEAN DEFAULT TRUE,
  CONSTRAINT fk_assignment_student FOREIGN KEY (student_id) REFERENCES students(id),
  CONSTRAINT fk_assignment_bus FOREIGN KEY (bus_id) REFERENCES buses(id),
  CONSTRAINT fk_assignment_boarding FOREIGN KEY (boarding_point_id) REFERENCES bus_boarding_points(id)
);

CREATE TABLE IF NOT EXISTS bus_fees (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  school_id BIGINT NOT NULL,
  bus_id BIGINT,
  amount DECIMAL(10,2) NOT NULL,
  academic_year_id BIGINT NOT NULL,
  fee_type VARCHAR(50) DEFAULT 'BUS',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_busfee_school FOREIGN KEY (school_id) REFERENCES schools(id),
  CONSTRAINT fk_busfee_bus FOREIGN KEY (bus_id) REFERENCES buses(id)
);

CREATE TABLE IF NOT EXISTS principal_department_assignments (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  principal_user_id BIGINT NOT NULL,
  staff_user_id BIGINT NOT NULL,
  department VARCHAR(100) NOT NULL,
  assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  active BOOLEAN DEFAULT TRUE,
  CONSTRAINT fk_principal_user FOREIGN KEY (principal_user_id) REFERENCES users(id),
  CONSTRAINT fk_staff_user FOREIGN KEY (staff_user_id) REFERENCES users(id)
);

