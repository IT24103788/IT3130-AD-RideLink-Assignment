package com.ridelink.ride_service.controller;

import com.ridelink.ride_service.dto.AssignDriverRequest;
import com.ridelink.ride_service.dto.CreateRideRequest;
import com.ridelink.ride_service.dto.UpdateRideStatusRequest;
import com.ridelink.ride_service.model.Ride;
import com.ridelink.ride_service.service.RideService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    // POST /api/rides — Create a new ride
    @PostMapping
    public ResponseEntity<Ride> createRide(@Valid @RequestBody CreateRideRequest request) {
        Ride ride = rideService.createRide(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ride);
    }

    // GET /api/rides — Get all rides
    @GetMapping
    public ResponseEntity<List<Ride>> getAllRides() {
        return ResponseEntity.ok(rideService.getAllRides());
    }

    // GET /api/rides/{rideId} — Get a specific ride
    @GetMapping("/{rideId}")
    public ResponseEntity<Ride> getRideById(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.getRideById(rideId));
    }

    // GET /api/rides/passenger/{passengerId} — Get rides for a passenger
    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<List<Ride>> getRidesByPassenger(@PathVariable String passengerId) {
        return ResponseEntity.ok(rideService.getRidesByPassenger(passengerId));
    }

    // GET /api/rides/driver/{driverId} — Get rides for a driver
    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<Ride>> getRidesByDriver(@PathVariable String driverId) {
        return ResponseEntity.ok(rideService.getRidesByDriver(driverId));
    }

    // PUT /api/rides/{rideId}/assign-driver — Assign a driver to a ride
    @PutMapping("/{rideId}/assign-driver")
    public ResponseEntity<Ride> assignDriver(
            @PathVariable String rideId,
            @Valid @RequestBody AssignDriverRequest request) {
        return ResponseEntity.ok(rideService.assignDriver(rideId, request));
    }

    // PUT /api/rides/{rideId}/status — Update the ride status
    @PutMapping("/{rideId}/status")
    public ResponseEntity<Ride> updateRideStatus(
            @PathVariable String rideId,
            @Valid @RequestBody UpdateRideStatusRequest request) {
        return ResponseEntity.ok(rideService.updateRideStatus(rideId, request));
    }

    // PUT /api/rides/{rideId}/cancel — Cancel a ride
    @PutMapping("/{rideId}/cancel")
    public ResponseEntity<Ride> cancelRide(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.cancelRide(rideId));
    }
}
