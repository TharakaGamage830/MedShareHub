-- Create Additional Performance Indexes
-- Optimized indexes for high-traffic ABAC policy evaluation queries

-- Users table indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_department ON users(department) WHERE department IS NOT NULL;

-- Patients table indexes
CREATE INDEX idx_patients_mrn ON patients(mrn);
CREATE INDEX idx_patients_user_id ON patients(user_id) WHERE user_id IS NOT NULL;

-- Medical records table indexes
CREATE INDEX idx_medical_records_patient ON medical_records(patient_id, created_at DESC);
CREATE INDEX idx_medical_records_sensitivity ON medical_records(sensitivity_level);
CREATE INDEX idx_medical_records_type ON medical_records(record_type);
CREATE INDEX idx_medical_records_created_by ON medical_records(created_by);

-- Treatment relationships indexes (additional to V4)
CREATE INDEX idx_treatment_rel_provider ON treatment_relationships(provider_id, status);
CREATE INDEX idx_treatment_rel_patient ON treatment_relationships(patient_id, status);

-- Consents indexes (additional to V5)
CREATE INDEX idx_consents_patient ON consents(patient_id, expires_at, revoked);
CREATE INDEX idx_consents_granted_to ON consents(granted_to_user_id, revoked) WHERE granted_to_user_id IS NOT NULL;

-- Analyze tables for query optimization
ANALYZE users;
ANALYZE patients;
ANALYZE medical_records;
ANALYZE treatment_relationships;
ANALYZE consents;
ANALYZE access_logs;
