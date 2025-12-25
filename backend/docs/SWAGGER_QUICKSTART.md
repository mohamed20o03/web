# Quick Start: Viewing Swagger UI

## Prerequisites

1. **Start Docker services:**
   ```bash
   cd /home/eima40x4c/Projects/campuscard/backend
   docker-compose up -d
   ```

2. **Set required environment variables:**
   ```bash
   export JWT_SECRET="testSecretKeyForJWTThatIsLongEnoughForHMACSignatureGeneration123456789"
   export ADMIN_PASSWORD="testAdminPassword123!"
   export ADMIN_NATIONAL_ID="99999999999999"
   ```

3. **Start the application:**
   ```bash
   ./mvnw spring-boot:run
   ```

## Accessing Swagger UI

Once the application starts (wait ~30 seconds), open your browser:

**Swagger UI (Interactive):**
```
http://localhost:8080/swagger-ui.html
```

**OpenAPI JSON Spec:**
```
http://localhost:8080/v3/api-docs
```

**OpenAPI YAML Spec:**
```
http://localhost:8080/v3/api-docs.yaml
```

## What's Documented

### ✅ Fully Documented Endpoints:

#### Authentication
- **POST /api/login** - User authentication with JWT token generation
  - Full request/response examples
  - All status codes (200, 401, 429)
  - Rate limiting documentation
  
- **POST /api/signup** - New user registration
  - Multipart form data examples
  - Complete validation rules
  - Registration workflow documented

#### Admin Operations
- All admin endpoints tagged and grouped
- Security requirement annotations (JWT required)
- ADMIN role requirement documented

#### Profile Management  
- All profile endpoints tagged
- File upload operations documented
- Visibility settings explained

### 📊 Current Step 9 Progress:

✅ **Completed:**
1. SpringDoc OpenAPI dependency added
2. OpenApiConfig created with full configuration
3. SecurityConfig updated (Swagger UI publicly accessible)
4. LoginController - FULLY documented
5. SignUpController - FULLY documented  
6. AdminController - Tagged and security annotations
7. ProfileController - Tagged
8. PublicController - Tagged
9. All tests passing (50/50)

⏳ **Remaining Work:**
- Add @Operation annotations to individual admin endpoints
- Add @Operation annotations to profile management endpoints
- Add @Operation annotations to public endpoints
- Document DTOs with @Schema annotations
- Test Swagger UI with running application

## Using Swagger UI

### 1. Testing Public Endpoints

Navigate to "Authentication" section and try the login endpoint without authentication.

### 2. Testing Protected Endpoints

1. **Login first:**
   - Expand `POST /api/login`
   - Click "Try it out"
   - Enter test credentials:
     ```json
     {
       "identifier": "test@eng.psu.edu.eg",
       "password": "password123"
     }
     ```
   - Click "Execute"
   - Copy the `token` from response

2. **Authorize:**
   - Click the green "Authorize" button (top right)
   - Enter: `Bearer <your-token>`
   - Click "Authorize", then "Close"

3. **Test protected endpoints:**
   - Now all requests will include your JWT token
   - Try profile endpoints, admin endpoints, etc.

## Benefits

- **Interactive testing** - No Postman needed
- **Auto-generated docs** - Always in sync with code
- **Type safety** - Clear parameter types and validation
- **Examples** - Copy-paste ready requests
- **Discovery** - Explore all endpoints in one place

## Next Steps

To complete Step 9:
1. Run the application following steps above
2. Verify Swagger UI is accessible
3. Test authentication flow in Swagger UI
4. (Optional) Add more detailed annotations to remaining endpoints
