# API Documentation with Swagger/OpenAPI

## Overview

CampusCard API documentation is now available through Swagger UI, providing interactive API exploration and testing capabilities.

## Accessing the Documentation

### Local Development

When running the application locally, access the documentation at:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **OpenAPI YAML**: http://localhost:8080/v3/api-docs.yaml

### Production

Replace `localhost:8080` with your production domain (e.g., `https://api.campuscard.psu.edu.eg`).

## Features

### Interactive API Testing

- **Try It Out**: Test API endpoints directly from the browser
- **Request Examples**: Pre-filled example requests for each endpoint
- **Response Examples**: Sample responses for success and error cases
- **Authentication**: Built-in JWT token management for authenticated requests

### Comprehensive Documentation

- **Endpoint Descriptions**: Detailed information about each API operation
- **Parameters**: Description, type, and validation rules for all parameters
- **Request/Response Schemas**: Full JSON structure with examples
- **HTTP Status Codes**: All possible response codes with explanations
- **Authentication Flow**: JWT token usage and bearer authentication

## Using JWT Authentication in Swagger UI

1. **Login to Get Token**:
   - Navigate to the `Authentication` section
   - Expand the `POST /api/login` endpoint
   - Click "Try it out"
   - Enter credentials:
     ```json
     {
       "identifier": "test@eng.psu.edu.eg",
       "password": "password123"
     }
     ```
   - Click "Execute"
   - Copy the `token` value from the response

2. **Authorize Requests**:
   - Click the "Authorize" button at the top of the page
   - Enter: `Bearer <your-token>` (replace `<your-token>` with the copied token)
   - Click "Authorize"
   - Click "Close"

3. **Test Protected Endpoints**:
   - All subsequent requests will include the JWT token automatically
   - Try admin endpoints (requires admin role) or user endpoints

## API Endpoints Overview

### Authentication
- `POST /api/login` - Authenticate user and obtain JWT token
- `POST /api/signup` - Register new user account

### Profile Management
- `GET /api/profile` - Get current user's profile
- `PUT /api/profile` - Update current user's profile
- `GET /api/profile/{id}` - View another user's profile (subject to visibility settings)

### File Upload
- `POST /api/profile/photo` - Upload profile photo
- `POST /api/profile/national-id-scan` - Upload national ID scan
- `DELETE /api/profile/photo` - Delete profile photo

### Admin Operations
- `GET /api/admin/users/pending` - List users pending approval
- `POST /api/admin/users/approve-reject` - Approve or reject user
- `GET /api/admin/stats` - Get system statistics

### Public Endpoints
- `GET /api/public/students` - Browse student directory (with filters)
- `GET /api/public/faculties` - List all faculties
- `GET /api/public/departments` - List all departments

## Configuration

### OpenAPI Configuration

The OpenAPI documentation is configured in:
- **File**: `src/main/java/com/abdelwahab/CampusCard/domain/common/config/OpenApiConfig.java`
- **Security**: JWT Bearer authentication
- **Servers**: Local development and production URLs

### Security Configuration

Swagger UI endpoints are publicly accessible (no authentication required):
- `/swagger-ui/**`
- `/v3/api-docs/**`
- `/swagger-ui.html`

This is configured in `SecurityConfig.java`.

## Annotations Used

### Controller Annotations
- `@Tag` - Groups related endpoints
- `@Operation` - Describes the operation
- `@ApiResponses` - Documents possible responses
- `@ApiResponse` - Details for specific response code
- `@Parameter` - Describes request parameters

### DTO Annotations
- `@Schema` - Describes the model/schema
- Properties:
  - `description` - Human-readable description
  - `example` - Example value
  - `requiredMode` - Whether field is required
  - `minLength/maxLength` - String constraints
  - `allowableValues` - Enum values

## Example: Annotated Endpoint

```java
@PostMapping
@Operation(
    summary = "Authenticate user and obtain JWT token",
    description = "Authenticates a user using email or national ID and password..."
)
@ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Authentication successful",
        content = @Content(schema = @Schema(implementation = LoginResponse.class))
    ),
    @ApiResponse(
        responseCode = "401",
        description = "Invalid credentials"
    )
})
public ResponseEntity<LoginResponse> login(
    @Parameter(description = "Login credentials")
    @Valid @RequestBody LoginRequest request
) {
    // implementation
}
```

## Next Steps

To complete Step 9, we need to:

1. ✅ Add SpringDoc OpenAPI dependency
2. ✅ Create OpenAPI configuration class
3. ✅ Annotate LoginController with @Tag, @Operation, @ApiResponses
4. ✅ Annotate LoginRequest and LoginResponse DTOs with @Schema
5. ✅ Configure SecurityConfig to allow Swagger UI access
6. ✅ Verify tests still pass (50/50 tests passing)
7. ⏳ Annotate remaining controllers (AdminController, ProfileController, SignUpController)
8. ⏳ Annotate remaining DTOs
9. ⏳ Test Swagger UI with running application
10. ⏳ Document authentication flow with examples

## Benefits

### For Developers
- **Quick Reference**: No need to dig through code to understand endpoints
- **Interactive Testing**: Test APIs without writing code or using external tools
- **Type Safety**: Schema validation and examples prevent errors
- **Discoverability**: Explore all available endpoints in one place

### For Frontend Developers
- **Contract Definition**: Clear API contract for frontend integration
- **Example Requests**: Copy-paste ready request examples
- **Error Handling**: Understand all possible error responses
- **No Ambiguity**: Precise parameter types and validation rules

### For API Consumers
- **Self-Service**: Explore and test APIs independently
- **Up-to-Date**: Documentation always matches implementation
- **Language Agnostic**: Standard OpenAPI format works with any client
- **Code Generation**: Generate client SDKs automatically

## Resources

- [SpringDoc OpenAPI Documentation](https://springdoc.org/)
- [OpenAPI Specification](https://swagger.io/specification/)
- [Swagger UI Documentation](https://swagger.io/tools/swagger-ui/)
