package com.ridelink.ride_service.service;

import com.ridelink.ride_service.dto.*;
import com.ridelink.ride_service.exception.RideNotFoundException;
import com.ridelink.ride_service.model.Ride;
import com.ridelink.ride_service.model.RideStatus;
import com.ridelink.ride_service.repository.RideRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RideService {

    private final RideRepository rideRepository;

    public RideService(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    // Create a new ride
    public RideResponse createRide(CreateRideRequest request) {
        Ride ride = Ride.builder()
                .passengerId(request.getPassengerId())
                .pickupLocation(request.getPickupLocation())
                .destination(request.getDestination())
                .estimatedFare(request.getEstimatedFare())
                .status(RideStatus.REQUESTED)
                .paymentStatus("PENDING")
                .requestedAt(LocalDateTime.now())
                .build();

        return RideResponse.fromEntity(rideRepository.save(ride));
    }

    // Get ride by ID
    public RideResponse getRideById(String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException(rideId));
        return RideResponse.fromEntity(ride);
    }

    // Get all rides with optional filtering by status, passengerId, and driverId
    public List<RideResponse> getAllRides(RideStatus status, String passengerId, String driverId) {
        List<Ride> rides;

        if (passengerId != null && !passengerId.isBlank() && status != null) {
            rides = rideRepository.findByPassengerIdAndStatus(passengerId, status);
        } else if (driverId != null && !driverId.isBlank() && status != null) {
            rides = rideRepository.findByDriverIdAndStatus(driverId, status);
        } else if (passengerId != null && !passengerId.isBlank()) {
            rides = rideRepository.findByPassengerId(passengerId);
        } else if (driverId != null && !driverId.isBlank()) {
            rides = rideRepository.findByDriverId(driverId);
        } else if (status != null) {
            rides = rideRepository.findByStatus(status);
        } else {
            rides = rideRepository.findAll();
        }

        return rides.stream().map(RideResponse::fromEntity).collect(Collectors.toList());
    }

    public List<RideResponse> getRidesByPassenger(String passengerId) {
        return rideRepository.findByPassengerId(passengerId)
                .stream()
                .map(RideResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<RideResponse> getRidesByDriver(String driverId) {
        return rideRepository.findByDriverId(driverId)
                .stream()
                .map(RideResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // Assign a driver to a ride (REQUESTED -> ASSIGNED)
    public RideResponse assignDriver(String rideId, AssignDriverRequest request) {
        Ride ride = findRideEntity(rideId);
        if (ride.getStatus() != RideStatus.REQUESTED && ride.getStatus() != RideStatus.ASSIGNED) {
            throw new IllegalArgumentException("Driver can only be assigned to a ride in REQUESTED or ASSIGNED status. Current status: " + ride.getStatus());
        }
        ride.setDriverId(request.getDriverId());
        ride.setStatus(RideStatus.ASSIGNED);
        return RideResponse.fromEntity(rideRepository.save(ride));
    }

    // Reassign a driver to a ride (REQUESTED/ASSIGNED -> ASSIGNED with new driver)
    public RideResponse reassignDriver(String rideId, ReassignDriverRequest request) {
        Ride ride = findRideEntity(rideId);
        if (ride.getStatus() == RideStatus.STARTED || ride.getStatus() == RideStatus.COMPLETED || ride.getStatus() == RideStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot reassign driver for a ride that is " + ride.getStatus());
        }
        ride.setDriverId(request.getNewDriverId());
        ride.setStatus(RideStatus.ASSIGNED);
        return RideResponse.fromEntity(rideRepository.save(ride));
    }

    // Driver accepts the ride (ASSIGNED -> ACCEPTED)
    public RideResponse acceptRide(String rideId, AcceptRideRequest request) {
        Ride ride = findRideEntity(rideId);
        if (ride.getStatus() != RideStatus.ASSIGNED && ride.getStatus() != RideStatus.REQUESTED) {
            throw new IllegalArgumentException("Ride can only be accepted when in ASSIGNED or REQUESTED status. Current: " + ride.getStatus());
        }
        if (request != null && request.getDriverId() != null && !request.getDriverId().isBlank()) {
            ride.setDriverId(request.getDriverId());
        }
        ride.setStatus(RideStatus.ACCEPTED);
        ride.setAcceptedAt(LocalDateTime.now());
        return RideResponse.fromEntity(rideRepository.save(ride));
    }

    // Driver starts the ride (ACCEPTED -> STARTED)
    public RideResponse startRide(String rideId) {
        Ride ride = findRideEntity(rideId);
        if (ride.getStatus() != RideStatus.ACCEPTED && ride.getStatus() != RideStatus.ASSIGNED) {
            throw new IllegalArgumentException("Ride can only be started after being ACCEPTED. Current status: " + ride.getStatus());
        }
        ride.setStatus(RideStatus.STARTED);
        ride.setStartedAt(LocalDateTime.now());
        return RideResponse.fromEntity(rideRepository.save(ride));
    }

    // Driver completes the ride (STARTED -> COMPLETED)
    public RideResponse completeRide(String rideId, CompleteRideRequest request) {
        Ride ride = findRideEntity(rideId);
        if (ride.getStatus() != RideStatus.STARTED) {
            throw new IllegalArgumentException("Ride can only be completed when in STARTED status. Current status: " + ride.getStatus());
        }
        ride.setStatus(RideStatus.COMPLETED);
        ride.setFinalFare(request.getFinalFare());
        if (request.getActualDistanceKm() != null) {
            ride.setDistanceKm(request.getActualDistanceKm());
        }
        ride.setCompletedAt(LocalDateTime.now());
        return RideResponse.fromEntity(rideRepository.save(ride));
    }

    // Cancel a ride (can cancel anytime before COMPLETED)
    public RideResponse cancelRide(String rideId, CancelRideRequest request) {
        Ride ride = findRideEntity(rideId);
        if (ride.getStatus() == RideStatus.COMPLETED || ride.getStatus() == RideStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot cancel a ride that is already " + ride.getStatus());
        }
        ride.setStatus(RideStatus.CANCELLED);
        if (request != null && request.getReason() != null) {
            ride.setCancellationReason(request.getReason());
        }
        ride.setCancelledAt(LocalDateTime.now());
        return RideResponse.fromEntity(rideRepository.save(ride));
    }

    // Legacy cancel method for backwards compatibility
    public RideResponse cancelRide(String rideId) {
        return cancelRide(rideId, null);
    }

    // Generic status update method
    public RideResponse updateRideStatus(String rideId, UpdateRideStatusRequest request) {
        Ride ride = findRideEntity(rideId);
        ride.setStatus(request.getStatus());

        if (request.getStatus() == RideStatus.ACCEPTED && ride.getAcceptedAt() == null) {
            ride.setAcceptedAt(LocalDateTime.now());
        } else if (request.getStatus() == RideStatus.STARTED && ride.getStartedAt() == null) {
            ride.setStartedAt(LocalDateTime.now());
        } else if (request.getStatus() == RideStatus.COMPLETED) {
            ride.setCompletedAt(LocalDateTime.now());
            if (request.getFinalFare() != null) {
                ride.setFinalFare(request.getFinalFare());
            }
        } else if (request.getStatus() == RideStatus.CANCELLED && ride.getCancelledAt() == null) {
            ride.setCancelledAt(LocalDateTime.now());
        }

        return RideResponse.fromEntity(rideRepository.save(ride));
    }

    private Ride findRideEntity(String rideId) {
        return rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException(rideId));
    }
}
