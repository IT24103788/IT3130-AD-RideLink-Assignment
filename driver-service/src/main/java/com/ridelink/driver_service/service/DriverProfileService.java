package com.ridelink.driver_service.service;

import com.ridelink.driver_service.model.Availability;
import com.ridelink.driver_service.model.DriverProfile;
import com.ridelink.driver_service.repository.DriverProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class DriverProfileService {

    private final DriverProfileRepository repository;

    public DriverProfileService(DriverProfileRepository repository) {
        this.repository = repository;
    }

    public DriverProfile create(DriverProfile profile) {
        profile.setId(null);
        profile.setAvailability(Availability.UNAVAILABLE);

        return repository.save(profile);
    }
}