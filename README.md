# CampusCard - University Student Directory Platform

<div align="center">

![CampusCard Logo](frontend/src/assets/psu-logo.png)

**A comprehensive student directory and profile management platform for Port Said University**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19.2.3-blue.svg)](https://reactjs.org/)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

[Features](#-features) • [Quick Start](#-quick-start) • [Documentation](#-documentation) • [Tech Stack](#-tech-stack) • [Contributing](#-contributing)

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [Prerequisites](#-prerequisites)
- [Quick Start](#-quick-start)
- [Configuration](#-configuration)
- [Project Structure](#-project-structure)
- [API Documentation](#-api-documentation)
- [Development](#-development)
- [Testing](#-testing)
- [Deployment](#-deployment)
- [Security](#-security)
- [Contributing](#-contributing)
- [License](#-license)
- [Support](#-support)

---

## 🎯 Overview

CampusCard is a full-stack web application designed for Port Said University to manage student profiles and facilitate student networking. The platform provides a secure, role-based system where students can create profiles, connect with peers, and administrators can manage user registrations and content moderation.

### Key Highlights

- 🔐 **Secure Authentication**: JWT-based stateless authentication with BCrypt password hashing
- 👥 **Role-Based Access Control**: Separate permissions for students and administrators
- ✅ **Admin Approval Workflow**: Manual approval process with email verification
- 🛡️ **Content Moderation**: Automated filtering of inappropriate content
- 📸 **Profile Management**: Photo uploads, bio, interests, and social links
- 🔍 **Student Directory**: Public-facing searchable directory with filters
- 🎓 **Multi-Faculty Support**: 12 faculties with 50+ departments
- 🌐 **Privacy Controls**: Three-level visibility settings (Public, Students Only, Private)

---

## ✨ Features

### For Students

- **Registration & Profile Creation**
  - Multi-step registration form
  - Email verification with university domain
  - National ID validation (14 digits)
  - Profile photo and ID scan upload
  - Bio, interests, and social links (LinkedIn, GitHub)

- **Profile Management**
  - Edit profile information
  - Update profile photo
  - Set profile visibility (Public/Students Only/Private)
  - View own profile and statistics

- **Student Directory**
  - Browse approved student profiles
  - Search by name
  - Filter by faculty
  - View public and students-only profiles

### For Administrators

- **User Management**
  - View pending registrations
  - Approve or reject user accounts
  - Provide rejection reasons
  - Verify email addresses
  - Promote users to admin role

- **Dashboard & Analytics**
  - User statistics (total, pending, approved, rejected)
  - Faculty distribution charts
  - Role-based metrics

- **Content Moderation**
  - Manage banned words list
  - Review flagged content
  - Monitor user-generated content

### Security Features

- **Authentication & Authorization**
  - JWT token-based authentication
  - 24-hour token expiration
  - Role-based access control (RBAC)
  - BCrypt password hashing

- **Rate Limiting**
  - Login: 5 attempts per 15 minutes
  - Signup: 3 attempts per 60 minutes
  - IP-based tracking

- **Data Protection**
  - CORS configuration with allowed origins
  - Input validation with Jakarta Bean Validation
  - SQL injection prevention with JPA
  - XSS protection with content sanitization

---

## 🏗️ Architecture

CampusCard follows a modern, scalable architecture pattern:

```
┌─────────────────┐
│   React SPA     │  Frontend (Vite + React 19)
│   (Port 3000)   │  - React Router for navigation
└────────┬────────┘  - React Query for state management
         │           - Axios for HTTP requests
         │ REST API
         ▼
┌─────────────────┐
│  Spring Boot    │  Backend (Spring Boot 4.0)
│   (Port 8080)   │  - JWT Authentication
└────────┬────────┘  - JPA/Hibernate ORM
         │           - Flyway migrations
         │
    ┌────┴────┬──────────────┐
    ▼         ▼              ▼
┌─────────┐ ┌──────────┐ ┌──────────┐
│PostgreSQL│ │  MinIO   │ │  Email   │
│  (5432) │ │  (9000)  │ │  SMTP    │
└─────────┘ └──────────┘ └──────────┘
   Database   Object Store   Notifications
```

### Design Patterns

- **Layered Architecture**: Controller → Service → Repository → Database
- **DTO Pattern**: Separate data transfer objects for API communication
- **Repository Pattern**: Data access abstraction with Spring Data JPA
- **Builder Pattern**: Used extensively with Lombok for object creation
- **Singleton Pattern**: Spring beans are singletons by default
- **Strategy Pattern**: Content moderation and validation strategies

---

## 🛠️ Tech Stack

### Backend

| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| **Framework** | Spring Boot | 4.0.0 | Application framework |
| **Language** | Java | 17 | Programming language |
| **Database** | PostgreSQL | 16 | Relational database |
| **ORM** | Hibernate/JPA | 6.x | Object-relational mapping |
| **Migration** | Flyway | 10.x | Database version control |
| **Security** | Spring Security | 6.x | Authentication & authorization |
| **JWT** | JJWT | 0.12.6 | Token generation & validation |
| **Object Storage** | MinIO | 8.5.7 | File storage |
| **Rate Limiting** | Bucket4j | 8.7.0 | Request rate limiting |
| **Email** | Spring Mail | 3.x | Email notifications |
| **Validation** | Jakarta Validation | 3.x | Input validation |
| **Testing** | JUnit + Testcontainers | 5.x | Unit & integration testing |
| **Build** | Maven | 3.9+ | Dependency management |

### Frontend

| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| **Framework** | React | 19.2.3 | UI library |
| **Router** | React Router DOM | 7.11.0 | Client-side routing |
| **State Management** | React Query | 5.90.12 | Server state management |
| **Forms** | React Hook Form | 7.69.0 | Form handling |
| **Validation** | Zod | 4.2.1 | Schema validation |
| **HTTP Client** | Axios | 1.13.2 | API requests |
| **Styling** | Tailwind CSS | 4.1.18 | Utility-first CSS |
| **Icons** | React Icons | 5.5.0 | Icon library |
| **Build Tool** | Vite | 7.3.0 | Fast dev server & bundler |
| **Package Manager** | npm | 10+ | Dependency management |

### Infrastructure

- **Docker Compose**: Local development environment
- **Git**: Version control
- **GitHub**: Repository hosting

---

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

### Required

- **Java 17** or higher ([Download](https://adoptium.net/))
  ```bash
  java -version  # Should show 17+
  ```

- **Maven 3.9+** (or use included wrapper)
  ```bash
  mvn -version
  ```

- **Node.js 20+** and **npm 10+** ([Download](https://nodejs.org/))
  ```bash
  node --version  # Should show 20+
  npm --version   # Should show 10+
  ```

- **Docker** and **Docker Compose** ([Download](https://www.docker.com/))
  ```bash
  docker --version
  docker compose version
  ```

### Optional

- **PostgreSQL 16** (if not using Docker)
- **MinIO** (if not using Docker)
- **Git** for version control

---

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/mohamed20o03/campuscard.git
cd campuscard
```

### 2. Start Infrastructure Services

Start PostgreSQL and MinIO using Docker Compose:

```bash
cd backend
docker compose up -d
```

This will start:
- PostgreSQL on port `5432`
- MinIO on port `9000` (API) and `9001` (Console)

Verify services are running:
```bash
docker compose ps
```

### 3. Configure Environment Variables

Copy the example environment file and configure it:

```bash
cd backend
cp .env.example .env
```

Edit `.env` and set the following critical variables:

```properties
# JWT Secret (REQUIRED - Generate a secure key)
JWT_SECRET=your-secure-jwt-secret-here

# Admin Credentials (Change these!)
ADMIN_EMAIL=admin@eng.psu.edu.eg
ADMIN_PASSWORD=your-secure-password
ADMIN_NATIONAL_ID=30000000000000

# Database (Default values work with Docker Compose)
DB_URL=jdbc:postgresql://localhost:5432/campuscard
DB_USERNAME=campuscard_user
DB_PASSWORD=campuscard_password

# MinIO (Default values work with Docker Compose)
MINIO_URL=http://localhost:9000
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin123

# Frontend URL
FRONTEND_URL=http://localhost:5173

# CORS Allowed Origins
ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173
```

**Generate a secure JWT secret:**
```bash
openssl rand -hex 32
```

### 4. Start the Backend

```bash
cd backend

# Using Maven wrapper (recommended)
./mvnw spring-boot:run

# Or using installed Maven
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

**First-time setup:**
- Database migrations run automatically (Flyway)
- Admin user is created with credentials from `.env`
- 12 faculties and 50+ departments are seeded

### 5. Start the Frontend

In a new terminal:

```bash
cd frontend
npm install
npm run dev
```

The frontend will start on `http://localhost:5173`

### 6. Access the Application

- **Frontend**: http://localhost:5173
- **Backend API**: http://localhost:8080
- **MinIO Console**: http://localhost:9001
  - Username: `minioadmin`
  - Password: `minioadmin123`

### 7. Login as Admin

Use the credentials you set in `.env`:

- **Email**: Value of `ADMIN_EMAIL`
- **Password**: Value of `ADMIN_PASSWORD`

---

## ⚙️ Configuration

### Environment Variables

All configuration is managed through environment variables. See [.env.example](backend/.env.example) for the complete list.

### Key Configuration Files

- **Backend**:
  - `backend/src/main/resources/application.properties` - Spring Boot configuration
  - `backend/.env` - Environment-specific secrets (not committed)
  - `backend/pom.xml` - Maven dependencies

- **Frontend**:
  - `frontend/vite.config.js` - Vite + Tailwind CSS 4 configuration
  - `frontend/src/core/theme/` - Theme constants (colors, styles)
  - `frontend/package.json` - npm dependencies

### Database Configuration

The application uses PostgreSQL with Flyway for migrations.

**Migration files** are located in `backend/src/main/resources/db/migration/`:
- `V1__init.sql` - Initial schema (users, profiles, faculties, departments)
- `V2__add_email_verification.sql` - Email verification feature
- `V3__add_students_only_visibility.sql` - Profile visibility options
- `V4__add_realistic_banned_words.sql` - Content moderation

**Connection Settings**:
```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

---

## 📁 Project Structure

```
campuscard/
├── backend/                          # Backend (Spring Boot)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/abdelwahab/CampusCard/
│   │   │   │   ├── domain/              # Domain-driven feature modules
│   │   │   │   │   ├── academic/        # Faculty/Department management
│   │   │   │   │   │   ├── dto/
│   │   │   │   │   │   ├── model/
│   │   │   │   │   │   └── repository/
│   │   │   │   │   ├── admin/           # Admin operations
│   │   │   │   │   │   ├── controller/
│   │   │   │   │   │   ├── dto/
│   │   │   │   │   │   └── service/
│   │   │   │   │   ├── auth/            # Authentication
│   │   │   │   │   │   ├── controller/
│   │   │   │   │   │   ├── dto/
│   │   │   │   │   │   └── service/
│   │   │   │   │   ├── common/          # Shared components
│   │   │   │   │   │   ├── config/      # SecurityConfig, OpenApiConfig, etc.
│   │   │   │   │   │   ├── constants/
│   │   │   │   │   │   ├── dto/
│   │   │   │   │   │   ├── exception/   # GlobalExceptionHandler, custom exceptions
│   │   │   │   │   │   ├── security/    # JwtService, JwtAuthFilter, RateLimiter
│   │   │   │   │   │   └── validation/
│   │   │   │   │   ├── moderation/      # Content moderation
│   │   │   │   │   ├── profile/         # Profile management
│   │   │   │   │   │   ├── controller/
│   │   │   │   │   │   ├── dto/
│   │   │   │   │   │   ├── model/
│   │   │   │   │   │   ├── repository/
│   │   │   │   │   │   └── service/
│   │   │   │   │   ├── storage/         # File storage (MinIO)
│   │   │   │   │   └── user/            # User management
│   │   │   │   │       ├── model/
│   │   │   │   │       └── repository/
│   │   │   │   └── CampusCardApplication.java
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── db/migration/        # Flyway migrations
│   │   └── test/                        # Test files
│   ├── .env.example                     # Environment template
│   ├── pom.xml                          # Maven configuration
│   └── docker-compose.yml               # Local infrastructure
│
├── frontend/                            # Frontend (React)
│   ├── src/
│   │   ├── app/                         # App setup
│   │   │   ├── App.jsx
│   │   │   ├── router.jsx
│   │   │   └── providers.jsx
│   │   ├── assets/                      # Static assets (psu-logo.png, login-bg.jpg)
│   │   ├── components/                  # Reusable components
│   │   │   ├── MainLayout.jsx
│   │   │   └── ui/                      # UI primitives
│   │   ├── core/                        # Core utilities
│   │   │   ├── api/                     # API client
│   │   │   ├── auth/                    # Authentication
│   │   │   ├── config/                  # Configuration
│   │   │   └── theme/                   # Theme constants (colors, styles)
│   │   └── features/                    # Feature modules
│   │       ├── admin/                   # Admin features
│   │       ├── auth/                    # Authentication
│   │       ├── profile/                 # Profile management
│   │       └── public/                  # Public pages
│   ├── package.json
│   └── vite.config.js                   # Vite + Tailwind CSS 4 configuration
│
├── docs/                                # Documentation
│   ├── SECURITY.md                      # Security documentation
│   ├── API.md                           # API reference
│   ├── ARCHITECTURE.md                  # Architecture overview
│   ├── DATABASE.md                      # Database schema
│   ├── DEPLOYMENT.md                    # Deployment guide
│   ├── DEVELOPMENT.md                   # Development guide
│   └── ADMIN_GUIDE.md                   # Admin manual
│
└── README.md                            # This file
```

---

## 📚 API Documentation

### Authentication Endpoints

| Endpoint | Method | Description | Auth Required |
|----------|--------|-------------|---------------|
| `/api/login` | POST | Authenticate user | No |
| `/api/signup` | POST | Register new user | No |

### User Endpoints

| Endpoint | Method | Description | Auth Required |
|----------|--------|-------------|---------------|
| `/api/user/me` | GET | Get current user | Yes |
| `/api/user/profile` | GET | Get own profile | Yes |

### Profile Endpoints

| Endpoint | Method | Description | Auth Required |
|----------|--------|-------------|---------------|
| `/api/profile/{userId}` | GET | Get user profile | No* |
| `/api/profile` | PUT | Update profile | Yes |
| `/api/profile/photo` | POST | Upload profile photo | Yes |

*Visibility enforced at service layer

### Admin Endpoints

| Endpoint | Method | Description | Auth Required |
|----------|--------|-------------|---------------|
| `/api/admin/pending-users` | GET | List pending users | Admin |
| `/api/admin/users/{userId}` | GET | Get user details | Admin |
| `/api/admin/users/{userId}/approve` | POST | Approve user | Admin |
| `/api/admin/users/{userId}/reject` | POST | Reject user | Admin |
| `/api/admin/users/{userId}/verify-email` | POST | Verify email | Admin |
| `/api/admin/dashboard` | GET | Get statistics | Admin |

### Public Endpoints

| Endpoint | Method | Description | Auth Required |
|----------|--------|-------------|---------------|
| `/api/public/faculties` | GET | List all faculties | No |
| `/api/public/departments/{facultyId}` | GET | List departments | No |
| `/api/public/students` | GET | List public students | No |

For detailed API documentation with request/response examples, see [docs/API.md](docs/API.md).

---

## 💻 Development

### Running in Development Mode

**Backend** (with hot reload):
```bash
cd backend
./mvnw spring-boot:run
```

**Frontend** (with hot reload):
```bash
cd frontend
npm run dev
```

### Code Style

**Backend (Java)**:
- Follow standard Java naming conventions
- Use Lombok to reduce boilerplate
- Add Javadoc to all public methods
- Maximum line length: 120 characters
- Maximum method length: 50 lines

**Frontend (JavaScript/React)**:
- Use functional components with hooks
- Follow React best practices
- Add JSDoc comments
- Use Tailwind CSS for styling
- Maximum component length: 300 lines

### Linting

**Frontend**:
```bash
cd frontend
npm run lint
```

### Database Migrations

Create a new migration:

1. Create a new file: `backend/src/main/resources/db/migration/V{version}__{description}.sql`
2. Write SQL commands
3. Restart the application (Flyway runs automatically)

Example:
```sql
-- V5__add_user_preferences.sql
ALTER TABLE users ADD COLUMN notification_enabled BOOLEAN DEFAULT true;
```

### Adding New Features

See [docs/DEVELOPMENT.md](docs/DEVELOPMENT.md) for detailed development guidelines.

---

## 🧪 Testing

### Backend Tests

Run all tests:
```bash
cd backend
./mvnw test
```

Run specific test class:
```bash
./mvnw test -Dtest=LoginServiceTest
```

Run integration tests:
```bash
./mvnw verify
```

### Frontend Tests

```bash
cd frontend
npm run test
```

### Test Coverage

Generate coverage report:
```bash
cd backend
./mvnw test jacoco:report
```

View report: `backend/target/site/jacoco/index.html`

**Current Coverage Goals**:
- Backend: 80%+
- Frontend: 70%+

---

## 🚢 Deployment

### Production Checklist

Before deploying to production:

- [ ] Change `JWT_SECRET` to a strong, unique value
- [ ] Update `ADMIN_PASSWORD` to a secure password
- [ ] Set `ALLOWED_ORIGINS` to your production domain
- [ ] Configure `MAIL_*` variables for email notifications
- [ ] Set `TESTING_MODE=false`
- [ ] Use HTTPS for all connections
- [ ] Configure backup strategy for PostgreSQL
- [ ] Set up monitoring and logging
- [ ] Review and test rate limits
- [ ] Configure proper error pages

### Deployment Options

**Option 1: Docker Compose (Simple)**

See [docs/DEPLOYMENT.md](docs/DEPLOYMENT.md) for detailed deployment instructions.

**Option 2: Separate Services**

- Backend: Deploy Spring Boot JAR on any Java hosting
- Frontend: Deploy static build to Vercel/Netlify/S3
- Database: Use managed PostgreSQL (AWS RDS, DigitalOcean)
- Storage: Use MinIO or S3

**Option 3: Kubernetes**

For scaling and high availability (documentation coming soon).

---

## 🔒 Security

Security is a top priority for CampusCard. We implement industry-standard security practices:

### Authentication & Authorization
- JWT token-based authentication
- BCrypt password hashing (10 rounds)
- Role-based access control
- 24-hour token expiration

### Protection Mechanisms
- Rate limiting on authentication endpoints
- CORS with restricted origins
- Input validation on all endpoints
- SQL injection prevention with JPA
- Content moderation for user-generated content

### Best Practices
- No hardcoded credentials
- Environment variables for secrets
- Secure file upload validation
- Regular security audits

For complete security documentation, see [docs/SECURITY.md](docs/SECURITY.md).

### Reporting Security Issues

If you discover a security vulnerability, please email **security@campuscard.edu** instead of using the public issue tracker.

---

## 🤝 Contributing

We welcome contributions! Please follow these guidelines:

### How to Contribute

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Make your changes**
4. **Add tests** for new functionality
5. **Commit your changes**
   ```bash
   git commit -m "Add amazing feature"
   ```
6. **Push to your fork**
   ```bash
   git push origin feature/amazing-feature
   ```
7. **Open a Pull Request**

### Code Review Process

- All submissions require review
- Tests must pass
- Code must follow style guidelines
- Documentation must be updated

### Development Guidelines

See [docs/DEVELOPMENT.md](docs/DEVELOPMENT.md) for:
- Coding standards
- Git workflow
- Testing requirements
- Documentation standards

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 💬 Support

### Getting Help

- **Documentation**: Check [docs/](docs/) folder
- **Issues**: [GitHub Issues](https://github.com/mohamed20o03/campuscard/issues)
- **Discussions**: [GitHub Discussions](https://github.com/mohamed20o03/campuscard/discussions)
- **Email**: Mohamed170408@eng.psu.edu.eg

### Known Issues

- Email verification requires SMTP configuration
- Profile photos limited to 10MB
- Rate limiting resets on application restart (in-memory)

### Roadmap

- [ ] TypeScript migration for frontend
- [ ] Microservices architecture
- [ ] Real-time notifications
- [ ] Mobile applications (iOS/Android)
- [ ] Advanced search with Elasticsearch
- [ ] Internationalization (i18n)
- [ ] Two-factor authentication (2FA)
- [ ] OAuth integration (Google, Microsoft)

---

## 🙏 Acknowledgments

- Port Said University for project requirements
- Spring Boot team for excellent framework
- React team for powerful UI library
- All contributors who help improve CampusCard

---

## 📊 Project Status

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![Tests](https://img.shields.io/badge/tests-passing-brightgreen)
![Coverage](https://img.shields.io/badge/coverage-80%25-green)
![Version](https://img.shields.io/badge/version-1.0.0-blue)

**Current Version**: 1.0.0  
**Last Updated**: December 24, 2025  
**Status**: Active Development

---

<div align="center">

**Made with ❤️ for Port Said University**

[⬆ Back to Top](#campuscard---university-student-directory-platform)

</div>
