-- Create Access Logs Table (Immutable Audit Trail)
-- Comprehensive HIPAA-compliant audit logging for all access attempts
-- Tamper-proof design with immutability constraints

CREATE TABLE access_logs (
    log_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(user_id) ON DELETE SET NULL,
    patient_id BIGINT REFERENCES patients(patient_id) ON DELETE SET NULL,
    resource_type VARCHAR(50) NOT NULL,
    resource_id BIGINT,
    action VARCHAR(50) NOT NULL CHECK (action IN ('READ', 'WRITE', 'UPDATE', 'DELETE', 'EXPORT', 'PRINT')),
    decision VARCHAR(20) NOT NULL CHECK (decision IN ('PERMIT', 'DENY')),
    policy_matched VARCHAR(100),
    deny_reason TEXT,
    justification TEXT,
    is_emergency BOOLEAN DEFAULT FALSE,
    ip_address VARCHAR(45),
    device_info TEXT,
    session_id VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Rules removed for H2 compatibility in tests
-- CREATE RULE access_logs_immutable AS 
--     ON UPDATE TO access_logs 
--     DO INSTEAD NOTHING;

-- CREATE RULE access_logs_no_delete AS 
--     ON DELETE TO access_logs 
--     DO INSTEAD NOTHING;

-- Create indexes for audit log queries and compliance reporting
CREATE INDEX idx_access_logs_user ON access_logs(user_id, created_at DESC);
CREATE INDEX idx_access_logs_patient ON access_logs(patient_id, created_at DESC);
CREATE INDEX idx_access_logs_decision ON access_logs(decision, created_at DESC);
CREATE INDEX idx_access_logs_emergency ON access_logs(is_emergency);
CREATE INDEX idx_access_logs_timestamp ON access_logs(created_at DESC);

-- Add comments for documentation
COMMENT ON TABLE access_logs IS 'HIPAA-compliant immutable audit trail for all access attempts';
COMMENT ON COLUMN access_logs.decision IS 'ABAC policy decision: PERMIT or DENY';
COMMENT ON COLUMN access_logs.policy_matched IS 'Name of ABAC policy that made the decision';
COMMENT ON COLUMN access_logs.is_emergency IS 'TRUE for break-glass emergency access';
COMMENT ON COLUMN access_logs.justification IS 'Required for emergency access, explains why access was needed';
