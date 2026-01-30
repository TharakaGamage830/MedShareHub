-- Create Medical Records Table
-- Stores medical records with sensitivity levels for ABAC policy evaluation
-- Uses JSONB for FHIR R4 compatibility

CREATE TABLE medical_records (
    record_id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patients(patient_id) ON DELETE CASCADE,
    record_type VARCHAR(50) NOT NULL CHECK (record_type IN ('LAB_RESULT', 'PRESCRIPTION', 'VISIT_NOTE', 'IMAGING', 'DIAGNOSIS', 'PROCEDURE')),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    sensitivity_level VARCHAR(50) NOT NULL CHECK (sensitivity_level IN ('PUBLIC', 'STANDARD', 'PSYCHIATRIC', 'HIV', 'CRITICAL')),
    content JSONB NOT NULL,
    created_by BIGINT REFERENCES users(user_id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create trigger to auto-update updated_at timestamp
CREATE TRIGGER update_medical_records_updated_at
    BEFORE UPDATE ON medical_records
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Add comments for documentation
COMMENT ON TABLE medical_records IS 'Medical records with FHIR R4 compatible JSON content';
COMMENT ON COLUMN medical_records.sensitivity_level IS 'Data sensitivity for ABAC: PUBLIC, STANDARD, PSYCHIATRIC, HIV, CRITICAL';
COMMENT ON COLUMN medical_records.content IS 'FHIR R4 compatible JSON structure for medical data';
COMMENT ON COLUMN medical_records.record_type IS 'Type of medical record for categorization and filtering';
