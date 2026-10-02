package com.ridelink.ride_service.controller;

import com.ridelink.ride_service.dto.*;
import com.ridelink.ride_service.model.RideStatus;
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

    // POST /api/rides — Create a new ride request (status = REQUESTED)
    @PostMapping
    public ResponseEntity<RideResponse> createRide(@Valid @RequestBody CreateRideRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rideService.createRide(request));
    }

    // GET /api/rides — Get all rides with optional filtering by status, passengerId, or driverId
    @GetMapping
    public ResponseEntity<List<RideResponse>> getAllRides(
            @RequestParam(required = false) RideStatus status,
            @RequestParam(required = false) String passengerId,
            @RequestParam(required = false) String driverId
    ) {
        return ResponseEntity.ok(rideService.getAllRides(status, passengerId, driverId));
    }

    // GET /api/rides/{rideId} — Get a specific ride
    @GetMapping("/{rideId}")
    public ResponseEntity<RideResponse> getRideById(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.getRideById(rideId));
    }

    // GET /api/rides/passenger/{passengerId} — Get rides for a passenger
    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<List<RideResponse>> getRidesByPassenger(@PathVariable String passengerId) {
        return ResponseEntity.ok(rideService.getRidesByPassenger(passengerId));
    }

    // GET /api/rides/driver/{driverId} — Get rides for a driver
    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<RideResponse>> getRidesByDriver(@PathVariable String driverId) {
        return ResponseEntity.ok(rideService.getRidesByDriver(driverId));
    }

    // PUT /api/rides/{rideId}/assign-driver — Assign a driver to a ride
    @PutMapping("/{rideId}/assign-driver")
    public ResponseEntity<RideResponse> assignDriver(
            @PathVariable String rideId,
            @Valid @RequestBody AssignDriverRequest request
    ) {
        return ResponseEntity.ok(rideService.assignDriver(rideId, request));
    }

    // PUT /api/rides/{rideId}/reassign-driver — Reassign to a new driver
    @PutMapping("/{rideId}/reassign-driver")
    public ResponseEntity<RideResponse> reassignDriver(
            @PathVariable String rideId,
            @Valid @RequestBody ReassignDriverRequest request
    ) {
        return ResponseEntity.ok(rideService.reassignDriver(rideId, request));
    }

    // PUT /api/rides/{rideId}/accept — Driver accepts the ride
    @PutMapping("/{rideId}/accept")
    public ResponseEntity<RideResponse> acceptRide(
            @PathVariable String rideId,
            @RequestBody(required = false) AcceptRideRequest request
    ) {
        return ResponseEntity.ok(rideService.acceptRide(rideId, request));
    }

    // PUT /api/rides/{rideId}/start — Driver starts the ride
    @PutMapping("/{rideId}/start")
    public ResponseEntity<RideResponse> startRide(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.startRide(rideId));
    }

    // PUT /api/rides/{rideId}/complete — Driver completes the ride
    @PutMapping("/{rideId}/complete")
    public ResponseEntity<RideResponse> completeRide(
            @PathVariable String rideId,
            @Valid @RequestBody CompleteRideRequest request
    ) {
        return ResponseEntity.ok(rideService.completeRide(rideId, request));
    }

    // PUT /api/rides/{rideId}/cancel — Cancel a ride
    @PutMapping("/{rideId}/cancel")
    public ResponseEntity<RideResponse> cancelRide(
            @PathVariable String rideId,
            @RequestBody(required = false) CancelRideRequest request
    ) {
        return ResponseEntity.ok(rideService.cancelRide(rideId, request));
    }

    // PUT /api/rides/{rideId}/status — Update the ride status generically
    @PutMapping("/{rideId}/status")
    public ResponseEntity<RideResponse> updateRideStatus(
            @PathVariable String rideId,
            @Valid @RequestBody UpdateRideStatusRequest request
    ) {
        return ResponseEntity.ok(rideService.updateRideStatus(rideId, request));
    }
}
