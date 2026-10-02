package com.ridelink.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * RideLink API Gateway — Entry Point
 *
 * <p>This application is the single entry point for all HTTP traffic to the
 * RideLink system. It does NOT contain any business logic, database access,
 * or domain models.
 *
 * <p>Responsibility: Route incoming HTTP requests to the correct downstream
 * microservice based on the URL path.
 *
 * <pre>
 *   /api/auth/**     → Account Service  (port 8081)
 *   /api/users/**    → Account Service  (port 8081)
 *   /api/drivers/**  → Driver Service   (port 8082)
 *   /api/rides/**    → Ride Service     (port 8083)
 *   /api/fares/**    → Fare &amp; Payment  (port 8084)
 *   /api/payments/** → Fare &amp; Payment  (port 8084)
 * </pre>
 *
 * <p>All routing is configured declaratively in application.yml — no Java
 * route configuration classes are needed for this project's requirements.
 */
@SpringBootApplication
public class RidelinkApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(RidelinkApiGatewayApplication.class, args);
    }
}
