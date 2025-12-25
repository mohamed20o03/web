# Code Quality Guidelines

This document outlines the code quality standards, conventions, and tools used in the CampusCard project.

## Table of Contents

1. [Naming Conventions](#naming-conventions)
2. [Code Structure](#code-structure)
3. [Backend Standards (Java)](#backend-standards-java)
4. [Frontend Standards (JavaScript/React)](#frontend-standards-javascriptreact)
5. [Constants and Magic Numbers](#constants-and-magic-numbers)
6. [Quality Tools](#quality-tools)
7. [Pre-Commit Checklist](#pre-commit-checklist)

---

## Naming Conventions

### Java Backend

| Element | Convention | Example |
|---------|------------|---------|
| Classes | PascalCase | `UserService`, `ProfileController` |
| Interfaces | PascalCase (no prefix) | `UserRepository`, `ProfileService` |
| Methods | camelCase | `findUserById`, `approveUser` |
| Variables | camelCase | `userId`, `emailToken` |
| Constants | UPPER_SNAKE_CASE | `MAX_FILE_SIZE`, `DEFAULT_PAGE_SIZE` |
| Packages | lowercase | `com.abdelwahab.campuscard.domain.user` |
| Enums | PascalCase (values UPPER_SNAKE_CASE) | `UserStatus.APPROVED` |

### JavaScript/React Frontend

| Element | Convention | Example |
|---------|------------|---------|
| Components | PascalCase | `LoginPage`, `StudentCard` |
| Functions | camelCase | `handleSubmit`, `fetchStudents` |
| Variables | camelCase | `userData`, `isLoading` |
| Constants | UPPER_SNAKE_CASE | `API_BASE_URL`, `MAX_BIO_LENGTH` |
| Files (components) | PascalCase | `LoginPage.jsx`, `StudentCard.jsx` |
| Files (utilities) | camelCase | `apiClient.js`, `auth.storage.js` |
| CSS modules | camelCase | `loginPage.module.css` → `styles.container` |

---

## Code Structure

### Method Complexity Guidelines

- **Maximum method length**: 50 lines (soft limit), 75 lines (hard limit)
- **Maximum cyclomatic complexity**: 15
- **Maximum parameters**: 7 (use DTOs for more)
- **Maximum nesting depth**: 4 levels

### Refactoring Large Methods

**Before (too complex):**
```java
public void approveUser(Long userId, String adminEmail) {
    User user = userRepository.findById(userId).orElseThrow(...);
    if (!user.isEmailVerified()) {
        throw new RuntimeException("Email not verified");
    }
    if (user.getStatus() == UserStatus.APPROVED) {
        throw new RuntimeException("Already approved");
    }
    user.setStatus(UserStatus.APPROVED);
    userRepository.save(user);
    String emailBody = "Your account has been approved...";
    emailService.sendEmail(user.getEmail(), "Account Approved", emailBody);
    log.info("Admin {} approved user {}", adminEmail, userId);
}
```

**After (refactored):**
```java
public void approveUser(Long userId, String adminEmail) {
    User user = findAndValidateUser(userId);
    updateUserStatus(user, UserStatus.APPROVED);
    notifyUserOfApproval(user);
    auditService.logApproval(adminEmail, userId);
}

private User findAndValidateUser(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));
    
    if (!user.isEmailVerified()) {
        throw new EmailNotVerifiedException(user.getEmail());
    }
    
    return user;
}
```

---

## Backend Standards (Java)

### Import Organization

```java
// 1. Java standard library
import java.util.*;
import java.io.*;

// 2. Jakarta/Javax packages
import jakarta.validation.*;

// 3. Third-party libraries (Spring, etc.)
import org.springframework.*;

// 4. Project packages
import com.abdelwahab.CampusCard.*;
```

### Javadoc Requirements

**All public classes must have:**
```java
/**
 * Service responsible for user authentication and login operations.
 * Supports login via email or national ID with JWT token generation.
 *
 * @author CampusCard Team
 * @since 1.0
 */
```

**All public methods must have:**
```java
/**
 * Authenticates a user and generates a JWT token.
 *
 * @param request the login credentials
 * @return LoginResponse containing JWT token
 * @throws InvalidCredentialsException if credentials are invalid
 */
```

### Exception Handling

Use custom exceptions instead of generic ones:

| Instead of | Use |
|------------|-----|
| `RuntimeException("User not found")` | `ResourceNotFoundException(userId)` |
| `RuntimeException("Invalid password")` | `InvalidCredentialsException()` |
| `RuntimeException("Already exists")` | `DuplicateResourceException(email)` |

### Logging Standards

```java
// DEBUG - Detailed diagnostic (disabled in prod)
log.debug("Processing login for: {}", request.getIdentifier());

// INFO - Key business events
log.info("User registered successfully: {}", user.getEmail());

// WARN - Potential issues
log.warn("Login failed for user: {}", identifier);

// ERROR - Actual errors (include stack trace)
log.error("Failed to process request: {}", e.getMessage(), e);
```

---

## Frontend Standards (JavaScript/React)

### Component Structure

```jsx
/**
 * Student profile card component.
 *
 * @component
 * @param {Object} props
 * @param {Object} props.student - Student data object
 * @param {Function} props.onClick - Click handler
 * @returns {JSX.Element}
 */
export function StudentCard({ student, onClick }) {
  // 1. Hooks
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();
  
  // 2. Effects
  useEffect(() => {
    // effect logic
  }, [dependency]);
  
  // 3. Event handlers
  const handleClick = () => {
    onClick?.(student.id);
  };
  
  // 4. Render helpers (if needed)
  const renderStatus = () => (
    <span className={styles.status}>{student.status}</span>
  );
  
  // 5. Main render
  return (
    <div className={styles.card} onClick={handleClick}>
      {/* content */}
    </div>
  );
}
```

### API Function Standards

```javascript
/**
 * Fetches student profile by ID.
 *
 * @async
 * @param {number} studentId - The student's unique identifier
 * @returns {Promise<Object>} Student profile data
 * @throws {Error} If student not found or request fails
 */
export async function fetchStudentProfile(studentId) {
  const response = await http.get(`/api/profile/${studentId}`);
  return response.data;
}
```

---

## Constants and Magic Numbers

### Avoid Magic Numbers

**Bad:**
```java
if (password.length() < 8) { ... }
if (file.getSize() > 10485760) { ... }
```

**Good:**
```java
import static com.abdelwahab.CampusCard.domain.common.constants.ValidationConstants.*;

if (password.length() < MIN_PASSWORD_LENGTH) { ... }
if (file.getSize() > MAX_FILE_SIZE_BYTES) { ... }
```

### Using Constants

**Backend (Java):**
```java
import com.abdelwahab.CampusCard.domain.common.constants.ValidationConstants;
import com.abdelwahab.CampusCard.domain.common.constants.ErrorMessages;

// Validation
if (bio.length() > ValidationConstants.MAX_BIO_LENGTH) {
    throw new ValidationException(ErrorMessages.FIELD_TOO_LONG);
}
```

**Frontend (JavaScript):**
```javascript
import { MAX_BIO_LENGTH, FIELD_TOO_LONG } from '@/core/constants';

// Validation
if (bio.length > MAX_BIO_LENGTH) {
    setError(FIELD_TOO_LONG);
}
```

---

## Quality Tools

### Backend: Checkstyle

Run code quality analysis:
```bash
cd backend
./mvnw checkstyle:check
```

Configuration: `config/checkstyle/checkstyle.xml`

Key rules enforced:
- Naming conventions
- Method length (max 75 lines)
- Cyclomatic complexity (max 15)
- Javadoc on public elements
- Import organization
- Magic number detection

### Backend: JaCoCo (Test Coverage)

Run tests with coverage:
```bash
./mvnw test jacoco:report
```

Coverage report: `target/site/jacoco/index.html`

Coverage requirements:
- Overall: 70% minimum
- Service layer: 80%+ recommended
- Controller layer: 70%+ recommended

### Frontend: ESLint

Run linting:
```bash
cd frontend
npm run lint
```

Fix auto-fixable issues:
```bash
npm run lint -- --fix
```

Configuration: `eslint.config.js`

Key rules enforced:
- Unused variables
- Prefer const
- Function complexity
- React hooks rules
- Consistent styling

---

## Pre-Commit Checklist

Before committing code, ensure:

### Backend
- [ ] Code compiles without errors: `./mvnw compile`
- [ ] All tests pass: `./mvnw test`
- [ ] No critical Checkstyle violations: `./mvnw checkstyle:check`
- [ ] Public classes/methods have Javadoc
- [ ] No magic numbers (use constants)
- [ ] Appropriate logging added
- [ ] Custom exceptions used (not RuntimeException)

### Frontend
- [ ] No ESLint errors: `npm run lint`
- [ ] Components have JSDoc comments
- [ ] No console.log statements (except debug)
- [ ] Constants used for validation rules
- [ ] Error messages from constants file

### General
- [ ] Commit message is descriptive
- [ ] Branch is up to date with main
- [ ] No sensitive data in code (passwords, secrets)
- [ ] Related tests added/updated

---

## IDE Setup

### IntelliJ IDEA (Backend)

1. Import Checkstyle plugin
2. Configure: Settings → Tools → Checkstyle
3. Add configuration file: `config/checkstyle/checkstyle.xml`
4. Enable real-time checking

### VS Code (Frontend)

1. Install ESLint extension
2. Enable auto-fix on save:
   ```json
   {
     "editor.codeActionsOnSave": {
       "source.fixAll.eslint": true
     }
   }
   ```

---

**Last Updated**: December 25, 2025  
**Version**: 1.0  
**Maintainer**: Mohamed170408@eng.psu.edu.eg
