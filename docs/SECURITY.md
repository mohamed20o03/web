# CampusCard Security Documentation

This document outlines the security architecture, best practices, and guidelines for the CampusCard application.

## Table of Contents

- [Security Overview](#security-overview)
- [Authentication](#authentication)
- [Authorization](#authorization)
- [Password Security](#password-security)
- [JWT Token Management](#jwt-token-management)
- [Rate Limiting](#rate-limiting)
- [CORS Policy](#cors-policy)
- [Content Moderation](#content-moderation)
- [File Upload Security](#file-upload-security)
- [Environment Variables](#environment-variables)
- [Security Best Practices](#security-best-practices)
- [Vulnerability Reporting](#vulnerability-reporting)

---

## Security Overview

CampusCard implements a multi-layered security approach to protect user data and prevent unauthorized access:

- **Stateless Authentication**: JWT token-based authentication for scalability
- **Role-Based Access Control (RBAC)**: Separate permissions for students and admins
- **Rate Limiting**: Protection against brute force attacks
- **Password Hashing**: BCrypt with default strength (10 rounds)
- **Content Moderation**: Automated filtering of inappropriate content
- **Input Validation**: Jakarta Bean Validation on all user inputs
- **CORS Configuration**: Restricted to authorized frontend origins
- **Secure File Upload**: Validation and size limits on uploaded files

---

## Authentication

### Authentication Flow

1. **User Registration** (`POST /api/signup`):
   - User submits registration form with personal information
   - System validates input (email format, national ID format, faculty/department)
   - Password is hashed with BCrypt before storage
   - Account created with `PENDING` status
   - Email verification token generated (if email configured)
   - User cannot access protected resources until approved by admin

2. **Email Verification**:
   - Admin verifies user's email address manually or via token
   - Email verification status tracked in `email_verified` field
   - Approval workflow requires email verification

3. **User Login** (`POST /api/login`):
   - User submits email/nationalId and password
   - System validates credentials
   - JWT token generated with 24-hour expiration
   - Token contains: `userId`, `email`, `role`, `status`
   - Token returned in response body

4. **Authenticated Requests**:
   - Client includes JWT token in Authorization header: `Bearer <token>`
   - Server validates token on each request
   - User information extracted from token claims

### Supported Authentication Methods

- **Email-based login**: `email@eng.psu.edu.eg`
- **National ID-based login**: 14-digit national ID number

### Authentication Endpoints

| Endpoint | Method | Description | Rate Limit |
|----------|--------|-------------|------------|
| `/api/signup` | POST | Register new user | 3 requests / 60 min |
| `/api/login` | POST | Authenticate user | 5 requests / 15 min |

---

## Authorization

### User Roles

CampusCard supports two user roles:

1. **STUDENT**: Regular university students
   - Can view own profile
   - Can update own profile (with content moderation)
   - Can browse public student directory
   - Can view other students' profiles (based on visibility settings)

2. **ADMIN**: System administrators
   - All STUDENT permissions
   - Can approve/reject user registrations
   - Can verify user emails
   - Can promote users to admin or demote to student
   - Can manage banned words list
   - Can view dashboard statistics
   - Can access all user profiles regardless of visibility

### User Status

Users can have one of three statuses:

1. **PENDING**: Newly registered, awaiting admin approval
   - Can login but cannot access most features
   - Redirected to pending status page

2. **APPROVED**: Approved by admin
   - Full access to application features
   - Can create and update profile

3. **REJECTED**: Registration rejected by admin
   - Can login to view rejection reason
   - Cannot access application features

### Authorization Rules

```java
// Public endpoints (no authentication required)
/api/signup
/api/login
/api/public/**
/api/profile/** (visibility enforced at service layer)

// Admin endpoints (requires ROLE_ADMIN)
/api/admin/**

// Protected endpoints (requires authentication)
All other endpoints require valid JWT token
```

### Profile Visibility

Profiles support three visibility levels:

1. **PUBLIC**: Visible to everyone (including unauthenticated users)
2. **STUDENTS_ONLY**: Visible only to authenticated students
3. **PRIVATE**: Visible only to profile owner and admins

Visibility is enforced at the service layer in `ProfileService`, not at the URL level.

---

## Password Security

### Password Policy

- **Minimum Length**: 8 characters
- **Maximum Length**: 100 characters
- **Hashing Algorithm**: BCrypt with default strength (10 rounds)
- **Storage**: Only hashed passwords stored in database
- **Validation**: Enforced via `@NotBlank` and `@Size` annotations

### Password Best Practices

**For Users**:
- Use strong, unique passwords
- Avoid common passwords (e.g., "password123")
- Don't share passwords
- Change password if compromised

**For Administrators**:
- Admin passwords should be extra strong (16+ characters)
- Use password manager for secure storage
- Never commit passwords to version control
- Rotate admin passwords periodically

### BCrypt Configuration

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(); // Default strength: 10
}
```

To verify a password:
```java
boolean matches = passwordEncoder.matches(rawPassword, hashedPassword);
```

---

## JWT Token Management

### Token Structure

JWT tokens contain the following claims:

```json
{
  "sub": "userId",
  "email": "user@eng.psu.edu.eg",
  "role": "STUDENT|ADMIN",
  "status": "PENDING|APPROVED|REJECTED",
  "iat": 1703423152,
  "exp": 1703509552
}
```

### Token Configuration

- **Expiration**: 24 hours (86400000 ms)
- **Algorithm**: HS256 (HMAC with SHA-256)
- **Secret Key**: Loaded from environment variable `JWT_SECRET`
- **Token Type**: Bearer token

### Token Lifecycle

1. **Generation**: Created on successful login
2. **Validation**: Verified on each authenticated request
3. **Extraction**: User information extracted from claims
4. **Expiration**: Token expires after 24 hours
5. **Renewal**: User must re-authenticate after expiration

### Token Usage

**Request Header**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Token Validation**:
- Signature verification using secret key
- Expiration check
- User existence check in database
- Role and status validation

### Security Considerations

- **Secret Key Security**: JWT secret must be kept secure
  - Never commit to version control
  - Use strong, random secret (minimum 256 bits)
  - Generate using: `openssl rand -hex 32`
  - Store in environment variable

- **Token Storage** (Frontend):
  - Currently stored in `localStorage`
  - **Risk**: Vulnerable to XSS attacks
  - **Better Practice**: Use httpOnly cookies (future improvement)

- **Token Transmission**:
  - Always use HTTPS in production
  - Never log tokens
  - Don't include tokens in URLs

---

## Rate Limiting

Rate limiting protects against brute force attacks and abuse.

### Rate Limit Configuration

| Endpoint | Max Attempts | Time Window | Purpose |
|----------|--------------|-------------|---------|
| `/api/login` | 5 | 15 minutes | Prevent brute force login attacks |
| `/api/signup` | 3 | 60 minutes | Prevent automated account creation |

### Rate Limit Response

When rate limit is exceeded, the API returns:

**Status Code**: `429 Too Many Requests`

**Response Body**:
```json
{
  "error": "Rate limit exceeded for login endpoint",
  "message": "Too many requests. Please try again in 123 seconds.",
  "retryAfter": 123
}
```

**Response Headers**:
```
X-Rate-Limit-Remaining: 0
```

### Rate Limiting Implementation

- **Algorithm**: Token bucket (Bucket4j)
- **Scope**: Per IP address
- **Storage**: In-memory (resets on application restart)
- **Customization**: Configure via environment variables:
  - `RATE_LIMIT_LOGIN_MAX_ATTEMPTS`
  - `RATE_LIMIT_LOGIN_WINDOW_MINUTES`
  - `RATE_LIMIT_SIGNUP_MAX_ATTEMPTS`
  - `RATE_LIMIT_SIGNUP_WINDOW_MINUTES`

### Future Improvements

- Distributed rate limiting (Redis-based)
- Per-user rate limits (in addition to per-IP)
- Graduated rate limits based on user reputation
- Admin exemption from rate limits

---

## CORS Policy

Cross-Origin Resource Sharing (CORS) configuration controls which frontend origins can access the API.

### Development Configuration

```properties
ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173
```

### Production Configuration

```properties
ALLOWED_ORIGINS=https://campuscard.youruniversity.edu
```

### CORS Settings

- **Allowed Origins**: Configured via `ALLOWED_ORIGINS` environment variable
- **Allowed Methods**: All HTTP methods (GET, POST, PUT, DELETE, PATCH, OPTIONS)
- **Allowed Headers**: All headers
- **Allow Credentials**: `true` (enables cookies and authorization headers)

### Security Considerations

- **Never use wildcard (`*`) in production**
- Set specific frontend domain(s) only
- Use HTTPS in production
- Comma-separate multiple origins if needed

---

## Content Moderation

CampusCard implements automated content moderation to prevent inappropriate content.

### Moderation System

- **Scope**: Profile bio, interests, social links
- **Method**: Keyword-based filtering
- **Database**: `banned_words` table with ~50 inappropriate words
- **Action**: Reject update and return validation error
- **Logging**: Violations logged to `flagged_content` table

### Moderation Flow

1. User submits profile update
2. `ContentModerationService` checks all text fields
3. If banned word detected:
   - Update rejected
   - User receives error message
   - Violation logged for admin review
4. If clean:
   - Update proceeds normally

### Banned Words Management

- **Initial List**: Seeded via Flyway migration `V4__add_realistic_banned_words.sql`
- **Admin Management**: Admins can add/remove banned words (future feature)
- **Case Insensitive**: Matching is case-insensitive

### Future Improvements

- Natural Language Processing (NLP) for context-aware moderation
- Pattern matching for variations (e.g., l33t speak)
- Severity levels (warning vs. blocking)
- User appeals process
- Admin review dashboard for flagged content

---

## File Upload Security

### Allowed File Types

- **Profile Photos**: JPEG, PNG
- **National ID Scans**: JPEG, PNG

### File Size Limits

- **Maximum File Size**: 10 MB per file
- **Maximum Request Size**: 10 MB per request

### Validation

```java
// File type validation
private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
    "image/jpeg",
    "image/png"
);

// Size validation
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

### File Storage

- **Storage Backend**: MinIO object storage
- **Bucket**: `uploads`
- **File Path Structure**: `uploads/{userId}/profile_photo.{ext}`
- **Access Control**: Files accessible via pre-signed URLs

### Security Considerations

- **File Type Validation**: Check actual file content, not just extension
- **Virus Scanning**: Not currently implemented (future improvement)
- **File Name Sanitization**: Use UUID or user ID, not original filename
- **Storage Isolation**: Separate buckets for different file types (future improvement)

---

## Environment Variables

### Critical Security Variables

**NEVER commit these to version control**:

```bash
# JWT Configuration
JWT_SECRET=your-secure-jwt-secret-key-here

# Admin Credentials
ADMIN_EMAIL=admin@eng.psu.edu.eg
ADMIN_PASSWORD=SecureAdminPass123!
ADMIN_NATIONAL_ID=30303130300275

# Database Credentials
DB_USERNAME=campuscard_user
DB_PASSWORD=campuscard_password

# MinIO Credentials
MINIO_ACCESS_KEY=your-access-key
MINIO_SECRET_KEY=your-secret-key

# Email Credentials
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-specific-password
```

### Environment Variable Management

1. **Development**:
   - Copy `.env.example` to `.env`
   - Fill in actual values
   - `.env` is gitignored

2. **Production**:
   - Set environment variables in deployment platform
   - Never use `.env` file in production
   - Use secret management service (AWS Secrets Manager, HashiCorp Vault, etc.)

3. **Rotation**:
   - Rotate JWT secret periodically (invalidates all tokens)
   - Rotate database passwords on schedule
   - Rotate admin passwords on schedule

### Generating Secure Secrets

**JWT Secret** (256-bit minimum):
```bash
openssl rand -hex 32
```

**Strong Password**:
```bash
openssl rand -base64 24
```

---

## Security Best Practices

### For Developers

1. **Never Hardcode Secrets**
   - Use environment variables for all sensitive data
   - Never commit secrets to version control
   - Use `.env.example` for documentation

2. **Input Validation**
   - Validate all user inputs
   - Use Jakarta Bean Validation annotations
   - Sanitize inputs to prevent injection attacks

3. **Error Handling**
   - Don't expose internal details in error messages
   - Use generic messages for authentication failures
   - Log detailed errors server-side only

4. **Dependency Management**
   - Keep dependencies up to date
   - Monitor for security vulnerabilities (Snyk, Dependabot)
   - Review transitive dependencies

5. **Logging**
   - Never log passwords or tokens
   - Log security events (failed logins, access denials)
   - Implement audit logging for admin actions

6. **HTTPS**
   - Use HTTPS in production
   - Enforce HTTPS redirection
   - Use HTTP Strict Transport Security (HSTS) headers

### For Administrators

1. **Admin Account Security**
   - Use strong, unique password
   - Change default admin password immediately
   - Don't share admin credentials
   - Use separate admin account per person

2. **Access Control**
   - Follow principle of least privilege
   - Review user roles regularly
   - Revoke access for inactive admins

3. **Monitoring**
   - Monitor failed login attempts
   - Review flagged content regularly
   - Check for suspicious account patterns

4. **Backup**
   - Regular database backups
   - Secure backup storage
   - Test backup restoration periodically

5. **Incident Response**
   - Have incident response plan
   - Know how to revoke tokens (rotate JWT secret)
   - Document security incidents

### For Users

1. **Password Management**
   - Use strong, unique password
   - Don't share password
   - Change password if compromised

2. **Profile Privacy**
   - Set appropriate visibility level
   - Don't share sensitive information in bio
   - Review profile visibility regularly

3. **Suspicious Activity**
   - Report suspicious profiles
   - Report security issues to admins
   - Logout from shared computers

---

## Vulnerability Reporting

If you discover a security vulnerability, please report it responsibly:

### Reporting Process

1. **DO NOT** create a public GitHub issue
2. **DO NOT** disclose publicly until fixed
3. **DO** email security team at: `security@campuscard.edu`
4. **DO** provide detailed reproduction steps

### Report Should Include

- Description of the vulnerability
- Steps to reproduce
- Potential impact
- Suggested fix (if known)
- Your contact information

### Response Timeline

- **Initial Response**: Within 24 hours
- **Status Update**: Within 72 hours
- **Fix Timeline**: Depends on severity
  - Critical: 1-3 days
  - High: 1 week
  - Medium: 2 weeks
  - Low: 1 month

### Recognition

- Security researchers will be credited (if desired)
- Hall of fame for significant findings (future)
- Bounty program (under consideration)

---

## Security Checklist

### Before Deployment

- [ ] JWT secret is strong and stored in environment variable
- [ ] Admin password is changed from default
- [ ] CORS configured with specific frontend origin(s)
- [ ] HTTPS enabled and enforced
- [ ] Rate limiting configured and tested
- [ ] All environment variables documented
- [ ] `.env` file added to `.gitignore`
- [ ] Database credentials are strong
- [ ] Email verification configured (if using)
- [ ] File upload validation tested
- [ ] Content moderation tested
- [ ] Security headers configured (CSP, X-Frame-Options, etc.)
- [ ] Backup strategy implemented
- [ ] Monitoring and alerting configured
- [ ] Incident response plan documented

### Regular Security Tasks

- [ ] Review access logs weekly
- [ ] Check for failed login attempts
- [ ] Review flagged content
- [ ] Update dependencies monthly
- [ ] Scan for vulnerabilities monthly
- [ ] Rotate JWT secret quarterly
- [ ] Rotate admin password quarterly
- [ ] Review user roles quarterly
- [ ] Audit admin actions quarterly
- [ ] Test backup restoration quarterly

---

## Additional Resources

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)
- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/index.html)
- [Bucket4j Documentation](https://bucket4j.com/)
- [MinIO Security Guide](https://min.io/docs/minio/linux/operations/security.html)

---

**Last Updated**: December 24, 2025  
**Version**: 1.0  
**Maintainer**: Mohamed170408@eng.psu.edu.eg
