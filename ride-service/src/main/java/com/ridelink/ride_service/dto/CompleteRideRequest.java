package com.ridelink.ride_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompleteRideRequest {

    @NotNull(message = "Final fare is required")
    @DecimalMin(value = "0.0", message = "Final fare must be greater than or equal to 0")
    private Double finalFare;

    private Double actualDistanceKm;
}
