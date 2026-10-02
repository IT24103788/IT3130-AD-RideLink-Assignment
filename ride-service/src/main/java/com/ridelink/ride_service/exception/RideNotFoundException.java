package com.ridelink.ride_service.exception;

public class RideNotFoundException extends RuntimeException {

    public RideNotFoundException(String rideId) {
        super("Ride not found with ID: " + rideId);
    }
}
