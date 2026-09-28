package com.ridelink.ride_service.service;

import com.ridelink.ride_service.dto.AssignDriverRequest;
import com.ridelink.ride_service.dto.CreateRideRequest;
import com.ridelink.ride_service.dto.UpdateRideStatusRequest;
import com.ridelink.ride_service.exception.RideNotFoundException;
import com.ridelink.ride_service.model.Ride;
import com.ridelink.ride_service.model.RideStatus;
import com.ridelink.ride_service.repository.RideRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RideService {

    private final RideRepository rideRepository;

    public RideService(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    // Create a new ride
    public Ride createRide(CreateRideRequest request) {
        Ride ride = new Ride();
        ride.setPassengerId(request.getPassengerId());
        ride.setPickupLocation(request.getPickupLocation());
        ride.setDestination(request.getDestination());
        ride.setEstimatedFare(request.getEstimatedFare());
        ride.setStatus(RideStatus.REQUESTED);
        return rideRepository.save(ride);
    }

    // Get ride by ID
    public Ride getRideById(String rideId) {
        return rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException(rideId));
    }

    // Get all rides
    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    // Get rides by passenger
    public List<Ride> getRidesByPassenger(String passengerId) {
        return rideRepository.findByPassengerId(passengerId);
    }

    // Get rides by driver
    public List<Ride> getRidesByDriver(String driverId) {
        return rideRepository.findByDriverId(driverId);
    }

    // Assign a driver to a ride
    public Ride assignDriver(String rideId, AssignDriverRequest request) {
        Ride ride = getRideById(rideId);
        if (ride.getStatus() != RideStatus.REQUESTED) {
            throw new IllegalArgumentException("Driver can only be assigned to a ride in REQUESTED status. Current status: " + ride.getStatus());
        }
        ride.setDriverId(request.getDriverId());
        ride.setStatus(RideStatus.ASSIGNED);
        return rideRepository.save(ride);
    }

    // Update ride status
    public Ride updateRideStatus(String rideId, UpdateRideStatusRequest request) {
        Ride ride = getRideById(rideId);
        ride.setStatus(request.getStatus());
        if (request.getStatus() == RideStatus.COMPLETED && request.getFinalFare() != null) {
            ride.setFinalFare(request.getFinalFare());
        }
        return rideRepository.save(ride);
    }

    // Cancel a ride
    public Ride cancelRide(String rideId) {
        Ride ride = getRideById(rideId);
        if (ride.getStatus() == RideStatus.COMPLETED || ride.getStatus() == RideStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot cancel a ride that is already " + ride.getStatus());
        }
        ride.setStatus(RideStatus.CANCELLED);
        return rideRepository.save(ride);
    }
}
