-- Flyway Migration: Create principal_notes table

CREATE TABLE IF NOT EXISTS principal_notes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    school_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    content LONGTEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    FOREIGN KEY (school_id) REFERENCES schools(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for faster queries
CREATE INDEX idx_principal_notes_school_id ON principal_notes(school_id);
CREATE INDEX idx_principal_notes_user_id ON principal_notes(user_id);
CREATE INDEX idx_principal_notes_is_active ON principal_notes(is_active);
CREATE INDEX idx_principal_notes_created_at ON principal_notes(created_at);

