# MedShare Hub - Backend

> Secure Healthcare Data Exchange Platform with ABAC Authorization

## Overview

MedShare Hub backend implements a HIPAA-compliant healthcare data exchange system with Attribute-Based Access Control (ABAC). Built with Spring Boot 3.2.2, PostgreSQL, and Redis.

## Key Features

- ✅ **ABAC Policy Engine** - Custom implementation with 4 core policies
- ✅ **JWT Authentication** - Stateless authentication with BCrypt (strength 12)  
- ✅ **HIPAA Audit Logging** - Immutable, tamper-proof audit trail
- ✅ **Field-Level Redaction** - Policy-based content filtering
- ✅ **Patient Consent Management** - Granular consent controls
- ✅ **Emergency Override** - Break-glass access for life-threatening situations
- ✅ **Performance Optimized** - Redis caching, connection pooling, <100ms policy evaluation

## Technology Stack

- **Framework**: Spring Boot 3.2.2 (Java 17+)
- **Database**: PostgreSQL 15+
- **Cache**: Redis
- **Security**: Spring Security, JWT (io.jsonwebtoken)
- **Migration**: Flyway
- **API Docs**: Swagger/OpenAPI

## Prerequisites

- Java 17 or higher
- Maven 3.8+
- PostgreSQL 15+
- Redis 7+
- Docker (optional, for databases)

## Quick Start

### 1. Setup PostgreSQL

```bash
# Option A: Using Docker
docker run --name medshare-postgres \
  -e POSTGRES_DB=medshare_hub \
  -e POSTGRES_USER=medshare_user \
  -e POSTGRES_PASSWORD=medshare_secure_password_2026 \
  -p 5432:5432 \
  -d postgres:15

# Option B: Manual Installation
createdb medshare_hub
createuser medshare_user
ALTER USER medshare_user WITH PASSWORD 'medshare_secure_password_2026';
GRANT ALL PRIVILEGES ON DATABASE medshare_hub TO medshare_user;
```

### 2. Setup Redis

```bash
# Using Docker
docker run --name medshare-redis -p 6379:6379 -d redis:7

# Or install locally
# Windows: Download from https://redis.io/download
# Linux: sudo apt-get install redis-server
# Mac: brew install redis
```

### 3. Configure Application

Edit `src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/medshare_hub
spring.datasource.username=medshare_user  
spring.datasource.password=medshare_secure_password_2026

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379

# JWT Secret (CHANGE IN PRODUCTION!)
jwt.secret=MedShareHub_ABAC_SecureJWT_Secret_Key_2026_Change_In_Production_256bit
jwt.expiration=86400000
jwt.refresh-expiration=604800000
```

### 4. Run Database Migrations

Flyway will automatically run migrations on startup:

```bash
mvn flyway:migrate
```

Or migrations run automatically when starting the application.

### 5. Build and Run

```bash
# Build
mvn clean package -DskipTests

# Run
mvn spring-boot:run

# Or run JAR
java -jar target/medshare-hub-backend-0.0.1-SNAPSHOT.jar
```

Application starts on http://localhost:8080

### 6. Test API

```bash
# Health check
curl http://localhost:8080/actuator/health

# API Documentation
open http://localhost:8080/swagger-ui.html
```

## Database Schema

### Core Tables

1. **users** - Healthcare providers, patients, administrators
   - ABAC attributes: role, department, certifications, emergency_certified
   
2. **patients** - Patient demographics with MRN

3. **medical_records** - Medical data with FHIR JSONB content
   - Sensitivity levels: PUBLIC, STANDARD, PSYCHIATRIC, HIV, CRITICAL

4. **treatment_relationships** - Active provider-patient relationships

5. **consents** - Patient consent for data sharing

6. **access_logs** - Immutable HIPAA audit trail

## ABAC Policies

### 1. EmergencyOverridePolicy (Priority 1)
- **Trigger**: Emergency flag set
- **Requirements**: Emergency-certified physician, justification
- **Obligations**: Enhanced audit, supervisor notification

### 2. PatientSelfAccessPolicy (Priority 2)
- **Allow**: Patients access their own records without restrictions

### 3. TreatingPhysicianPolicy (Priority 3)
- **Allow**: Doctors with active treatment relationships
- **Restrictions**: Business hours, psychiatric department for psych records

### 4. InsuranceClaimsPolicy (Priority 4)
- **Allow**: Insurance adjusters with patient consent
- **Obligations**: Redact clinical notes, redact sensitive diagnoses

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Refresh access token
- `POST /api/auth/logout` - Logout

### Medical Records
- `GET /api/records/{id}` - Get record with ABAC
- `GET /api/records/patient/{patientId}` - Get patient's records
- `POST /api/records` - Create record

### Patients
- `GET /api/patients/{id}` - Get patient
- `GET /api/patients/mrn/{mrn}` - Get by MRN
- `GET /api/patients/search?query={name}` - Search
- `POST /api/patients` - Create patient
- `PUT /api/patients/{id}` - Update patient

### Consents
- `GET /api/consents/patient/{patientId}` - Get consents
- `POST /api/consents` - Grant consent
- `DELETE /api/consents/{id}` - Revoke consent

## Testing

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=PolicyEvaluatorTest

# Integration tests
mvn verify
```

## Performance Targets

- Policy Evaluation: <100ms (p95)
- API Response Time: <500ms (p95)
- Concurrent Users: 100,000+

## Security

- **Password Hashing**: BCrypt (strength 12)
- **JWT Signing**: HS256 with 256-bit secret
- **SQL Injection**: Prevented via JPA/Hibernate
- **XSS Protection**: Input validation, output encoding
- **CORS**: Configured for React frontend
- **Audit Logging**: All access attempts logged

## HIPAA Compliance

- ✅ Access control (ABAC)
- ✅ Audit controls (immutable logs)
- ✅ Integrity controls (database constraints)
- ✅ Authentication (JWT with BCrypt)
- ✅ Transmission security (TLS 1.3 recommended)
- ✅ 7-year audit retention

## Production Deployment

1. **Change JWT Secret**: Generate 256-bit random key
2. **Enable HTTPS**: Configure TLS 1.3
3. **Database Encryption**: Enable encryption at rest
4. **Redis Security**: Enable AUTH, use TLS
5. **Environment Variables**: Use secrets management
6. **Monitoring**: Add APM (New Relic, Datadog)
7. **Rate Limiting**: Implement API rate limiting

## License

Proprietary - MedShare Hub © 2026

## Support

For issues or questions, contact: [project maintainer]
