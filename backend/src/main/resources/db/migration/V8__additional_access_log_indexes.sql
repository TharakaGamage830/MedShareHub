-- V8__additional_access_log_indexes.sql
-- Optimizing audit trail for compliance reporting and anomaly detection

CREATE INDEX idx_access_logs_user_date ON access_logs(user_id, created_at DESC);
CREATE INDEX idx_access_logs_patient_date ON access_logs(patient_id, created_at DESC);
CREATE INDEX idx_access_logs_decision_date ON access_logs(decision, created_at DESC);
-- Redundant index removed (already present in V6)
-- CREATE INDEX idx_access_logs_emergency ON access_logs(is_emergency);
CREATE INDEX idx_access_logs_session ON access_logs(session_id);

-- ANALYZE access_logs;
