-- Create Treatment Relationships Table
-- Tracks provider-patient relationships for ABAC policy evaluation
-- Temporal validity with start_date and end_date

CREATE TABLE treatment_relationships (
    relationship_id BIGSERIAL PRIMARY KEY,
    provider_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    patient_id BIGINT NOT NULL REFERENCES patients(patient_id) ON DELETE CASCADE,
    relationship_type VARCHAR(50) NOT NULL CHECK (relationship_type IN ('TREATING', 'CONSULTING', 'EMERGENCY')),
    status VARCHAR(20) DEFAULT 'ACTIVE' NOT NULL CHECK (status IN ('ACTIVE', 'ENDED')),
    start_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valid_date_range CHECK (end_date IS NULL OR end_date >= start_date)
);

-- Create index for efficient ABAC policy lookups
CREATE INDEX idx_treatment_rel_active_lookup ON treatment_relationships(provider_id, patient_id, status);

-- Add comments for documentation
COMMENT ON TABLE treatment_relationships IS 'Provider-patient treatment relationships for ABAC authorization';
COMMENT ON COLUMN treatment_relationships.relationship_type IS 'Type: TREATING (primary), CONSULTING (specialist), EMERGENCY (temporary)';
COMMENT ON COLUMN treatment_relationships.status IS 'ACTIVE relationships grant access rights in ABAC policies';
COMMENT ON COLUMN treatment_relationships.end_date IS 'NULL for ongoing relationships, set for ended relationships';
