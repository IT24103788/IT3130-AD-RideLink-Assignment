# RideLink API Gateway

The **RideLink API Gateway** is the single entry point for client requests in the RideLink microservices system. It receives HTTP requests from clients such as Postman and frontend applications and routes them to the appropriate Spring Boot microservice.

The gateway centralizes request routing, logging, CORS configuration, and error handling while allowing each microservice to manage its own business logic and database.

## Table of Contents

* [Project Overview](#project-overview)
* [Architecture](#architecture)
* [Technologies Used](#technologies-used)
* [Port Configuration](#port-configuration)
* [API Route Mappings](#api-route-mappings)
* [Project Structure](#project-structure)
* [Prerequisites](#prerequisites)
* [Running the Application](#running-the-application)
* [Testing with Postman](#testing-with-postman)
* [Gateway Features](#gateway-features)
* [Troubleshooting](#troubleshooting)
* [Microservices Design](#microservices-design)

## Project Overview

RideLink follows a microservices architecture in which the backend is divided into four independent services:

| Microservice             | Responsibility                                                                |
| ------------------------ | ----------------------------------------------------------------------------- |
| Account Service          | User registration, authentication, profiles, roles, and account management    |
| Driver & Vehicle Service | Driver profiles, vehicle information, availability, and service areas         |
| Ride Management Service  | Ride requests, driver assignment, ride status, cancellation, and ride history |
| Fare & Payment Service   | Fare estimation and payment-related operations                                |

The API Gateway provides a common entry point for these services, so clients do not need to communicate with each microservice directly.

## Architecture

```text
                 Postman / Frontend
                         |
                         v
                +-------------------+
                |   API GATEWAY     |
                |   localhost:8080  |
                +-------------------+
                    |   |   |   |
          +---------+   |   +---------+
          |             |             |
          v             v             v
     Account Svc    Driver Svc    Ride Service
       :8081          :8082          :8083
          |             |             |
       MongoDB       MongoDB       MongoDB

                         |
                         v
                 Fare & Payment Svc
                       :8084
                         |
                       MongoDB
```

Each microservice maintains its own separate MongoDB database. The API Gateway communicates with the services through HTTP/REST and does not directly access their databases.

## Technologies Used

| Technology           | Purpose                                          |
| -------------------- | ------------------------------------------------ |
| Java 21              | Backend programming language                     |
| Spring Boot 3.3.5    | Application framework                            |
| Spring Cloud Gateway | API routing and gateway functionality            |
| Spring WebFlux       | Reactive web infrastructure                      |
| Reactor Netty        | Reactive networking runtime                      |
| Spring Boot Actuator | Health checks and gateway monitoring             |
| Maven                | Dependency management and build automation       |
| MongoDB              | Database used independently by the microservices |

## Port Configuration

| Application              | Port |
| ------------------------ | ---: |
| API Gateway              | 8080 |
| Account Service          | 8081 |
| Driver & Vehicle Service | 8082 |
| Ride Management Service  | 8083 |
| Fare & Payment Service   | 8084 |

## API Route Mappings

The gateway routes requests according to their URL paths.

| Gateway Path       | Destination Service      | Port |
| ------------------ | ------------------------ | ---: |
| `/api/auth/**`     | Account Service          | 8081 |
| `/api/users/**`    | Account Service          | 8081 |
| `/api/drivers/**`  | Driver & Vehicle Service | 8082 |
| `/api/rides/**`    | Ride Management Service  | 8083 |
| `/api/fares/**`    | Fare & Payment Service   | 8084 |
| `/api/payments/**` | Fare & Payment Service   | 8084 |

### Available endpoint examples

**Account Service**

* `POST /api/auth/register`
* `POST /api/auth/login`
* `GET /api/users/me`
* `PUT /api/users/me`
* `PUT /api/users/me/password`
* `GET /api/users`
* `GET /api/users/{id}`
* `PUT /api/users/{id}/status`

**Driver & Vehicle Service**

* `POST /api/drivers`

**Ride Management Service**

* `POST /api/rides`
* `GET /api/rides`
* `GET /api/rides/{id}`
* `GET /api/rides/passenger/{id}`
* `GET /api/rides/driver/{id}`
* `PUT /api/rides/{id}/assign-driver`
* `PUT /api/rides/{id}/status`
* `PUT /api/rides/{id}/cancel`

**Fare & Payment Service**

* `POST /api/fares/estimate`

> **Note:** The `/api/payments/**` route is configured for payment-related endpoints. The availability of functional payment endpoints depends on the Fare & Payment Service implementation. Vehicle-specific endpoints may also be added as the Driver & Vehicle Service is extended.

## Project Structure

```text
ridelink-api-gateway/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/ridelink/gateway/
    │   │       ├── RidelinkApiGatewayApplication.java
    │   │       ├── config/
    │   │       │   └── GatewayErrorHandler.java
    │   │       └── filter/
    │   │           └── RequestLoggingFilter.java
    │   └── resources/
    │       └── application.yml
    └── test/
        └── java/
            └── com/ridelink/gateway/
                └── RidelinkApiGatewayApplicationTests.java
```

## Prerequisites

Before running the API Gateway, ensure the following are available:

* Java Development Kit (JDK) 21
* Maven 3.9+ or the included Maven Wrapper
* The RideLink microservices project
* MongoDB configured for the individual services
* Required application configuration and environment variables

## Running the Application

### Step 1: Start the microservices

Start each microservice in a separate terminal from the integrated project directory.

```cmd
REM Terminal 1 - Account Service
cd account-service
mvnw.cmd spring-boot:run
```

```cmd
REM Terminal 2 - Driver & Vehicle Service
cd driver-service
mvnw.cmd spring-boot:run
```

```cmd
REM Terminal 3 - Ride Management Service
cd ride-service
mvnw.cmd spring-boot:run
```

```cmd
REM Terminal 4 - Fare & Payment Service
cd fare-payment-service
mvnw.cmd spring-boot:run
```

Ensure each service starts successfully on its configured port.

### Step 2: Start the API Gateway

Open another terminal:

```cmd
cd ridelink-api-gateway
mvnw.cmd spring-boot:run
```

The gateway should start on port `8080`, provided that the port is available and the application configuration is correct.

### Step 3: Access the gateway

Use the following base URL for requests:

```text
http://localhost:8080
```

Clients can now access the configured microservice endpoints through the gateway.

## Testing with Postman

### 1. Gateway health check

```http
GET http://localhost:8080/actuator/health
```

Expected response when the application is healthy:

```json
{
  "status": "UP"
}
```

### 2. View configured gateway routes

```http
GET http://localhost:8080/actuator/gateway/routes
```

This endpoint can help inspect the configured gateway routes when the relevant Actuator endpoint is enabled.

### 3. Register a user

```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json
```

Example request body:

```json
{
  "name": "Alice Smith",
  "email": "alice@example.com",
  "password": "Password123!",
  "phone": "+94771234567",
  "role": "CUSTOMER"
}
```

The gateway forwards the request to the Account Service on port `8081`.

### 4. Log in

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json
```

Example request body:

```json
{
  "email": "alice@example.com",
  "password": "Password123!"
}
```

### 5. Create a driver profile

```http
POST http://localhost:8080/api/drivers
Content-Type: application/json
```

Example request body:

```json
{
  "accountId": "6ab4885449dadbf46a7ce771",
  "licenseNumber": "B1234567",
  "serviceArea": "Colombo",
  "currentLatitude": 6.9271,
  "currentLongitude": 79.8612
}
```

### 6. Create a ride

```http
POST http://localhost:8080/api/rides
Content-Type: application/json
```

Example request body:

```json
{
  "passengerId": "user-001",
  "pickupLocation": "Colombo Fort",
  "destination": "Nugegoda",
  "estimatedFare": 850.00
}
```

### 7. Estimate a fare

```http
POST http://localhost:8080/api/fares/estimate
Content-Type: application/json
```

Example request body:

```json
{
  "rideId": "ride-001",
  "distanceKm": 5.0,
  "durationMinutes": 15.0
}
```

The request is forwarded to the Fare & Payment Service on port `8084`.

The request fields and validation requirements should match the DTO implemented in that service.

> These request bodies are examples for testing. Use valid IDs, credentials, roles, and fields that match the current service implementations.

## Gateway Features

### Centralized request routing

Routes incoming client requests to the appropriate microservice based on configured URL patterns.

### Centralized request logging

The request logging filter records incoming requests and relevant response information, helping with debugging and monitoring.

### Centralized CORS configuration

Provides a common place to configure cross-origin access for supported frontend applications.

### Error handling

Provides a centralized mechanism for handling gateway errors, including situations where a downstream service cannot be reached.

### Health monitoring

Spring Boot Actuator can expose health and monitoring endpoints, depending on the application's configuration.

### Gateway response header

Responses routed through the gateway can include the following configured header:

```http
X-Gateway: RideLink-API-Gateway
```

This can help identify responses that have passed through the gateway.

## Troubleshooting

### Port 8080 is already in use

Check which process is using the port:

```cmd
netstat -ano | findstr :8080
```

Terminate the relevant process if appropriate:

```cmd
taskkill /PID <PID> /F
```

Replace `<PID>` with the actual process ID.

### Connection refused or service unavailable

Check that the destination microservice is running on the configured port. Also verify the service URL in the gateway configuration.

### Gateway routes are not matching

Inspect the configured route mappings and verify that the incoming request path matches the relevant route predicate.

### Maven dependency errors

Try resolving the project dependencies:

```cmd
mvnw.cmd dependency:resolve
```

Confirm that the configured Spring Boot and Spring Cloud dependencies are compatible.

### Spring MVC and WebFlux conflicts

Spring Cloud Gateway commonly uses a reactive WebFlux runtime. Avoid introducing incompatible Spring MVC dependencies into the gateway application unless the selected gateway configuration explicitly supports that setup.

## Microservices Design

The RideLink API Gateway follows these design principles:

* **Single entry point:** Clients access backend functionality through a common gateway.
* **Separation of concerns:** Each microservice owns its own business functionality.
* **Database isolation:** Each microservice manages its own database instead of sharing direct database access through the gateway.
* **Independent services:** Microservices can be developed and maintained separately.
* **Centralized cross-cutting concerns:** Common gateway responsibilities can be configured in one place.

The gateway is a routing and request-processing layer. Business operations such as account registration, ride assignment, and fare calculation remain the responsibility of their respective microservices.

---

**RideLink – Microservices-Based Ride-Hailing System**
