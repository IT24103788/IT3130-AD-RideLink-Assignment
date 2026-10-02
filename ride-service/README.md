# 🛣️ RideLink — Ride Management Service

The **Ride Management Service** is the central orchestrator for the entire lifecycle of a ride in the RideLink platform. It handles ride requests, driver assignment & reassignment, ride acceptance, trip commencement, ride completion with final fare recording, and cancellation.

---

## 📌 Service Overview

| Property | Details |
|---|---|
| **Service Name** | `ride-service` |
| **Port** | `8083` |
| **Gateway Port** | `8080` (routes `/api/rides/**`) |
| **Framework** | Spring Boot 4.1.x / Java 21 |
| **Database** | MongoDB (Database: `ridelink_ride_db`) |
| **Architecture** | Controller ➔ Service ➔ Repository ➔ MongoDB |
| **Interactive Docs** | Swagger UI: `http://localhost:8083/swagger-ui.html` |
| **OpenAPI JSON** | `http://localhost:8083/v3/api-docs` |

---

## 🔄 Ride Lifecycle State Machine

```
   [REQUESTED] ──────────(assignDriver)──────────► [ASSIGNED]
        │                                             │
        │                                      (reassignDriver)
        │                                             │
        ▼                                             ▼
   (cancelRide) ◄────────────────────────────── (acceptRide)
        │                                             │
        │                                             ▼
        │                                         [ACCEPTED]
        │                                             │
        │                                        (startRide)
        │                                             │
        │                                             ▼
        ▼                                         [STARTED]
   [CANCELLED]                                        │
                                                (completeRide)
                                                      │
                                                      ▼
                                                 [COMPLETED]
```

---

## 📊 Domain Model (`rides` Collection)

| Field | Type | Description |
|---|---|---|
| `id` | `String` | MongoDB Document ID (Primary Key) |
| `passengerId` | `String` | Reference ID to passenger user in `account-service` |
| `driverId` | `String` | Reference ID to assigned driver in `driver-service` |
| `pickupLocation` | `String` | Starting location / address |
| `destination` | `String` | Drop-off destination address |
| `status` | `RideStatus` | Enum: `REQUESTED`, `ASSIGNED`, `ACCEPTED`, `STARTED`, `COMPLETED`, `CANCELLED` |
| `estimatedFare` | `Double` | Initial estimated fare (LKR) |
| `finalFare` | `Double` | Actual calculated final fare on completion (LKR) |
| `distanceKm` | `Double` | Actual distance traveled |
| `cancellationReason`| `String` | Reason why the ride was cancelled |
| `paymentStatus` | `String` | `PENDING`, `COMPLETED` |
| `requestedAt` | `LocalDateTime` | Ride creation timestamp |
| `acceptedAt` | `LocalDateTime` | Driver acceptance timestamp |
| `startedAt` | `LocalDateTime` | Trip start timestamp |
| `completedAt` | `LocalDateTime` | Trip completion timestamp |
| `cancelledAt` | `LocalDateTime` | Ride cancellation timestamp |

---

## 📡 Complete REST API Endpoints

All endpoints are accessible via the **API Gateway (`http://localhost:8080`)** or directly on **Ride Service (`http://localhost:8083`)**.

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/rides` | Create a new ride request (status = `REQUESTED`) |
| `GET` | `/api/rides` | List all rides (supports filter query params: `?status=...&passengerId=...&driverId=...`) |
| `GET` | `/api/rides/{id}` | Get ride details by ID |
| `GET` | `/api/rides/passenger/{passengerId}` | Get all rides requested by a passenger |
| `GET` | `/api/rides/driver/{driverId}` | Get all rides assigned to a driver |
| `PUT` | `/api/rides/{id}/assign-driver` | Assign an available driver (`REQUESTED` ➔ `ASSIGNED`) |
| `PUT` | `/api/rides/{id}/reassign-driver` | Reassign ride to another driver |
| `PUT` | `/api/rides/{id}/accept` | Driver accepts the assigned ride (`ASSIGNED` ➔ `ACCEPTED`) |
| `PUT` | `/api/rides/{id}/start` | Driver starts the trip (`ACCEPTED` ➔ `STARTED`) |
| `PUT` | `/api/rides/{id}/complete` | Driver completes the trip (`STARTED` ➔ `COMPLETED` + records `finalFare`) |
| `PUT` | `/api/rides/{id}/cancel` | Cancel a ride (anytime before `COMPLETED`) |
| `PUT` | `/api/rides/{id}/status` | Generic status update |

---

## 🧪 Postman Step-by-Step Testing Walkthrough

### 1. Create a Ride Request
```http
POST http://localhost:8080/api/rides
Content-Type: application/json

{
  "passengerId": "6ab4885449dadbf46a7ce771",
  "pickupLocation": "Colombo Fort Station",
  "destination": "Nugegoda Supermarket",
  "estimatedFare": 850.00
}
```
**Response (`201 Created`):**
```json
{
  "id": "6ab5000149dadbf46a7ce800",
  "passengerId": "6ab4885449dadbf46a7ce771",
  "driverId": null,
  "pickupLocation": "Colombo Fort Station",
  "destination": "Nugegoda Supermarket",
  "status": "REQUESTED",
  "estimatedFare": 850.0,
  "finalFare": null,
  "paymentStatus": "PENDING",
  "requestedAt": "2026-09-24T16:00:00"
}
```

---

### 2. Assign Driver to Ride
```http
PUT http://localhost:8080/api/rides/6ab5000149dadbf46a7ce800/assign-driver
Content-Type: application/json

{
  "driverId": "6ab4900149dadbf46a7ce780"
}
```
**Response (`200 OK`):**
```json
{
  "id": "6ab5000149dadbf46a7ce800",
  "driverId": "6ab4900149dadbf46a7ce780",
  "status": "ASSIGNED"
}
```

---

### 3. Optional: Reassign to Another Driver
```http
PUT http://localhost:8080/api/rides/6ab5000149dadbf46a7ce800/reassign-driver
Content-Type: application/json

{
  "newDriverId": "6ab4900149dadbf46a7ce999",
  "reason": "Previous driver was delayed"
}
```

---

### 4. Driver Accepts Ride
```http
PUT http://localhost:8080/api/rides/6ab5000149dadbf46a7ce800/accept
Content-Type: application/json

{
  "driverId": "6ab4900149dadbf46a7ce780"
}
```
**Response (`200 OK`):**
```json
{
  "id": "6ab5000149dadbf46a7ce800",
  "status": "ACCEPTED",
  "acceptedAt": "2026-09-24T16:05:00"
}
```

---

### 5. Driver Starts the Trip
```http
PUT http://localhost:8080/api/rides/6ab5000149dadbf46a7ce800/start
```
**Response (`200 OK`):**
```json
{
  "id": "6ab5000149dadbf46a7ce800",
  "status": "STARTED",
  "startedAt": "2026-09-24T16:10:00"
}
```

---

### 6. Driver Completes the Trip
```http
PUT http://localhost:8080/api/rides/6ab5000149dadbf46a7ce800/complete
Content-Type: application/json

{
  "finalFare": 920.00,
  "actualDistanceKm": 8.5
}
```
**Response (`200 OK`):**
```json
{
  "id": "6ab5000149dadbf46a7ce800",
  "status": "COMPLETED",
  "finalFare": 920.0,
  "distanceKm": 8.5,
  "completedAt": "2026-09-24T16:35:00"
}
```

---

### 7. Filter Rides (by Status / Passenger / Driver)
```http
GET http://localhost:8080/api/rides?status=COMPLETED&passengerId=6ab4885449dadbf46a7ce771
```

---

### 8. Cancel a Ride
```http
PUT http://localhost:8080/api/rides/6ab5000149dadbf46a7ce800/cancel
Content-Type: application/json

{
  "reason": "Passenger changed their mind",
  "cancelledBy": "PASSENGER"
}
```

---

## 💡 Viva Q&A Reference for IT3130

**Q1: How does the Ride Service handle state validation?**  
> **A:** The service layer implements strict state transition guards. For example, a ride cannot be started unless it is in `ACCEPTED` status, and cannot be completed unless it is in `STARTED` status. Invalid transitions throw an `IllegalArgumentException` mapped to HTTP `400 Bad Request`.

**Q2: How does Ride Service maintain data isolation?**  
> **A:** It maintains its own dedicated MongoDB database `ridelink_ride_db` and only references string IDs (`passengerId`, `driverId`) from other microservices, adhering strictly to microservice decoupling principles.
