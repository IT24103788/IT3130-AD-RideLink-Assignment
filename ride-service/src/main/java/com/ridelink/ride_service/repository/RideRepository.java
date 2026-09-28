package com.ridelink.ride_service.repository;

import com.ridelink.ride_service.model.Ride;
import com.ridelink.ride_service.model.RideStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RideRepository extends MongoRepository<Ride, String> {

    List<Ride> findByPassengerId(String passengerId);

    List<Ride> findByDriverId(String driverId);

    List<Ride> findByStatus(RideStatus status);

    List<Ride> findByPassengerIdAndStatus(String passengerId, RideStatus status);

    List<Ride> findByDriverIdAndStatus(String driverId, RideStatus status);
}