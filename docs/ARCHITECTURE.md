# CampusCard Architecture Documentation

This document provides a comprehensive overview of the CampusCard application architecture, design decisions, and technical implementation.

## Table of Contents

- [System Overview](#system-overview)
- [Architecture Patterns](#architecture-patterns)
- [Component Architecture](#component-architecture)
- [Data Flow](#data-flow)
- [Security Architecture](#security-architecture)
- [Scalability Considerations](#scalability-considerations)
- [Technology Decisions](#technology-decisions)

---

## System Overview

CampusCard is a full-stack web application built with a modern, scalable architecture:

```
┌─────────────────────────────────────────────────────────────┐
│                        Client Layer                          │
│  ┌─────────────────────────────────────────────────────┐   │
│  │          React SPA (Single Page Application)        │   │
│  │  - React Router (Navigation)                        │   │
│  │  - React Query (State Management)                   │   │
│  │  - Axios (HTTP Client)                              │   │
│  │  - Tailwind CSS (Styling)                           │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            │ HTTPS / REST API
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                     Application Layer                        │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              Spring Boot Application                │   │
│  │  ┌───────────────────────────────────────────────┐ │   │
│  │  │         Controller Layer (REST API)           │ │   │
│  │  ├───────────────────────────────────────────────┤ │   │
│  │  │         Service Layer (Business Logic)        │ │   │
│  │  ├───────────────────────────────────────────────┤ │   │
│  │  │    Repository Layer (Data Access - JPA)      │ │   │
│  │  └───────────────────────────────────────────────┘ │   │
│  │                                                      │   │
│  │  Cross-Cutting Concerns:                            │   │
│  │  - Security (JWT, Spring Security)                  │   │
│  │  - Validation (Jakarta Validation)                  │   │
│  │  - Rate Limiting (Bucket4j)                         │   │
│  │  - Content Moderation                               │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
          ┌─────────────────┼─────────────────┐
          │                 │                 │
          ▼                 ▼                 ▼
┌──────────────────┐ ┌─────────────┐ ┌──────────────┐
│   PostgreSQL     │ │    MinIO    │ │ Email SMTP   │
│  (Port 5432)     │ │  (Port 9000)│ │ (Optional)   │
│                  │ │             │ │              │
│  - User Data     │ │  - Profile  │ │  - Email     │
│  - Profiles      │ │    Photos   │ │    Verif.    │
│  - Faculties     │ │  - ID Scans │ │  - Notif.    │
│  - Departments   │ │             │ │              │
└──────────────────┘ └─────────────┘ └──────────────┘
     Data Layer        Object Store     Messaging
```

---

## Architecture Patterns

### 1. Layered Architecture

CampusCard follows a classic **three-tier architecture**:

#### **Presentation Layer** (Frontend - React)
- **Responsibility**: User interface and user experience
- **Components**: React components, routing, state management
- **Communication**: REST API calls to backend

#### **Application Layer** (Backend - Spring Boot)
- **Controller Layer**: 
  - Handles HTTP requests and responses
  - Validates input
  - Delegates to service layer
  - Returns DTOs (Data Transfer Objects)

- **Service Layer**:
  - Contains business logic
  - Orchestrates operations
  - Transaction management
  - Calls repositories

- **Repository Layer**:
  - Data access abstraction
  - Spring Data JPA interfaces
  - Custom queries

#### **Data Layer**
- **Database**: PostgreSQL for relational data
- **Object Storage**: MinIO for file uploads
- **Caching**: (Future) Redis for performance

### 2. Model-View-Controller (MVC)

The backend follows MVC pattern:
- **Model**: JPA entities (`User`, `Profile`, `Faculty`, etc.)
- **View**: JSON responses (DTOs)
- **Controller**: REST controllers

### 3. Repository Pattern

Data access is abstracted through Spring Data JPA repositories:

```java
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByNationalId(String nationalId);
    List<User> findByStatus(User.Status status);
}
```

**Benefits**:
- Decouples business logic from data access
- Enables easy testing with mocks
- Provides consistent data access API

### 4. DTO Pattern

Data Transfer Objects separate internal models from API contracts:

```java
// Entity (internal)
@Entity
public class User {
    private String password; // Sensitive data
    // ...
}

// DTO (external)
public class UserResponse {
    private Long id;
    private String email;
    // Password not exposed
}
```

**Benefits**:
- Security: Don't expose sensitive fields
- Flexibility: API can differ from database schema
- Versioning: Change internal models without breaking API

### 5. Builder Pattern

Lombok's `@Builder` annotation is used extensively:

```java
User admin = User.builder()
    .email(adminEmail)
    .password(hashedPassword)
    .role(User.Role.ADMIN)
    .status(User.Status.APPROVED)
    .build();
```

**Benefits**:
- Readable object construction
- Immutability support
- Optional parameters

---

## Component Architecture

### Backend Components

#### **Controllers**
- `LoginController`: Authentication
- `SignUpController`: User registration
- `UserController`: User operations
- `ProfileController`: Profile management
- `AdminController`: Admin operations
- `PublicController`: Public endpoints

#### **Services**
- `LoginService`: Authentication logic
- `SignUpService`: Registration workflow
- `ProfileService`: Profile CRUD operations
- `AdminService`: Admin operations
- `ContentModerationService`: Content filtering
- `MinioService`: File upload/download
- `JwtService`: Token generation/validation

#### **Repositories**
- `UserRepository`: User data access
- `ProfileRepository`: Profile data access
- `FacultyRepository`: Faculty data access
- `DepartmentRepository`: Department data access
- `BannedWordRepository`: Banned words list
- `FlaggedContentRepository`: Flagged content log

#### **Security Components**
- `SecurityConfig`: Spring Security configuration
- `JwtAuthenticationFilter`: JWT token validation
- `RateLimitInterceptor`: Rate limiting
- `RateLimitConfig`: Rate limit configuration

#### **Configuration**
- `AdminUserInitializer`: Creates default admin
- `MinioConfig`: MinIO client setup
- `CorsConfig`: CORS configuration

### Frontend Components

#### **Core**
- `apiClient.js`: HTTP client wrapper
- `auth.context.jsx`: Authentication state
- `auth.storage.js`: Token persistence
- `requireAuth.jsx`: Authentication guard
- `requireAdmin.jsx`: Admin guard
- `requireApprovedStudent.jsx`: Approval guard

#### **Features**
- **Auth**: Login, Signup, Pending Status pages
- **Profile**: Profile view/edit
- **Admin**: Dashboard, User Review, Pending Users
- **Public**: Student Directory, Public Profiles

#### **UI Components**
- `MainLayout.jsx`: Application layout with navbar
- `Button.jsx`: Reusable button component
- `Card.jsx`: Card container
- `Input.jsx`: Form input
- `Spinner.jsx`: Loading indicator

---

## Data Flow

### 1. User Registration Flow

```
User → Frontend (SignupPage)
  ↓ FormData (multipart/form-data)
SignUpController.signup()
  ↓ Validate input
SignUpService.signUp()
  ↓ Check email availability
  ↓ Validate national ID format
  ↓ Check faculty-department relationship
  ↓ Upload ID scan to MinIO
  ↓ Hash password with BCrypt
  ↓ Create User entity (status=PENDING)
  ↓ Create default Profile
UserRepository.save()
  ↓
Database (PostgreSQL)
  ↓ Return created user
Frontend ← UserResponse DTO
```

### 2. Authentication Flow

```
User → Frontend (LoginPage)
  ↓ { identifier, password }
LoginController.login()
  ↓ Rate limit check
LoginService.login()
  ↓ Find user by email or national ID
  ↓ Verify password with BCrypt
  ↓ Check user status
JwtService.generateToken()
  ↓ Create JWT with claims
Frontend ← { token, user data }
  ↓ Store in localStorage
  ↓ Set Authorization header
Subsequent requests → JWT in header
  ↓
JwtAuthenticationFilter
  ↓ Validate token signature
  ↓ Check expiration
  ↓ Extract user info
  ↓ Load from database
  ↓ Set SecurityContext
Controller → Service → Repository
```

### 3. Profile Update Flow

```
User → Frontend (ProfilePage)
  ↓ Updated profile data
ProfileController.updateProfile()
  ↓ Get authenticated user
ProfileService.updateProfile()
  ↓ Load existing profile
ContentModerationService.checkContent()
  ↓ Check bio, interests for banned words
  ↓ If violation: log and reject
  ↓ If clean: proceed
ProfileRepository.save()
  ↓
Database (PostgreSQL)
  ↓
Frontend ← Updated ProfileResponse
```

### 4. Admin Approval Flow

```
Admin → Frontend (AdminReviewUserPage)
  ↓ userId
AdminController.getUserDetails()
  ↓
Frontend displays:
  - User info
  - Profile photo
  - National ID scan
  ↓
Admin clicks "Approve"
  ↓ POST /admin/users/{id}/approve
AdminController.approveUser()
  ↓
AdminService.approveUser()
  ↓ Check email verification
  ↓ Update status to APPROVED
  ↓ Send approval email (if configured)
  ↓ Log admin action
UserRepository.save()
  ↓
Frontend ← Success message
```

---

## Security Architecture

### Authentication Layer

```
┌─────────────────────────────────────────┐
│         Client Request                   │
│  Authorization: Bearer <JWT>            │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│    JwtAuthenticationFilter               │
│  1. Extract token from header           │
│  2. Validate token signature            │
│  3. Check expiration                    │
│  4. Extract claims (userId, role)       │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│    Load User from Database               │
│  - Verify user exists                   │
│  - Check user status                    │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│    Set SecurityContext                   │
│  - Store authentication                 │
│  - Available to controllers             │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│    Authorization Check                   │
│  - @PreAuthorize annotations            │
│  - hasRole() checks                     │
│  - Service-layer checks                 │
└──────────────┬──────────────────────────┘
               │
               ▼
         Controller
```

### Rate Limiting Architecture

```
Request → RateLimitInterceptor
  ↓
Bucket4j Token Bucket
  ├─ Login Bucket: 5 tokens / 15 min
  └─ Signup Bucket: 3 tokens / 60 min
  ↓
tryConsume(1 token)
  ├─ Success → Allow request
  └─ Failure → 429 Too Many Requests
```

### Content Moderation

```
Profile Update → ContentModerationService
  ↓
checkContent(bio, interests, links)
  ↓
  ├─ For each field:
  │   ├─ Convert to lowercase
  │   ├─ Check against banned_words table
  │   └─ If match found:
  │       ├─ Log to flagged_content
  │       └─ Throw ValidationException
  │
  └─ If all clean:
      └─ Proceed with update
```

---

## Scalability Considerations

### Current Architecture (Single Instance)

The current architecture runs as a monolith:
- Single Spring Boot instance
- Single PostgreSQL database
- Single MinIO instance
- In-memory rate limiting

**Limitations**:
- Limited to vertical scaling
- Rate limits reset on restart
- Single point of failure

### Future Scaling Options

#### 1. Horizontal Scaling (Load Balanced)

```
         Load Balancer (Nginx/HAProxy)
                 │
        ┌────────┼────────┐
        │        │        │
        ▼        ▼        ▼
    Instance  Instance  Instance
    (Port 1)  (Port 2)  (Port 3)
        │        │        │
        └────────┼────────┘
                 │
                 ▼
         Shared PostgreSQL
         Shared MinIO
         Redis (Rate Limits + Cache)
```

**Required Changes**:
- Stateless session management (already done with JWT)
- Distributed rate limiting with Redis
- Shared file storage (MinIO cluster or S3)
- Database connection pooling

#### 2. Microservices Architecture

```
API Gateway (Spring Cloud Gateway)
    │
    ├─→ Auth Service (Login/Signup)
    ├─→ User Service (Profile Management)
    ├─→ Admin Service (Approval Workflow)
    ├─→ File Service (MinIO Integration)
    └─→ Notification Service (Emails)
```

**Benefits**:
- Independent scaling
- Technology diversity
- Fault isolation
- Easier deployment

**Trade-offs**:
- Increased complexity
- Distributed transactions
- Network latency
- More infrastructure

#### 3. Caching Strategy

```
Request → Check Redis Cache
    ├─ Hit → Return cached data
    └─ Miss → Query Database
             └─ Store in Redis
```

**Cacheable Data**:
- Faculty list (rarely changes)
- Department list (rarely changes)
- Public student profiles
- User sessions

---

## Technology Decisions

### Why Spring Boot 4.0?

**Pros**:
- Latest features and security patches
- Excellent Spring Security integration
- Built-in observability (Actuator)
- Large ecosystem
- Strong community support

**Cons**:
- Relatively new (potential bugs)
- Breaking changes from Spring Boot 2.x/3.x

**Alternatives Considered**:
- Quarkus: Better startup time but smaller ecosystem
- Micronaut: Good for microservices but less mature
- Node.js/Express: Different language, less type safety

### Why React 19?

**Pros**:
- Latest React features
- Improved performance
- Better developer experience
- Large ecosystem

**Cons**:
- Cutting edge (potential instability)
- Some libraries not yet compatible

**Alternatives Considered**:
- Vue.js: Simpler but smaller ecosystem
- Angular: More opinionated but steeper learning curve
- Svelte: Better performance but smaller ecosystem

### Why PostgreSQL?

**Pros**:
- ACID compliance
- Rich feature set (JSON, arrays, full-text search)
- Excellent performance
- Open source
- Strong Spring Boot support

**Alternatives Considered**:
- MySQL: Less feature-rich
- MongoDB: NoSQL not needed for relational data
- Oracle: Expensive licensing

### Why MinIO?

**Pros**:
- S3-compatible API
- Self-hosted (no external dependencies)
- Docker support
- Simple setup

**Alternatives Considered**:
- AWS S3: Requires cloud account
- Local filesystem: Not scalable
- GridFS (MongoDB): Adds MongoDB dependency

### Why JWT?

**Pros**:
- Stateless (scalable)
- Self-contained (no database lookup)
- Standard (RFC 7519)
- Easy to validate

**Cons**:
- Cannot revoke easily
- Larger than session IDs
- Stored in localStorage (XSS risk)

**Alternatives Considered**:
- Session cookies: Requires server-side storage
- OAuth 2.0: Too complex for this use case

---

## Database Schema

See [DATABASE.md](DATABASE.md) for complete schema documentation.

**Core Tables**:
- `users` - User accounts and credentials
- `profiles` - User profiles and bio
- `faculties` - University faculties
- `departments` - Faculty departments
- `banned_words` - Content moderation wordlist
- `flagged_content` - Content violation logs

**Relationships**:
- User ↔ Profile: One-to-One
- User → Faculty: Many-to-One
- User → Department: Many-to-One
- Faculty → Departments: One-to-Many

---

## Future Architecture Enhancements

### Short-term (3-6 months)
- [ ] Add Redis for caching and distributed rate limiting
- [ ] Implement API response caching
- [ ] Add database query optimization and indexes
- [ ] Implement audit logging table

### Medium-term (6-12 months)
- [ ] Horizontal scaling with multiple instances
- [ ] Implement event-driven architecture
- [ ] Add Elasticsearch for advanced search
- [ ] Implement WebSocket for real-time notifications

### Long-term (12+ months)
- [ ] Migrate to microservices architecture
- [ ] Implement CQRS pattern
- [ ] Add GraphQL API alongside REST
- [ ] Implement ML-based content moderation

---

**Last Updated**: December 24, 2025  
**Version**: 1.0  
**Maintainer**: Mohamed170408@eng.psu.edu.eg
