package com.ridelink.account_service.controller;

import com.ridelink.account_service.dto.ChangePasswordRequest;
import com.ridelink.account_service.dto.UpdateProfileRequest;
import com.ridelink.account_service.dto.UserResponse;
import com.ridelink.account_service.model.AccountStatus;
import com.ridelink.account_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ─── GET /api/users/me ───────────────────────────────────────────────────
    // Returns the currently authenticated user's profile.
    // The JWT filter puts the userId as the principal (see JwtAuthenticationFilter).
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    // ─── PUT /api/users/me ────────────────────────────────────────────────────
    // Update name, phone, or profile image for the authenticated user.
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMyProfile(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(userId, request));
    }

    // ─── PUT /api/users/me/password ───────────────────────────────────────────
    // Change own password (requires old password confirmation).
    @PutMapping("/me/password")
    public ResponseEntity<String> changeMyPassword(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(userId, request);
        return ResponseEntity.ok("Password changed successfully");
    }

    // ─── GET /api/users ───────────────────────────────────────────────────────
    // Admin only: list all users.
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // ─── GET /api/users/{id} ──────────────────────────────────────────────────
    // Admin only: get a specific user by ID.
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // ─── PUT /api/users/{id}/status ───────────────────────────────────────────
    // Admin only: activate or deactivate an account.
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateUserStatus(
            @PathVariable String id,
            @RequestParam AccountStatus status) {
        userService.updateStatus(id, status);
        return ResponseEntity.ok("User status updated to " + status);
    }
}
