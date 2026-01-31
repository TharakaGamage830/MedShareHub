-- Seed Test Users
-- Dr. Smith (Healthcare Provider) with full ABAC attributes

INSERT INTO users (email, password_hash, first_name, last_name, role, department, certifications, emergency_certified)
VALUES (
    'dr_smith@medshare.hub',
    '$2a$12$CMRdsfTfC1tbFLSvqADcV.yniDSyp3jge.1kmxbzKHGhwtEncMBiS',
    'John',
    'Smith',
    'DOCTOR',
    'CARDIOLOGY',
    ARRAY['BOARD_CERTIFIED', 'HIPAA_TRAINED'],
    TRUE
);

-- Seed a Patient
INSERT INTO users (email, password_hash, first_name, last_name, role, emergency_certified)
VALUES (
    'patient@medshare.hub',
    '$2a$12$CMRdsfTfC1tbFLSvqADcV.yniDSyp3jge.1kmxbzKHGhwtEncMBiS',
    'Jane',
    'Doe',
    'PATIENT',
    FALSE
);
