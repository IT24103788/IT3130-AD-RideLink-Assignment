package com.ridelink.driver_service.dto;

import com.ridelink.driver_service.model.Availability;
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
public class UpdateAvailabilityRequest {

    @NotNull(message = "Availability status is required (AVAILABLE, UNAVAILABLE, ON_RIDE)")
    private Availability availability;
}
