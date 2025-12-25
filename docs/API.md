# CampusCard API Documentation

Complete REST API reference for the CampusCard platform.

## Table of Contents

- [Base URL](#base-url)
- [Authentication](#authentication)
- [Response Format](#response-format)
- [Error Handling](#error-handling)
- [Rate Limiting](#rate-limiting)
- [Authentication Endpoints](#authentication-endpoints)
- [User Endpoints](#user-endpoints)
- [Profile Endpoints](#profile-endpoints)
- [Admin Endpoints](#admin-endpoints)
- [Public Endpoints](#public-endpoints)

---

## Base URL

### Development
```
http://localhost:8080
```

### Production
```
https://api.campuscard.youruniversity.edu
```

---

## Authentication

CampusCard uses JWT (JSON Web Token) for authentication.

### Obtaining a Token

Send a POST request to `/api/login` with credentials:

```http
POST /api/login
Content-Type: application/json

{
  "identifier": "student@eng.psu.edu.eg",
  "password": "your-password"
}
```

### Using the Token

Include the token in the Authorization header for protected endpoints:

```http
GET /api/user/me
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Token Expiration

Tokens expire after **24 hours**. When a token expires, you'll receive a `401 Unauthorized` response. Request a new token by logging in again.

---

## Response Format

### Success Response

```json
{
  "id": 123,
  "email": "student@eng.psu.edu.eg",
  "firstName": "John",
  "lastName": "Doe",
  "status": "APPROVED"
}
```

### Error Response

```json
{
  "error": "Validation Error",
  "message": "Email already exists",
  "timestamp": "2025-12-24T10:30:00Z"
}
```

---

## Error Handling

### HTTP Status Codes

| Status Code | Description |
|-------------|-------------|
| `200 OK` | Request succeeded |
| `201 Created` | Resource created successfully |
| `400 Bad Request` | Invalid input or validation error |
| `401 Unauthorized` | Missing or invalid authentication token |
| `403 Forbidden` | Insufficient permissions |
| `404 Not Found` | Resource not found |
| `413 Payload Too Large` | File upload exceeds size limit |
| `429 Too Many Requests` | Rate limit exceeded |
| `500 Internal Server Error` | Server error |

### Common Error Messages

| Error | Description |
|-------|-------------|
| `Invalid credentials` | Wrong email/password |
| `Token expired` | JWT token has expired |
| `Access denied` | Insufficient permissions for this operation |
| `User not found` | User does not exist |
| `Email already exists` | Email is already registered |
| `Invalid national ID format` | National ID must be 14 digits |

---

## Rate Limiting

Rate limits protect against abuse and brute force attacks.

### Limits

| Endpoint | Max Requests | Time Window |
|----------|--------------|-------------|
| `/api/login` | 5 | 15 minutes |
| `/api/signup` | 3 | 60 minutes |

### Rate Limit Headers

```http
X-Rate-Limit-Remaining: 3
```

### Rate Limit Exceeded Response

```json
{
  "error": "Rate limit exceeded for login endpoint",
  "message": "Too many requests. Please try again in 123 seconds.",
  "retryAfter": 123
}
```

---

## Authentication Endpoints

### Login

Authenticate a user and receive a JWT token.

**Endpoint:** `POST /api/login`  
**Auth Required:** No  
**Rate Limit:** 5 requests / 15 minutes

**Request Body:**
```json
{
  "identifier": "student@eng.psu.edu.eg",
  "password": "securePassword123"
}
```

**Parameters:**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| identifier | string | Yes | Email or national ID (14 digits) |
| password | string | Yes | User password (min 8 characters) |

**Success Response:** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "id": 123,
  "email": "student@eng.psu.edu.eg",
  "firstName": "John",
  "lastName": "Doe",
  "role": "STUDENT",
  "status": "APPROVED"
}
```

**Error Responses:**
- `401 Unauthorized` - Invalid credentials
- `429 Too Many Requests` - Rate limit exceeded

**Example:**
```bash
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{
    "identifier": "student@eng.psu.edu.eg",
    "password": "securePassword123"
  }'
```

---

### Signup

Register a new user account.

**Endpoint:** `POST /api/signup`  
**Auth Required:** No  
**Rate Limit:** 3 requests / 60 minutes  
**Content-Type:** `multipart/form-data`

**Form Data:**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| firstName | string | Yes | First name |
| lastName | string | Yes | Last name |
| email | string | Yes | University email (@eng.psu.edu.eg) |
| password | string | Yes | Password (min 8 characters) |
| dateOfBirth | date | Yes | Date of birth (YYYY-MM-DD) |
| nationalId | string | Yes | National ID (14 digits) |
| facultyId | integer | Yes | Faculty ID |
| departmentId | integer | Yes | Department ID |
| year | integer | Yes | Academic year (1-5) |
| nationalIdScan | file | Yes | National ID scan (JPEG/PNG, max 10MB) |

**Success Response:** `201 Created`
```json
{
  "id": 123,
  "email": "student@eng.psu.edu.eg",
  "firstName": "John",
  "lastName": "Doe",
  "status": "PENDING",
  "message": "Registration successful. Awaiting admin approval."
}
```

**Error Responses:**
- `400 Bad Request` - Validation error
- `429 Too Many Requests` - Rate limit exceeded

**Example:**
```bash
curl -X POST http://localhost:8080/api/signup \
  -F "firstName=John" \
  -F "lastName=Doe" \
  -F "email=john.doe@eng.psu.edu.eg" \
  -F "password=securePassword123" \
  -F "dateOfBirth=2000-01-01" \
  -F "nationalId=30001010100123" \
  -F "facultyId=1" \
  -F "departmentId=4" \
  -F "year=3" \
  -F "nationalIdScan=@/path/to/id-scan.jpg"
```

---

## User Endpoints

### Get Current User

Get the authenticated user's information.

**Endpoint:** `GET /api/user/me`  
**Auth Required:** Yes

**Success Response:** `200 OK`
```json
{
  "id": 123,
  "email": "student@eng.psu.edu.eg",
  "firstName": "John",
  "lastName": "Doe",
  "birthDate": "2000-01-01",
  "nationalId": "30001010100123",
  "role": "STUDENT",
  "status": "APPROVED",
  "emailVerified": true,
  "faculty": {
    "id": 1,
    "name": "Faculty of Engineering"
  },
  "department": {
    "id": 4,
    "name": "Computer Science"
  },
  "year": 3
}
```

**Example:**
```bash
curl -X GET http://localhost:8080/api/user/me \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### Get User Profile

Get the authenticated user's profile details.

**Endpoint:** `GET /api/user/profile`  
**Auth Required:** Yes

**Success Response:** `200 OK`
```json
{
  "id": 456,
  "userId": 123,
  "bio": "Computer Science student interested in AI and ML",
  "interests": "Machine Learning, Web Development, Open Source",
  "phone": "+20123456789",
  "linkedin": "https://linkedin.com/in/johndoe",
  "github": "https://github.com/johndoe",
  "profilePhoto": "http://localhost:9000/uploads/123/profile_photo.jpg",
  "visibility": "PUBLIC"
}
```

---

## Profile Endpoints

### Get Profile by User ID

Get a user's profile (visibility rules apply).

**Endpoint:** `GET /api/profile/{userId}`  
**Auth Required:** No (but visibility depends on authentication status)

**URL Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| userId | integer | User ID |

**Success Response:** `200 OK`
```json
{
  "id": 456,
  "user": {
    "id": 123,
    "firstName": "John",
    "lastName": "Doe",
    "email": "student@eng.psu.edu.eg",
    "faculty": "Faculty of Engineering",
    "department": "Computer Science",
    "year": 3
  },
  "bio": "Computer Science student interested in AI and ML",
  "interests": "Machine Learning, Web Development",
  "linkedin": "https://linkedin.com/in/johndoe",
  "github": "https://github.com/johndoe",
  "profilePhoto": "http://localhost:9000/uploads/123/profile_photo.jpg",
  "visibility": "PUBLIC"
}
```

**Visibility Rules:**
- `PUBLIC`: Visible to everyone
- `STUDENTS_ONLY`: Visible only to authenticated students
- `PRIVATE`: Visible only to profile owner and admins

**Error Responses:**
- `403 Forbidden` - Profile is private or students-only
- `404 Not Found` - User not found

---

### Update Profile

Update the authenticated user's profile.

**Endpoint:** `PUT /api/profile`  
**Auth Required:** Yes

**Request Body:**
```json
{
  "bio": "Updated bio",
  "interests": "Updated interests",
  "phone": "+20123456789",
  "linkedin": "https://linkedin.com/in/johndoe",
  "github": "https://github.com/johndoe",
  "visibility": "STUDENTS_ONLY"
}
```

**Parameters:**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| bio | string | No | Profile bio (max 500 characters) |
| interests | string | No | Interests (max 500 characters) |
| phone | string | No | Phone number |
| linkedin | string | No | LinkedIn profile URL |
| github | string | No | GitHub profile URL |
| visibility | string | No | PUBLIC, STUDENTS_ONLY, or PRIVATE |

**Success Response:** `200 OK`
```json
{
  "message": "Profile updated successfully",
  "profile": { ... }
}
```

**Error Responses:**
- `400 Bad Request` - Content moderation violation or validation error
- `401 Unauthorized` - Not authenticated

---

### Upload Profile Photo

Upload or update profile photo.

**Endpoint:** `POST /api/profile/photo`  
**Auth Required:** Yes  
**Content-Type:** `multipart/form-data`

**Form Data:**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| file | file | Yes | Profile photo (JPEG/PNG, max 10MB) |

**Success Response:** `200 OK`
```json
{
  "message": "Profile photo uploaded successfully",
  "photoUrl": "http://localhost:9000/uploads/123/profile_photo.jpg"
}
```

**Error Responses:**
- `400 Bad Request` - Invalid file type or size
- `413 Payload Too Large` - File exceeds 10MB

---

## Admin Endpoints

All admin endpoints require `ROLE_ADMIN`.

### Get Dashboard Statistics

Get dashboard statistics and metrics.

**Endpoint:** `GET /api/admin/dashboard`  
**Auth Required:** Admin

**Success Response:** `200 OK`
```json
{
  "totalUsers": 250,
  "pendingUsers": 15,
  "approvedUsers": 220,
  "rejectedUsers": 15,
  "totalAdmins": 5,
  "facultyDistribution": {
    "Faculty of Engineering": 120,
    "Faculty of Commerce": 80,
    "Faculty of Science": 50
  }
}
```

---

### Get Pending Users

List all users awaiting approval.

**Endpoint:** `GET /api/admin/pending-users`  
**Auth Required:** Admin

**Success Response:** `200 OK`
```json
[
  {
    "id": 123,
    "email": "student@eng.psu.edu.eg",
    "firstName": "John",
    "lastName": "Doe",
    "nationalId": "30001010100123",
    "faculty": "Faculty of Engineering",
    "department": "Computer Science",
    "year": 3,
    "status": "PENDING",
    "emailVerified": false,
    "createdAt": "2025-12-20T10:00:00Z"
  }
]
```

---

### Get User Details

Get detailed information about a specific user.

**Endpoint:** `GET /api/admin/users/{userId}`  
**Auth Required:** Admin

**URL Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| userId | integer | User ID |

**Success Response:** `200 OK`
```json
{
  "user": {
    "id": 123,
    "email": "student@eng.psu.edu.eg",
    "firstName": "John",
    "lastName": "Doe",
    "birthDate": "2000-01-01",
    "nationalId": "30001010100123",
    "nationalIdScan": "http://localhost:9000/uploads/123/national_id.jpg",
    "role": "STUDENT",
    "status": "PENDING",
    "emailVerified": false,
    "faculty": "Faculty of Engineering",
    "department": "Computer Science",
    "year": 3
  },
  "profile": {
    "bio": null,
    "profilePhoto": null,
    "visibility": "PRIVATE"
  }
}
```

---

### Approve User

Approve a pending user registration.

**Endpoint:** `POST /api/admin/users/{userId}/approve`  
**Auth Required:** Admin

**URL Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| userId | integer | User ID |

**Success Response:** `200 OK`
```json
{
  "message": "User approved successfully",
  "user": {
    "id": 123,
    "status": "APPROVED"
  }
}
```

**Error Responses:**
- `400 Bad Request` - Email not verified
- `404 Not Found` - User not found

---

### Reject User

Reject a pending user registration with a reason.

**Endpoint:** `POST /api/admin/users/{userId}/reject`  
**Auth Required:** Admin

**URL Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| userId | integer | User ID |

**Request Body:**
```json
{
  "rejectionReason": "Invalid national ID photo"
}
```

**Success Response:** `200 OK`
```json
{
  "message": "User rejected successfully",
  "user": {
    "id": 123,
    "status": "REJECTED",
    "rejectionReason": "Invalid national ID photo"
  }
}
```

---

### Verify Email

Manually verify a user's email address.

**Endpoint:** `POST /api/admin/users/{userId}/verify-email`  
**Auth Required:** Admin

**URL Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| userId | integer | User ID |

**Success Response:** `200 OK`
```json
{
  "message": "Email verified successfully",
  "user": {
    "id": 123,
    "emailVerified": true
  }
}
```

---

### Promote to Admin

Promote a user to admin role.

**Endpoint:** `POST /api/admin/users/{userId}/promote`  
**Auth Required:** Admin

**Success Response:** `200 OK`
```json
{
  "message": "User promoted to admin",
  "user": {
    "id": 123,
    "role": "ADMIN"
  }
}
```

---

### Demote to Student

Demote an admin to student role.

**Endpoint:** `POST /api/admin/users/{userId}/demote`  
**Auth Required:** Admin

**Success Response:** `200 OK`
```json
{
  "message": "User demoted to student",
  "user": {
    "id": 123,
    "role": "STUDENT"
  }
}
```

---

## Public Endpoints

These endpoints are accessible without authentication.

### Get All Faculties

List all faculties.

**Endpoint:** `GET /api/public/faculties`  
**Auth Required:** No

**Success Response:** `200 OK`
```json
[
  {
    "id": 1,
    "name": "Faculty of Engineering",
    "description": "Engineering programs including Computer Science, Electrical, Mechanical",
    "yearsNumbers": 5
  },
  {
    "id": 2,
    "name": "Faculty of Commerce",
    "description": "Business and commerce programs",
    "yearsNumbers": 4
  }
]
```

---

### Get Departments by Faculty

List all departments in a faculty.

**Endpoint:** `GET /api/public/departments/{facultyId}`  
**Auth Required:** No

**URL Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| facultyId | integer | Faculty ID |

**Success Response:** `200 OK`
```json
[
  {
    "id": 4,
    "name": "Computer Science",
    "description": "Computer Science and Software Engineering",
    "facultyId": 1
  },
  {
    "id": 5,
    "name": "Electrical Engineering",
    "description": "Electrical and Electronics Engineering",
    "facultyId": 1
  }
]
```

---

### Get Public Students

List all students with public profiles.

**Endpoint:** `GET /api/public/students`  
**Auth Required:** No

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| facultyId | integer | No | Filter by faculty |
| search | string | No | Search by name |

**Success Response:** `200 OK`
```json
[
  {
    "id": 123,
    "firstName": "John",
    "lastName": "Doe",
    "email": "student@eng.psu.edu.eg",
    "faculty": "Faculty of Engineering",
    "department": "Computer Science",
    "year": 3,
    "profile": {
      "bio": "Computer Science student",
      "profilePhoto": "http://localhost:9000/uploads/123/profile_photo.jpg",
      "visibility": "PUBLIC"
    }
  }
]
```

**Example:**
```bash
curl "http://localhost:8080/api/public/students?facultyId=1&search=John"
```

---

## Webhooks (Future)

Webhook support is planned for future releases to notify external systems of events.

**Planned Events:**
- User registered
- User approved
- User rejected
- Profile updated

---

## API Versioning

Current API version: **v1**

Future versions will be accessible via URL prefix:
```
/api/v2/...
```

Version 1 will be maintained for 12 months after v2 release.

---

## Best Practices

### Error Handling

Always check HTTP status codes and handle errors appropriately:

```javascript
try {
  const response = await fetch('/api/profile', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message);
  }
  
  const data = await response.json();
  // Handle success
} catch (error) {
  // Handle error
  console.error('API Error:', error.message);
}
```

### Token Refresh

Implement token refresh logic to handle expiration:

```javascript
async function apiCall(endpoint, options) {
  let response = await fetch(endpoint, options);
  
  if (response.status === 401) {
    // Token expired - login again
    await login();
    // Retry with new token
    response = await fetch(endpoint, options);
  }
  
  return response;
}
```

### Rate Limit Handling

Respect rate limits and implement retry logic:

```javascript
async function loginWithRetry(credentials) {
  try {
    return await login(credentials);
  } catch (error) {
    if (error.status === 429) {
      const retryAfter = error.retryAfter || 60;
      await sleep(retryAfter * 1000);
      return await login(credentials);
    }
    throw error;
  }
}
```

---

**Last Updated**: December 24, 2025  
**API Version**: 1.0  
**Maintainer**: Mohamed170408@eng.psu.edu.eg
