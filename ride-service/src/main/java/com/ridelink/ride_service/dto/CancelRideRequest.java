package com.ridelink.ride_service.dto;

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
public class CancelRideRequest {

    private String reason;
    private String cancelledBy; // "PASSENGER", "DRIVER", "ADMIN"
}
