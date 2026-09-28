# 🚗 RideLink — Account Service

The **Account Service** handles all user identity, authentication, and profile management for the RideLink platform. It issues JWT tokens used by other services to verify user identity and roles.

---

## 🛠️ Tech Stack

| Technology | Details |
|---|---|
| **Framework** | Spring Boot 4.1.1 |
| **Language** | Java 21 |
| **Database** | MongoDB |
| **Security** | Spring Security + JWT (JJWT 0.12.3) |
| **Validation** | Jakarta Bean Validation |
| **API Docs** | SpringDoc OpenAPI (Swagger UI) |

---

## ⚙️ Setup & Running

### Prerequisites
- Java 21+
- Maven 3.8+
- MongoDB (local or Atlas)

### Configuration

Set the following environment variable before running:

```bash
MONGODB_URI=mongodb://localhost:27017
```

Or update `src/main/resources/application.properties`:

```properties
spring.application.name=account-service
server.port=8081

spring.data.mongodb.uri=${MONGODB_URI}
spring.data.mongodb.database=ridelink_accounts

springdoc.swagger-ui.path=/swagger-ui.html
```

### Run

```bash
mvn spring-boot:run
```

The service starts on **http://localhost:8081**

---

## 📖 API Documentation

Interactive Swagger UI: **http://localhost:8081/swagger-ui.html**  
Raw OpenAPI JSON: **http://localhost:8081/v3/api-docs**

---

## 📋 REST API Reference

### 🔓 Auth Endpoints — `/api/auth`
> Public — No token required

---

#### `POST /api/auth/register`
Register a new user account.

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "0771234567",
  "password": "secret123",
  "role": "CUSTOMER"
}
```

| Field | Type | Required | Notes |
|---|---|---|---|
| `name` | String | ✅ | Full name |
| `email` | String | ✅ | Must be a valid email, unique |
| `phone` | String | ✅ | Must be unique |
| `password` | String | ✅ | Minimum 6 characters |
| `role` | Enum | ✅ | `CUSTOMER`, `DRIVER`, or `ADMIN` |

**Response `200 OK`:**
```json
"User registered successfully"
```

**Response `400 Bad Request` (duplicate email):**
```json
{
  "timestamp": "2026-09-22T17:00:00",
  "status": 400,
  "error": "Email is already registered"
}
```

---

#### `POST /api/auth/login`
Authenticate a user and receive a JWT token.

**Request Body:**
```json
{
  "identifier": "john@example.com",
  "password": "secret123"
}
```

> `identifier` can be either **email** or **phone number**.

**Response `200 OK`:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": "64abc123def456",
    "name": "John Doe",
    "email": "john@example.com",
    "phone": "0771234567",
    "profileImage": null,
    "role": "CUSTOMER",
    "status": "ACTIVE"
  }
}
```

**Response `400 Bad Request`:**
```json
{
  "timestamp": "2026-09-22T17:00:00",
  "status": 400,
  "error": "Invalid credentials"
}
```

---

### 🔐 User Endpoints — `/api/users`
> Protected — Requires `Authorization: Bearer <token>` header

---

#### `GET /api/users/me`
Get the currently authenticated user's profile.

**Headers:**
```
Authorization: Bearer <token>
```

**Response `200 OK`:**
```json
{
  "id": "64abc123def456",
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "0771234567",
  "profileImage": null,
  "role": "CUSTOMER",
  "status": "ACTIVE"
}
```

---

#### `PUT /api/users/me`
Update the authenticated user's profile.

**Headers:**
```
Authorization: Bearer <token>
```

**Request Body** *(all fields optional)*:
```json
{
  "name": "John Updated",
  "phone": "0779998877",
  "profileImage": "https://example.com/photo.jpg"
}
```

**Response `200 OK`:** Updated user object (same as `GET /api/users/me`)

---

#### `PUT /api/users/me/password`
Change the authenticated user's password.

**Headers:**
```
Authorization: Bearer <token>
```

**Request Body:**
```json
{
  "oldPassword": "secret123",
  "newPassword": "newSecret456"
}
```

| Field | Type | Required | Notes |
|---|---|---|---|
| `oldPassword` | String | ✅ | Current password |
| `newPassword` | String | ✅ | Minimum 6 characters |

**Response `200 OK`:**
```json
"Password changed successfully"
```

---

#### `GET /api/users` 🔒 Admin Only
List all registered users.

**Headers:**
```
Authorization: Bearer <admin_token>
```

**Response `200 OK`:** Array of user objects

---

#### `GET /api/users/{id}` 🔒 Admin Only
Get a specific user by their ID.

**Headers:**
```
Authorization: Bearer <admin_token>
```

**Path Parameter:**

| Param | Description |
|---|---|
| `id` | MongoDB document ID of the user |

**Response `200 OK`:** Single user object

---

#### `PUT /api/users/{id}/status?status=INACTIVE` 🔒 Admin Only
Activate or deactivate a user account.

**Headers:**
```
Authorization: Bearer <admin_token>
```

**Path Parameter:**

| Param | Description |
|---|---|
| `id` | MongoDB document ID of the user |

**Query Parameter:**

| Param | Values |
|---|---|
| `status` | `ACTIVE` or `INACTIVE` |

**Response `200 OK`:**
```json
"User status updated to INACTIVE"
```

> ⚠️ Deactivated users will be blocked from logging in.

---

## ⚠️ Error Responses

All errors return a consistent JSON structure:

```json
{
  "timestamp": "2026-09-22T17:00:00",
  "status": 400,
  "error": "Description of the error"
}
```

| HTTP Status | When |
|---|---|
| `400` | Validation failure, duplicate email/phone, wrong password, etc. |
| `401` | Missing, expired, or invalid JWT token |
| `403` | Valid token but insufficient role (e.g. non-admin accessing admin routes) |

---

## 🧪 Quick Test with Postman

1. **Register** → `POST /api/auth/register`
2. **Login** → `POST /api/auth/login` → copy the `token` from the response
3. In Postman, set header: `Authorization: Bearer <paste_token_here>`
4. **Get profile** → `GET /api/users/me`
5. **Update profile** → `PUT /api/users/me`

---

## 🗂️ Project Structure

```
account-service/
├── src/main/java/com/ridelink/account_service/
│   ├── config/
│   │   └── SecurityConfig.java          # Spring Security + JWT filter setup
│   ├── controller/
│   │   ├── AuthController.java          # /api/auth endpoints
│   │   └── UserController.java          # /api/users endpoints
│   ├── dto/
│   │   ├── RegisterRequest.java
│   │   ├── LoginRequest.java
│   │   ├── AuthResponse.java
│   │   ├── UserResponse.java
│   │   ├── UpdateProfileRequest.java
│   │   └── ChangePasswordRequest.java
│   ├── exception/
│   │   └── GlobalExceptionHandler.java  # Centralised error handling
│   ├── model/
│   │   ├── User.java
│   │   ├── Role.java                    # CUSTOMER, DRIVER, ADMIN
│   │   └── AccountStatus.java           # ACTIVE, INACTIVE
│   ├── repository/
│   │   └── UserRepository.java
│   ├── security/
│   │   ├── JwtUtil.java                 # Token generation & validation
│   │   └── JwtAuthenticationFilter.java # Per-request JWT validation
│   └── service/
│       ├── AuthService.java             # Registration & login logic
│       └── UserService.java             # Profile management logic
└── src/main/resources/
    └── application.properties
```

---

## 🔑 Roles Reference

| Role | Access |
|---|---|
| `CUSTOMER` | Register, login, manage own profile |
| `DRIVER` | Register, login, manage own profile |
| `ADMIN` | All of the above + manage all users, change account status |
