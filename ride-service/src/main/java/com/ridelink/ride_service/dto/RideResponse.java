package com.ridelink.ride_service.dto;

import com.ridelink.ride_service.model.Ride;
import com.ridelink.ride_service.model.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideResponse {

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

    public static RideResponse fromEntity(Ride ride) {
        if (ride == null) return null;
        return RideResponse.builder()
                .id(ride.getId())
                .passengerId(ride.getPassengerId())
                .driverId(ride.getDriverId())
                .pickupLocation(ride.getPickupLocation())
                .destination(ride.getDestination())
                .status(ride.getStatus())
                .estimatedFare(ride.getEstimatedFare())
                .finalFare(ride.getFinalFare())
                .distanceKm(ride.getDistanceKm())
                .cancellationReason(ride.getCancellationReason())
                .paymentStatus(ride.getPaymentStatus())
                .requestedAt(ride.getRequestedAt())
                .acceptedAt(ride.getAcceptedAt())
                .startedAt(ride.getStartedAt())
                .completedAt(ride.getCompletedAt())
                .cancelledAt(ride.getCancelledAt())
                .build();
    }
}
