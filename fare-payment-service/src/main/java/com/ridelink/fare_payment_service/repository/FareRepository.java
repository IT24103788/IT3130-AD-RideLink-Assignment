package com.ridelink.fare_payment_service.repository;

import com.ridelink.fare_payment_service.model.Fare;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FareRepository extends MongoRepository<Fare, String> {

    Optional<Fare> findByRideId(String rideId);
}