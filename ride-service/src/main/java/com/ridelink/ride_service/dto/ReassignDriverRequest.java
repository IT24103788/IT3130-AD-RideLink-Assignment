package com.ridelink.ride_service.dto;

import jakarta.validation.constraints.NotBlank;
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
public class ReassignDriverRequest {

    @NotBlank(message = "New driver ID is required")
    private String newDriverId;

    private String reason;
}
