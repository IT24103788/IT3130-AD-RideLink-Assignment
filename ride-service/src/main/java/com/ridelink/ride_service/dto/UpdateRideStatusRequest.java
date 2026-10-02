package com.ridelink.ride_service.dto;

import com.ridelink.ride_service.model.RideStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateRideStatusRequest {

    @NotNull(message = "Status is required")
    private RideStatus status;

    private Double finalFare;

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public Double getFinalFare() {
        return finalFare;
    }

    public void setFinalFare(Double finalFare) {
        this.finalFare = finalFare;
    }
}
