# CampusCard Database Documentation

Complete database schema documentation for the CampusCard application.

## Table of Contents

- [Overview](#overview)
- [Database Configuration](#database-configuration)
- [Schema Diagram](#schema-diagram)
- [Table Definitions](#table-definitions)
- [Relationships](#relationships)
- [Indexes](#indexes)
- [Migrations](#migrations)
- [Queries](#queries)
- [Backup and Recovery](#backup-and-recovery)

---

## Overview

CampusCard uses **PostgreSQL 16** as its relational database, managed through **Flyway migrations** for version control and schema evolution.

### Key Characteristics

- **RDBMS**: PostgreSQL 16
- **Migration Tool**: Flyway
- **ORM**: Spring Data JPA with Hibernate
- **Connection Pool**: HikariCP (Spring Boot default)
- **Isolation Level**: READ_COMMITTED (default)

---

## Database Configuration

### Environment Variables

```properties
# Database connection
DB_HOST=localhost
DB_PORT=5432
DB_NAME=campuscard
DB_USERNAME=campuscard_user
DB_PASSWORD=your_secure_password

# Connection pool
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
```

### Connection String

```
jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
```

### Docker Compose

Local development uses Docker Compose:

```yaml
postgres:
  image: postgres:16
  environment:
    POSTGRES_DB: campuscard
    POSTGRES_USER: campuscard_user
    POSTGRES_PASSWORD: campuscard_password
  ports:
    - "5432:5432"
  volumes:
    - postgres_data:/var/lib/postgresql/data
```

---

## Schema Diagram

### Entity Relationship Diagram (ERD)

```
┌─────────────────┐         ┌──────────────────┐
│     users       │◄───────►│    profiles      │
├─────────────────┤  1:1    ├──────────────────┤
│ id (PK)         │         │ id (PK)          │
│ first_name      │         │ user_id (FK)     │
│ last_name       │         │ bio              │
│ email           │         │ date_of_birth    │
│ password        │         │ national_id      │
│ role            │         │ profile_photo_url│
│ status          │         │ national_id_scan │
│ email_verified  │         │ visibility       │
│ created_at      │         │ faculty          │
│ updated_at      │         │ department       │
└─────────────────┘         │ academic_year    │
                            │ interests        │
                            │ linkedin_url     │
                            │ github_url       │
                            │ x_url            │
                            │ students_only    │
                            │ created_at       │
                            │ updated_at       │
                            └──────────────────┘

┌──────────────────┐
│  banned_words    │
├──────────────────┤
│ id (PK)          │
│ word             │
│ created_at       │
└──────────────────┘
```

### Schema Overview

- **users**: Core authentication and user identity
- **profiles**: Extended user information and academic details
- **banned_words**: Content moderation dictionary

---

## Table Definitions

### users

Stores user authentication and account information.

**Table Structure**:
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Columns**:

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGSERIAL | PRIMARY KEY | Auto-incrementing user identifier |
| `first_name` | VARCHAR(50) | NOT NULL | User's first name |
| `last_name` | VARCHAR(50) | NOT NULL | User's last name |
| `email` | VARCHAR(100) | NOT NULL, UNIQUE | User's email (university domain) |
| `password` | VARCHAR(255) | NOT NULL | BCrypt hashed password |
| `role` | VARCHAR(20) | NOT NULL | User role: `STUDENT` or `ADMIN` |
| `status` | VARCHAR(20) | NOT NULL | Account status: `PENDING`, `APPROVED`, `REJECTED` |
| `email_verified` | BOOLEAN | NOT NULL, DEFAULT FALSE | Email verification status |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT NOW() | Account creation timestamp |
| `updated_at` | TIMESTAMP | NOT NULL, DEFAULT NOW() | Last update timestamp |

**Enums**:

```java
// UserRole.java
public enum UserRole {
    STUDENT,
    ADMIN
}

// UserStatus.java
public enum UserStatus {
    PENDING,    // Awaiting admin approval
    APPROVED,   // Approved by admin
    REJECTED    // Rejected by admin
}
```

**Indexes**:
- Primary key on `id`
- Unique index on `email`
- Index on `role` for admin queries
- Index on `status` for filtering

**Sample Data**:
```sql
INSERT INTO users (first_name, last_name, email, password, role, status, email_verified)
VALUES 
    ('Mohamed', 'Abdelwahab', 'admin@eng.psu.edu.eg', '$2a$10$...', 'ADMIN', 'APPROVED', true),
    ('Ahmed', 'Hassan', 'ahmed123@eng.psu.edu.eg', '$2a$10$...', 'STUDENT', 'PENDING', false);
```

---

### profiles

Stores extended user information and academic details.

**Table Structure**:
```sql
CREATE TABLE profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    bio TEXT,
    date_of_birth DATE NOT NULL,
    national_id VARCHAR(14) NOT NULL UNIQUE,
    profile_photo_url TEXT,
    national_id_scan_url TEXT,
    visibility VARCHAR(20) NOT NULL DEFAULT 'PUBLIC',
    faculty VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    academic_year INTEGER NOT NULL,
    interests TEXT,
    linkedin_url TEXT,
    github_url TEXT,
    x_url TEXT,
    students_only BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

**Columns**:

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGSERIAL | PRIMARY KEY | Profile identifier |
| `user_id` | BIGINT | NOT NULL, UNIQUE, FK | Reference to users.id |
| `bio` | TEXT | NULLABLE | User biography (checked for banned words) |
| `date_of_birth` | DATE | NOT NULL | User's date of birth |
| `national_id` | VARCHAR(14) | NOT NULL, UNIQUE | Egyptian national ID (14 digits) |
| `profile_photo_url` | TEXT | NULLABLE | MinIO URL to profile photo |
| `national_id_scan_url` | TEXT | NULLABLE | MinIO URL to national ID scan |
| `visibility` | VARCHAR(20) | NOT NULL, DEFAULT 'PUBLIC' | Profile visibility setting |
| `faculty` | VARCHAR(100) | NOT NULL | User's faculty |
| `department` | VARCHAR(100) | NOT NULL | User's department |
| `academic_year` | INTEGER | NOT NULL | Academic year (1-7) |
| `interests` | TEXT | NULLABLE | Comma-separated interests |
| `linkedin_url` | TEXT | NULLABLE | LinkedIn profile URL |
| `github_url` | TEXT | NULLABLE | GitHub profile URL |
| `x_url` | TEXT | NULLABLE | X (Twitter) profile URL |
| `students_only` | BOOLEAN | NOT NULL, DEFAULT FALSE | Restrict visibility to students only |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT NOW() | Profile creation timestamp |
| `updated_at` | TIMESTAMP | NOT NULL, DEFAULT NOW() | Last update timestamp |

**Enums**:

```java
// ProfileVisibility.java
public enum ProfileVisibility {
    PUBLIC,     // Visible to everyone (authenticated)
    PRIVATE     // Visible only to admins
}
```

**Constraints**:
- One profile per user (1:1 relationship)
- `national_id` must be exactly 14 characters
- `academic_year` must be between 1 and 7
- URLs validated at application level

**Indexes**:
- Primary key on `id`
- Unique index on `user_id`
- Unique index on `national_id`
- Index on `faculty` for directory filtering
- Index on `department` for directory filtering
- Index on `visibility` for access control queries

**Sample Data**:
```sql
INSERT INTO profiles (
    user_id, bio, date_of_birth, national_id,
    faculty, department, academic_year, visibility
)
VALUES (
    2,
    'Computer Science student passionate about AI',
    '2002-05-15',
    '30205151234567',
    'Faculty of Engineering',
    'Computer Engineering',
    3,
    'PUBLIC'
);
```

---

### banned_words

Stores words banned from user-generated content.

**Table Structure**:
```sql
CREATE TABLE banned_words (
    id BIGSERIAL PRIMARY KEY,
    word VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Columns**:

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGSERIAL | PRIMARY KEY | Word identifier |
| `word` | VARCHAR(100) | NOT NULL, UNIQUE | Banned word (case-insensitive) |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT NOW() | When word was added |

**Indexes**:
- Primary key on `id`
- Unique index on `word`

**Usage**:
Content moderation checks user input against this list:
- Profile bio
- Interests
- Social media URLs

**Sample Data**:
```sql
INSERT INTO banned_words (word) VALUES
    ('inappropriate'),
    ('offensive'),
    ('banned');
```

---

## Relationships

### users ↔ profiles (One-to-One)

**Type**: One-to-One (1:1)

**Foreign Key**: `profiles.user_id` → `users.id`

**On Delete**: CASCADE (deleting user deletes profile)

**JPA Mapping**:
```java
// User.java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Profile profile;
}

// Profile.java
@Entity
@Table(name = "profiles")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
```

**Business Logic**:
- Profile created automatically during signup
- Profile deleted when user deleted
- Cannot have profile without user
- Cannot have multiple profiles per user

---

## Indexes

### Existing Indexes

| Table | Index | Columns | Type | Purpose |
|-------|-------|---------|------|---------|
| `users` | `users_pkey` | `id` | PRIMARY KEY | Unique user identification |
| `users` | `users_email_key` | `email` | UNIQUE | Prevent duplicate emails |
| `users` | `idx_users_role` | `role` | INDEX | Filter by role (admin queries) |
| `users` | `idx_users_status` | `status` | INDEX | Filter by status (pending users) |
| `profiles` | `profiles_pkey` | `id` | PRIMARY KEY | Unique profile identification |
| `profiles` | `profiles_user_id_key` | `user_id` | UNIQUE | One profile per user |
| `profiles` | `profiles_national_id_key` | `national_id` | UNIQUE | Prevent duplicate IDs |
| `profiles` | `idx_profiles_faculty` | `faculty` | INDEX | Student directory filtering |
| `profiles` | `idx_profiles_department` | `department` | INDEX | Student directory filtering |
| `profiles` | `idx_profiles_visibility` | `visibility` | INDEX | Access control queries |
| `banned_words` | `banned_words_pkey` | `id` | PRIMARY KEY | Unique word identification |
| `banned_words` | `banned_words_word_key` | `word` | UNIQUE | Prevent duplicate banned words |

### Index Creation (Migration)

```sql
-- V1__init.sql includes:
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_profiles_faculty ON profiles(faculty);
CREATE INDEX idx_profiles_department ON profiles(department);
CREATE INDEX idx_profiles_visibility ON profiles(visibility);
```

### Performance Considerations

**Heavily indexed**:
- `users.email`: Used in authentication (every login)
- `users.status`: Used in admin dashboard
- `profiles.faculty`, `profiles.department`: Used in directory search

**Future indexes** (if needed):
```sql
-- Composite index for directory filtering
CREATE INDEX idx_profiles_directory ON profiles(visibility, faculty, department);

-- Full-text search on bio (if implemented)
CREATE INDEX idx_profiles_bio_fts ON profiles USING gin(to_tsvector('english', bio));
```

---

## Migrations

CampusCard uses Flyway for database version control.

### Migration Files

Located in: `backend/src/main/resources/db/migration/`

| File | Version | Description |
|------|---------|-------------|
| `V1__init.sql` | 1 | Initial schema: users, profiles, banned_words tables |
| `V2__add_email_verification.sql` | 2 | Added email_verified column to users |
| `V3__add_students_only_visibility.sql` | 3 | Added students_only column to profiles |
| `V4__add_realistic_banned_words.sql` | 4 | Populated banned_words with initial list |

### Migration Strategy

**Versioning**: `V<VERSION>__<DESCRIPTION>.sql`

**Execution**: Automatic on application startup

**Validation**: Flyway checks checksums to prevent tampering

### Viewing Migration History

```sql
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
```

**Output**:
```
installed_rank | version | description                   | type | script                             | checksum    | installed_on        | success
---------------+---------+-------------------------------+------+------------------------------------+-------------+---------------------+---------
1              | 1       | init                          | SQL  | V1__init.sql                       | 1234567890  | 2024-01-15 10:00:00 | t
2              | 2       | add email verification        | SQL  | V2__add_email_verification.sql     | 987654321   | 2024-02-20 14:30:00 | t
3              | 3       | add students only visibility  | SQL  | V3__add_students_only_visibility.sql | 1122334455 | 2024-03-10 09:15:00 | t
4              | 4       | add realistic banned words    | SQL  | V4__add_realistic_banned_words.sql | 5566778899  | 2024-03-10 09:15:00 | t
```

### Creating New Migrations

1. **Create file**:
   ```bash
   touch src/main/resources/db/migration/V5__add_new_feature.sql
   ```

2. **Write SQL**:
   ```sql
   -- V5__add_new_feature.sql
   ALTER TABLE users ADD COLUMN phone_number VARCHAR(20);
   CREATE INDEX idx_users_phone ON users(phone_number);
   ```

3. **Test locally**:
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Verify**:
   ```sql
   \d users;  -- Check column exists
   SELECT * FROM flyway_schema_history;  -- Check migration applied
   ```

### Rolling Back Migrations

Flyway **does not support automatic rollbacks**. To rollback:

1. **Create new migration** with reverse changes:
   ```sql
   -- V6__rollback_phone_number.sql
   DROP INDEX IF EXISTS idx_users_phone;
   ALTER TABLE users DROP COLUMN phone_number;
   ```

2. **Or restore from backup** (see Backup and Recovery section)

---

## Queries

### Common Queries

#### Authentication

```sql
-- Find user by email (login)
SELECT * FROM users WHERE email = 'user@eng.psu.edu.eg';

-- Verify email
UPDATE users SET email_verified = TRUE WHERE id = 123;
```

#### User Management

```sql
-- Get all pending users
SELECT u.id, u.first_name, u.last_name, u.email, u.created_at
FROM users u
WHERE u.status = 'PENDING'
ORDER BY u.created_at ASC;

-- Approve user
UPDATE users SET status = 'APPROVED' WHERE id = 123;

-- Reject user
UPDATE users SET status = 'REJECTED' WHERE id = 123;
```

#### Profile Queries

```sql
-- Get user profile
SELECT p.* FROM profiles p
JOIN users u ON p.user_id = u.id
WHERE u.id = 123;

-- Student directory (public profiles only)
SELECT 
    u.first_name,
    u.last_name,
    u.email,
    p.faculty,
    p.department,
    p.academic_year,
    p.profile_photo_url
FROM profiles p
JOIN users u ON p.user_id = u.id
WHERE u.status = 'APPROVED'
  AND p.visibility = 'PUBLIC'
  AND (p.students_only = FALSE OR :requester_role = 'STUDENT')
ORDER BY u.last_name, u.first_name;

-- Filter by faculty
SELECT * FROM profiles
WHERE faculty = 'Faculty of Engineering'
  AND visibility = 'PUBLIC';
```

#### Admin Dashboard

```sql
-- Dashboard statistics
SELECT 
    COUNT(*) FILTER (WHERE status = 'PENDING') AS pending_count,
    COUNT(*) FILTER (WHERE status = 'APPROVED') AS approved_count,
    COUNT(*) FILTER (WHERE status = 'REJECTED') AS rejected_count,
    COUNT(*) FILTER (WHERE role = 'ADMIN') AS admin_count,
    COUNT(*) AS total_users
FROM users;

-- Faculty distribution
SELECT 
    p.faculty,
    COUNT(*) AS student_count
FROM profiles p
JOIN users u ON p.user_id = u.id
WHERE u.status = 'APPROVED'
GROUP BY p.faculty
ORDER BY student_count DESC;
```

#### Content Moderation

```sql
-- Check if text contains banned words
SELECT word FROM banned_words
WHERE LOWER(:user_input) LIKE '%' || LOWER(word) || '%';

-- Add banned word
INSERT INTO banned_words (word) VALUES ('new_banned_word')
ON CONFLICT (word) DO NOTHING;
```

### Performance Tips

1. **Use prepared statements** (prevents SQL injection)
2. **Limit large queries**: Add `LIMIT` and `OFFSET` for pagination
3. **Use indexes**: Filter on indexed columns (status, faculty, visibility)
4. **Avoid SELECT ***: Select only needed columns
5. **Use JOINs**: More efficient than separate queries

---

## Backup and Recovery

### Backup Strategies

#### 1. pg_dump (Recommended for Development)

**Full backup**:
```bash
docker exec -t campuscard-postgres-1 pg_dump -U campuscard_user campuscard > backup_$(date +%Y%m%d_%H%M%S).sql
```

**Schema only**:
```bash
docker exec -t campuscard-postgres-1 pg_dump -U campuscard_user --schema-only campuscard > schema_backup.sql
```

**Data only**:
```bash
docker exec -t campuscard-postgres-1 pg_dump -U campuscard_user --data-only campuscard > data_backup.sql
```

#### 2. Docker Volume Backup

```bash
# Stop containers
docker compose down

# Backup volume
docker run --rm -v campuscard_postgres_data:/data -v $(pwd):/backup ubuntu tar czf /backup/postgres_volume_backup.tar.gz /data

# Start containers
docker compose up -d
```

#### 3. Continuous Archiving (Production)

Configure PostgreSQL WAL archiving:
```
# postgresql.conf
wal_level = replica
archive_mode = on
archive_command = 'cp %p /backup/archive/%f'
```

### Restore Procedures

#### From pg_dump

```bash
# Drop and recreate database
docker exec -t campuscard-postgres-1 psql -U campuscard_user -c "DROP DATABASE IF EXISTS campuscard;"
docker exec -t campuscard-postgres-1 psql -U campuscard_user -c "CREATE DATABASE campuscard;"

# Restore backup
docker exec -i campuscard-postgres-1 psql -U campuscard_user campuscard < backup_20241224_150000.sql
```

#### From Docker Volume

```bash
# Stop containers
docker compose down

# Restore volume
docker run --rm -v campuscard_postgres_data:/data -v $(pwd):/backup ubuntu tar xzf /backup/postgres_volume_backup.tar.gz -C /

# Start containers
docker compose up -d
```

### Backup Schedule (Production)

- **Daily**: Full database dump at 2 AM
- **Hourly**: Incremental WAL archiving
- **Weekly**: Full volume backup
- **Monthly**: Offsite backup transfer
- **Retention**: 7 daily, 4 weekly, 12 monthly

---

## Additional Resources

- [PostgreSQL 16 Documentation](https://www.postgresql.org/docs/16/)
- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)

---

**Last Updated**: December 24, 2025  
**Database Version**: PostgreSQL 16  
**Schema Version**: 4 (Flyway)
