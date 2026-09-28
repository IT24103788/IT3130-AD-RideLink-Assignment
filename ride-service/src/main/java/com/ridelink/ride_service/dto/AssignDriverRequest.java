package com.ridelink.ride_service.dto;

import jakarta.validation.constraints.NotBlank;

public class AssignDriverRequest {

    @NotBlank(message = "Driver ID is required")
    private String driverId;

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }
}
