-- Create Consents Table
-- Patient consent management for data sharing and ABAC policy evaluation

CREATE TABLE consents (
    consent_id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patients(patient_id) ON DELETE CASCADE,
    granted_to_user_id BIGINT REFERENCES users(user_id) ON DELETE CASCADE,
    granted_to_organization VARCHAR(200),
    data_type VARCHAR(50) NOT NULL CHECK (data_type IN ('ALL', 'LAB_RESULTS', 'PRESCRIPTIONS', 'VISIT_NOTES', 'IMAGING', 'BILLING')),
    purpose VARCHAR(100) NOT NULL CHECK (purpose IN ('TREATMENT', 'INSURANCE', 'RESEARCH', 'FAMILY_ACCESS', 'OTHER')),
    granted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    revoked BOOLEAN DEFAULT FALSE,
    revoked_at TIMESTAMP,
    CONSTRAINT consent_recipient_check CHECK (
        granted_to_user_id IS NOT NULL OR granted_to_organization IS NOT NULL
    )
);

-- Create index for efficient consent lookups during ABAC evaluation
CREATE INDEX idx_consents_active_lookup ON consents(patient_id, granted_to_user_id, revoked);

-- Add comments for documentation
COMMENT ON TABLE consents IS 'Patient consent preferences for ABAC-based data sharing authorization';
COMMENT ON COLUMN consents.data_type IS 'Granular data type consent: ALL, LAB_RESULTS, PRESCRIPTIONS, etc.';
COMMENT ON COLUMN consents.purpose IS 'Purpose of data access: TREATMENT, INSURANCE, RESEARCH, FAMILY_ACCESS';
COMMENT ON COLUMN consents.expires_at IS 'NULL for permanent consent, timestamp for temporary access';
COMMENT ON COLUMN consents.revoked IS 'Patients can revoke consent at any time';
