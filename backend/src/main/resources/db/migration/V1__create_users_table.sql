-- Create Users Table for Healthcare Providers, Patients, and Administrators
-- Stores ABAC attributes: role, department, certifications, emergency_certified

CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('DOCTOR', 'PATIENT', 'PHARMACIST', 'ADMIN', 'INSURANCE_ADJUSTER')),
    department VARCHAR(100),
    certifications TEXT ARRAY,
    employer VARCHAR(200),
    location VARCHAR(200),
    emergency_certified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- triggers removed as JPA auditing handles updated_at

-- Add comments for documentation
COMMENT ON TABLE users IS 'Healthcare users with ABAC attributes for access control';
COMMENT ON COLUMN users.role IS 'User role for ABAC: DOCTOR, PATIENT, PHARMACIST, ADMIN, INSURANCE_ADJUSTER';
COMMENT ON COLUMN users.certifications IS 'Array of certifications for ABAC policy evaluation';
COMMENT ON COLUMN users.emergency_certified IS 'Whether user can perform emergency break-glass access';
