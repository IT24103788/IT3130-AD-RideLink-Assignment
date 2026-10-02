package com.ridelink.ride_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "rides")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ride {

    @Id
    private String id;

    private String passengerId;
    private String driverId;
    private String pickupLocation;
    private String destination;
    private RideStatus status;
    private Double estimatedFare;
    private Double finalFare;
    private Double distanceKm;
    private String cancellationReason;
    private String paymentStatus;

    private LocalDateTime requestedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
}