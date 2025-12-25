# CampusCard Development Guide

Complete guide for developers working on the CampusCard project.

## Table of Contents

- [Getting Started](#getting-started)
- [Development Environment](#development-environment)
- [Code Standards](#code-standards)
- [Git Workflow](#git-workflow)
- [Testing Guidelines](#testing-guidelines)
- [Adding New Features](#adding-new-features)
- [Debugging](#debugging)
- [Common Issues](#common-issues)
- [Release Process](#release-process)

---

## Getting Started

### Prerequisites

Ensure you have installed:
- Java 17+
- Maven 3.9+
- Node.js 20+
- Docker & Docker Compose
- Git
- IDE (IntelliJ IDEA recommended for backend, VS Code for frontend)

### First-Time Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/mohamed20o03/campuscard.git
   cd campuscard
   ```

2. **Start infrastructure**:
   ```bash
   cd backend
   docker compose up -d
   ```

3. **Set up environment variables**:
   ```bash
   cp .env.example .env
   # Edit .env with your values
   ```

4. **Start backend**:
   ```bash
   ./mvnw spring-boot:run
   ```

5. **Start frontend**:
   ```bash
   cd ../frontend
   npm install
   npm run dev
   ```

---

## Development Environment

### IDE Configuration

#### IntelliJ IDEA (Backend)

1. **Import Project**:
   - File → Open → Select `backend/pom.xml`
   - Import as Maven project

2. **Install Plugins**:
   - Lombok Plugin (Required)
   - SonarLint (Recommended)
   - CheckStyle-IDEA (Recommended)

3. **Enable Annotation Processing**:
   - Settings → Build, Execution, Deployment → Compiler → Annotation Processors
   - Check "Enable annotation processing"

4. **Code Style**:
   - Settings → Editor → Code Style → Java
   - Scheme: Google Java Style (recommended)
   - Line length: 120

#### VS Code (Frontend)

1. **Install Extensions**:
   ```
   - ESLint
   - Prettier
   - Tailwind CSS IntelliSense
   - ES7+ React/Redux/React-Native snippets
   ```

2. **Settings (.vscode/settings.json)**:
   ```json
   {
     "editor.formatOnSave": true,
     "editor.defaultFormatter": "esbenp.prettier-vscode",
     "editor.codeActionsOnSave": {
       "source.fixAll.eslint": true
     }
   }
   ```

### Hot Reload

Both backend and frontend support hot reload:

**Backend**: Spring Boot DevTools automatically reloads on file changes
**Frontend**: Vite HMR (Hot Module Replacement) updates instantly

---

## Code Standards

### Backend (Java)

#### Naming Conventions

| Element | Convention | Example |
|---------|-----------|---------|
| Class | PascalCase | `UserService`, `ProfileController` |
| Method | camelCase | `findUserById`, `approveUser` |
| Variable | camelCase | `userId`, `emailAddress` |
| Constant | UPPER_SNAKE_CASE | `MAX_FILE_SIZE`, `JWT_EXPIRATION` |
| Package | lowercase | `com.abdelwahab.campuscard.service` |

#### Code Style

**Always use**:
- `Long` for IDs (not `Integer`)
- Lombok annotations (`@Data`, `@Builder`, `@Slf4j`)
- Jakarta Validation annotations (`@NotNull`, `@Email`, `@Size`)
- Constructor injection (not field injection)

**Example**:
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    
    /**
     * Finds a user by their unique identifier.
     *
     * @param userId the user ID
     * @return the user
     * @throws UserNotFoundException if user not found
     */
    public User findById(Long userId) {
        log.debug("Finding user by ID: {}", userId);
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
```

#### Javadoc Requirements

All public classes and methods must have Javadoc:

```java
/**
 * Service for managing user profiles.
 * Handles profile CRUD operations and visibility enforcement.
 *
 * @author Your Name
 * @version 1.0
 * @since 1.0
 */
public class ProfileService {
    
    /**
     * Updates a user's profile with new information.
     *
     * @param userId the ID of the user
     * @param request the profile update request
     * @return the updated profile
     * @throws ProfileNotFoundException if profile not found
     * @throws ContentModerationException if content contains banned words
     */
    public ProfileResponse updateProfile(Long userId, ProfileUpdateRequest request) {
        // implementation
    }
}
```

### Frontend (JavaScript/React)

#### Naming Conventions

| Element | Convention | Example |
|---------|-----------|---------|
| Component | PascalCase | `LoginPage`, `StudentCard` |
| Function | camelCase | `handleSubmit`, `fetchStudents` |
| Variable | camelCase | `userData`, `isLoading` |
| Constant | UPPER_SNAKE_CASE | `API_BASE_URL`, `MAX_BIO_LENGTH` |
| File (component) | PascalCase.jsx | `LoginPage.jsx` |
| File (utility) | camelCase.js | `apiClient.js` |

#### Component Structure

```javascript
/**
 * Login page component.
 * Handles user authentication and redirects based on role.
 *
 * @component
 * @returns {JSX.Element} The login page
 */
export default function LoginPage() {
  // 1. Hooks
  const navigate = useNavigate();
  const { setSession } = useAuth();
  
  // 2. State
  const [email, setEmail] = useState("");
  const [loading, setLoading] = useState(false);
  
  // 3. Effects
  useEffect(() => {
    // Effect logic
  }, []);
  
  // 4. Event handlers
  async function handleSubmit(e) {
    e.preventDefault();
    // Handler logic
  }
  
  // 5. Render
  return (
    <div>
      {/* JSX */}
    </div>
  );
}
```

#### JSDoc for Functions

```javascript
/**
 * Authenticates a user with email and password.
 *
 * @async
 * @param {Object} credentials - The login credentials
 * @param {string} credentials.email - User's email
 * @param {string} credentials.password - User's password
 * @returns {Promise<Object>} Authentication response with token
 * @throws {Error} If credentials are invalid
 */
export async function loginRequest(credentials) {
  return apiFetch("/api/login", {
    method: "POST",
    body: JSON.stringify(credentials)
  });
}
```

---

## Git Workflow

### Branch Strategy

```
main (production)
  └── develop (integration)
      ├── feature/user-authentication
      ├── feature/profile-management
      ├── bugfix/login-error
      └── hotfix/security-patch
```

### Branch Naming

| Type | Pattern | Example |
|------|---------|---------|
| Feature | `feature/description` | `feature/add-search-filter` |
| Bug Fix | `bugfix/description` | `bugfix/fix-login-redirect` |
| Hotfix | `hotfix/description` | `hotfix/jwt-validation` |
| Refactor | `refactor/description` | `refactor/service-layer` |

### Commit Messages

Follow Conventional Commits:

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types**:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation
- `style`: Code style (formatting)
- `refactor`: Code refactoring
- `test`: Adding tests
- `chore`: Maintenance

**Examples**:
```
feat(auth): add rate limiting to login endpoint

Implement Bucket4j rate limiting to prevent brute force attacks.
Login endpoint now limited to 5 attempts per 15 minutes.

Closes #123

fix(profile): correct visibility check for private profiles

Private profiles were visible to unauthenticated users.
Fixed service layer to properly enforce visibility rules.

docs: update README with deployment instructions
```

### Pull Request Process

1. **Create Feature Branch**:
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b feature/my-feature
   ```

2. **Make Changes**:
   ```bash
   git add .
   git commit -m "feat: add my feature"
   ```

3. **Push Branch**:
   ```bash
   git push origin feature/my-feature
   ```

4. **Open Pull Request**:
   - Base: `develop`
   - Compare: `feature/my-feature`
   - Fill in PR template
   - Request reviewers

5. **Code Review**:
   - Address feedback
   - Update PR

6. **Merge**:
   - Squash and merge (for feature branches)
   - Delete branch after merge

---

## Testing Guidelines

### Backend Testing

#### Unit Tests

Test services in isolation using Mockito:

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void findById_WhenUserExists_ReturnsUser() {
        // Arrange
        Long userId = 1L;
        User user = User.builder().id(userId).build();
        when(userRepository.findById(userId))
            .thenReturn(Optional.of(user));
        
        // Act
        User result = userService.findById(userId);
        
        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(userRepository).findById(userId);
    }
    
    @Test
    void findById_WhenUserNotExists_ThrowsException() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId))
            .thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(UserNotFoundException.class,
            () -> userService.findById(userId));
    }
}
```

#### Integration Tests

Test complete flows with Testcontainers:

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
class LoginControllerIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void login_WithValidCredentials_ReturnsToken() {
        // Arrange
        LoginRequest request = new LoginRequest(
            "test@eng.psu.edu.eg",
            "password123"
        );
        
        // Act
        ResponseEntity<LoginResponse> response = 
            restTemplate.postForEntity("/api/login", request, LoginResponse.class);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getToken());
    }
}
```

### Frontend Testing

#### Component Tests

```javascript
import { render, screen, fireEvent } from '@testing-library/react';
import { LoginPage } from './LoginPage';

describe('LoginPage', () => {
  test('renders login form', () => {
    render(<LoginPage />);
    
    expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /login/i })).toBeInTheDocument();
  });
  
  test('submits form with valid data', async () => {
    const mockLogin = jest.fn();
    render(<LoginPage onLogin={mockLogin} />);
    
    fireEvent.change(screen.getByLabelText(/email/i), {
      target: { value: 'test@eng.psu.edu.eg' }
    });
    fireEvent.change(screen.getByLabelText(/password/i), {
      target: { value: 'password123' }
    });
    fireEvent.click(screen.getByRole('button', { name: /login/i }));
    
    await waitFor(() => {
      expect(mockLogin).toHaveBeenCalledWith({
        email: 'test@eng.psu.edu.eg',
        password: 'password123'
      });
    });
  });
});
```

### Test Coverage

Run coverage reports:

```bash
# Backend
./mvnw test jacoco:report

# Frontend
npm run test:coverage
```

**Coverage Goals**:
- Service layer: 90%+
- Controller layer: 80%+
- Overall backend: 80%+
- Frontend components: 70%+

---

## Adding New Features

### Backend Feature Checklist

- [ ] Create entity (if needed)
- [ ] Create repository
- [ ] Create service with business logic
- [ ] Create DTO classes
- [ ] Create controller endpoint
- [ ] Add validation annotations
- [ ] Write Javadoc
- [ ] Write unit tests
- [ ] Write integration tests
- [ ] Update API documentation

### Frontend Feature Checklist

- [ ] Create component
- [ ] Add routing (if page)
- [ ] Create API function
- [ ] Add state management
- [ ] Add form validation (if form)
- [ ] Write JSDoc
- [ ] Add component tests
- [ ] Update user documentation

---

## Debugging

### Backend Debugging

**Enable debug logging**:
```properties
logging.level.com.abdelwahab.CampusCard=DEBUG
```

**IntelliJ Debug Configuration**:
1. Run → Edit Configurations
2. Add new "Spring Boot" configuration
3. Set main class: `CampusCardApplication`
4. Set debug port: 5005

**Remote debugging**:
```bash
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

### Frontend Debugging

**React DevTools**: Install browser extension

**Console logging**:
```javascript
console.log('State:', state);
console.table(users);
console.error('Error:', error);
```

**Network debugging**: Use browser DevTools Network tab

---

## Common Issues

### Backend Issues

**Issue**: `Port 8080 already in use`
```bash
# Find and kill process
lsof -ti:8080 | xargs kill -9
```

**Issue**: `Database connection failed`
- Check Docker containers: `docker compose ps`
- Restart PostgreSQL: `docker compose restart postgres`

**Issue**: `Flyway migration failed`
- Check migration syntax
- Rollback: `./mvnw flyway:clean` (WARNING: Deletes all data)

### Frontend Issues

**Issue**: `Module not found`
```bash
rm -rf node_modules package-lock.json
npm install
```

**Issue**: `CORS error`
- Check `ALLOWED_ORIGINS` in backend `.env`
- Verify frontend URL matches

---

## Release Process

### Version Numbering

Follow Semantic Versioning (SemVer): `MAJOR.MINOR.PATCH`

- **MAJOR**: Breaking changes
- **MINOR**: New features (backward compatible)
- **PATCH**: Bug fixes

### Release Steps

1. **Update version**:
   - `pom.xml`: `<version>1.1.0</version>`
   - `package.json`: `"version": "1.1.0"`

2. **Update CHANGELOG.md**

3. **Create release branch**:
   ```bash
   git checkout -b release/1.1.0
   ```

4. **Run tests**:
   ```bash
   ./mvnw clean verify
   npm run test
   ```

5. **Build production**:
   ```bash
   ./mvnw clean package -DskipTests
   cd frontend && npm run build
   ```

6. **Tag release**:
   ```bash
   git tag -a v1.1.0 -m "Release version 1.1.0"
   git push origin v1.1.0
   ```

7. **Deploy** (see [DEPLOYMENT.md](DEPLOYMENT.md))

---

## Additional Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [React Documentation](https://react.dev/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [MinIO Documentation](https://min.io/docs/minio/linux/index.html)

---

**Last Updated**: December 24, 2025  
**Maintainer**: Mohamed170408@eng.psu.edu.eg
