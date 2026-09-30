




































































































































































































































































































































































# RideLink API Gateway

## What Is an API Gateway?

An API Gateway is a **single entry point** for all clients accessing a microservices system. Instead of each client needing to know the port and address of every individual service, the client talks to the gateway only. The gateway inspects the request URL and forwards (routes) the request to the correct downstream service.

> Think of it like a hotel reception desk — guests don't walk directly into the kitchen or the laundry room. They go to reception, which directs them to the right place.

---

## Why Does RideLink Use One?

Without a gateway, a Postman user or frontend would need to know:
- Account Service runs on `:8081`
- Driver Service runs on `:8082`
- Ride Service runs on `:8083`
- Fare & Payment Service runs on `:8084`

With the gateway, everything goes through `:8080`. The gateway handles routing internally.

Additional benefits:
- **Single port to expose** during demonstration
- **Centralised logging** — every request is logged in one place
- **Centralised CORS** — one place to configure cross-origin settings
- **Error handling** — one place to handle service-unavailable errors

---

## Architecture

```
  Postman / Swagger
        |
        v
+-------------------+
|   API GATEWAY     |
|   :8080           |
+-------------------+
  |      |     |     |
  v      v     v     v
:8081  :8082 :8083 :8084
Account Driver  Ride  Fare &
 Svc    Svc    Svc   Payment
  |      |      |       |
 [DB]  [DB]   [DB]    [DB]
```

Each service has its **own separate MongoDB database**. The gateway does **not** connect to any database.

---

## Port Numbers

| Application        | Port |
|--------------------|------|
| API Gateway        | 8080 |
| Account Service    | 8081 |
| Driver Service     | 8082 |
| Ride Service       | 8083 |
| Fare/Payment Svc   | 8084 |

---

## Route Mappings

Routes were verified by inspecting the `@RequestMapping` annotations in each service's controller source code.

| Gateway Path          | Downstream Service        | Port | Verified Endpoints |
|-----------------------|---------------------------|------|--------------------|
| `/api/auth/**`        | Account Service           | 8081 | POST /api/auth/register, POST /api/auth/login |
| `/api/users/**`       | Account Service           | 8081 | GET /api/users/me, PUT /api/users/me, PUT /api/users/me/password, GET /api/users, GET /api/users/{id}, PUT /api/users/{id}/status |
| `/api/drivers/**`     | Driver & Vehicle Service  | 8082 | POST /api/drivers |
| `/api/rides/**`       | Ride Management Service   | 8083 | POST /api/rides, GET /api/rides, GET /api/rides/{id}, GET /api/rides/passenger/{id}, GET /api/rides/driver/{id}, PUT /api/rides/{id}/assign-driver, PUT /api/rides/{id}/status, PUT /api/rides/{id}/cancel |
| `/api/fares/**`       | Fare & Payment Service    | 8084 | POST /api/fares/estimate |
| `/api/payments/**`    | Fare & Payment Service    | 8084 | *(ready for future endpoints)* |

> **Note on `/api/vehicles/**`:** No `/api/vehicles` controller was found in `driver-service` at time of writing. When it is added, the gateway's `/api/drivers/**` route can be extended or a new route added — no change is needed as long as vehicle endpoints are also placed under `/api/drivers`.

---

## Why the Gateway Does NOT Access MongoDB Directly

Each microservice owns its own database. The gateway is a **routing layer only**. Giving the gateway direct database access would:

1. Break the microservices principle of data isolation
2. Introduce hidden coupling between the gateway and service schemas
3. Put business logic into a layer that should have none

The gateway communicates with services through **HTTP/REST only** — the same protocol a Postman client uses.

---

## Prerequisites

- Java 21 (same as the other four services)
- Maven 3.9+ (or use the included `mvnw` wrapper)
- The four microservices running on their respective ports

---

## How to Run

### Step 1: Start the four microservices first

Open four separate terminals:

```cmd
REM Terminal 1 — Account Service
cd IT3130-RideLink-integrated\account-service
mvnw.cmd spring-boot:run

REM Terminal 2 — Driver Service
cd IT3130-RideLink-integrated\driver-service
mvnw.cmd spring-boot:run

REM Terminal 3 — Ride Service
cd IT3130-RideLink-integrated\ride-service
mvnw.cmd spring-boot:run

REM Terminal 4 — Fare & Payment Service
cd IT3130-RideLink-integrated\fare-payment-service
mvnw.cmd spring-boot:run
```

### Step 2: Start the API Gateway

```cmd
REM Terminal 5 — API Gateway
cd IT3130-RideLink-integrated\ridelink-api-gateway
mvnw.cmd spring-boot:run
```

The gateway starts on port 8080. You will see log output like:
```
[GATEWAY >>>] POST /api/fares/estimate — from /127.0.0.1:XXXXX
[GATEWAY <<<] POST /api/fares/estimate — status: 201 CREATED — 45ms
```

### Optional: Override service URLs using environment variables

```cmd
set ACCOUNT_SERVICE_URL=http://192.168.1.10:8081
set DRIVER_SERVICE_URL=http://192.168.1.11:8082
mvnw.cmd spring-boot:run
```

---

## How to Test with Postman

### 1. Gateway Health Check

```
GET http://localhost:8080/actuator/health
```
Expected response:
```json
{
  "status": "UP"
}
```

### 2. View All Routes (useful for debugging)

```
GET http://localhost:8080/actuator/gateway/routes
```
Returns a JSON array of all configured routes with their predicates and URIs.

---

### 3. Account Service — Register a User

**Through gateway (port 8080):**
```
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "name": "Alice Smith",
  "email": "alice@example.com",
  "password": "Password123!",
  "phone": "+94771234567",
  "role": "CUSTOMER"
}
```

Gateway forwards to → `http://localhost:8081/api/auth/register`

---

### 4. Account Service — Login

```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "alice@example.com",
  "password": "Password123!"
}
```

Gateway forwards to → `http://localhost:8081/api/auth/login`

---

### 5. Driver Service — Full API Suite

#### 5.1 Create Driver Profile
```http
POST http://localhost:8080/api/drivers
Content-Type: application/json

{
  "accountId": "6ab4885449dadbf46a7ce771",
  "licenseNumber": "B1234567",
  "serviceArea": "Colombo",
  "currentLatitude": 6.9271,
  "currentLongitude": 79.8612
}
```

#### 5.2 Update Driver Availability (AVAILABLE, UNAVAILABLE, ON_RIDE)
```http
PUT http://localhost:8080/api/drivers/{driverId}/availability
Content-Type: application/json

{
  "availability": "AVAILABLE"
}
```

#### 5.3 Update Driver GPS Location
```http
PUT http://localhost:8080/api/drivers/{driverId}/location
Content-Type: application/json

{
  "latitude": 6.9319,
  "longitude": 79.8478
}
```

#### 5.4 Find Eligible / Nearby Available Drivers (Dispatch)
```http
GET http://localhost:8080/api/drivers/eligible?latitude=6.9271&longitude=79.8612&radiusKm=10.0&vehicleType=CAR
```

#### 5.5 Register a Vehicle
```http
POST http://localhost:8080/api/vehicles
Content-Type: application/json

{
  "driverId": "{driverId}",
  "make": "Toyota",
  "model": "Prius",
  "year": 2021,
  "color": "Pearl White",
  "licensePlate": "WP CAB-1234",
  "vehicleType": "CAR",
  "capacity": 4
}
```

#### 5.6 Get Vehicles for Driver
```http
GET http://localhost:8080/api/vehicles/driver/{driverId}
```

Gateway forwards `/api/drivers/**` and `/api/vehicles/**` to → `http://localhost:8082`

---

### 6. Ride Service — Create a Ride

```
POST http://localhost:8080/api/rides
Content-Type: application/json

{
  "passengerId": "user-001",
  "pickupLocation": "Colombo Fort",
  "destination": "Nugegoda",
  "estimatedFare": 850.00
}
```

Gateway forwards to → `http://localhost:8083/api/rides`

---

### 7. Fare & Payment Service — Estimate a Fare ⭐

```
POST http://localhost:8080/api/fares/estimate
Content-Type: application/json

{
  "rideId": "ride-001",
  "distanceKm": 5.0,
  "durationMinutes": 15.0
}
```

Gateway forwards to → `http://localhost:8084/api/fares/estimate`

Required fields (from `FareEstimateRequest.java`):
| Field | Type | Validation |
|-------|------|------------|
| `rideId` | String | Required, not blank |
| `distanceKm` | Double | Required, min 0.1 |
| `durationMinutes` | Double | Required, min 1.0 |

---

### 8. Negative Test — Service Unavailable

Stop the Fare & Payment Service (close terminal 4), then send:

```
POST http://localhost:8080/api/fares/estimate
Content-Type: application/json

{
  "rideId": "ride-001",
  "distanceKm": 5.0,
  "durationMinutes": 15.0
}
```

Expected response (503):
```json
{
  "error": "Service Unavailable",
  "status": 503,
  "message": "Service is currently unavailable. Please try again later.",
  "path": "/api/fares/estimate"
}
```

---

### 9. Negative Test — Invalid Route

```
GET http://localhost:8080/api/unknown-endpoint
```

Expected response (404):
```json
{
  "error": "Not Found",
  "status": 404,
  "message": "No route found for this path. Check the API documentation.",
  "path": "/api/unknown-endpoint"
}
```

---

## Response Header Added by Gateway

Every response routed through the gateway includes:
```
X-Gateway: RideLink-API-Gateway
```
You can see this in Postman's response headers tab. It confirms the request went through the gateway, not directly to the service.

---

## Common Errors and Troubleshooting

### Port 8080 already in use

```
Web server failed to start. Port 8080 was already in use.
```

**Fix:** Kill the process using port 8080:
```cmd
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Connection refused when calling downstream service

The gateway returns `503 Service Unavailable`. This means the downstream service is not running. Start it first.

### Cannot find class `spring-cloud-starter-gateway`

Make sure your Maven cache is populated:
```cmd
mvnw.cmd dependency:resolve
```

### `spring-boot-starter-web` conflict

Spring Cloud Gateway uses **WebFlux (reactive)**. Do NOT add `spring-boot-starter-web` to the pom.xml — it will cause a conflict. The current pom.xml is correct.

### Routes not matching

Check configured routes:
```
GET http://localhost:8080/actuator/gateway/routes
```

---

## Project Structure

```
ridelink-api-gateway/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/ridelink/gateway/
    │   │   ├── RidelinkApiGatewayApplication.java   ← Entry point
    │   │   ├── config/
    │   │   │   └── GatewayErrorHandler.java          ← Custom error responses
    │   │   └── filter/
    │   │       └── RequestLoggingFilter.java          ← Logs every request
    │   └── resources/
    │       └── application.yml                       ← All route configuration
    └── test/
        └── java/com/ridelink/gateway/
            └── RidelinkApiGatewayApplicationTests.java
```

---

## Technology Choices (Viva Explanation)

| Technology | Why |
|---|---|
| Spring Boot 3.3.5 | Latest stable release; compatible with Spring Cloud 2023.x |
| Spring Cloud Gateway | Official Spring reactive API gateway; production-grade |
| Spring WebFlux / Reactor Netty | Gateway is reactive; non-blocking HTTP proxy |
| Spring Boot Actuator | Monitoring and health endpoints without writing code |
| Maven | Same build tool used by all four microservices |
| Java 21 | Same JDK used by all four microservices |

**Why NOT Node.js/Express?** — The assignment requires Java. The gateway is a Java application, consistent with the four microservices.

**Why no MongoDB in the gateway?** — The gateway's only job is routing. Putting a database in the gateway would violate the single responsibility principle and break the data isolation requirement of the microservices architecture.
