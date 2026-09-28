package com.ridelink.driver_service.repository;

import com.ridelink.driver_service.model.DriverProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DriverProfileRepository
        extends MongoRepository<DriverProfile, String> {
}