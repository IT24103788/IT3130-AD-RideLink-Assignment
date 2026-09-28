package com.ridelink.account_service.service;

import com.ridelink.account_service.dto.UserResponse;
import com.ridelink.account_service.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(String id) {
        com.ridelink.account_service.model.User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserResponse(user);
    }

    public UserResponse updateProfile(String id, com.ridelink.account_service.dto.UpdateProfileRequest request) {
        com.ridelink.account_service.model.User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getName() != null) user.setName(request.getName());
        if (request.getPhone() != null) {
            // Check if phone is already taken by someone else
            if (!request.getPhone().equals(user.getPhone()) && userRepository.existsByPhone(request.getPhone())) {
                throw new RuntimeException("Phone number is already registered");
            }
            user.setPhone(request.getPhone());
        }
        if (request.getProfileImage() != null) user.setProfileImage(request.getProfileImage());

        return new UserResponse(userRepository.save(user));
    }

    public void changePassword(String id, com.ridelink.account_service.dto.ChangePasswordRequest request) {
        com.ridelink.account_service.model.User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Incorrect old password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public void updateStatus(String id, com.ridelink.account_service.model.AccountStatus status) {
        com.ridelink.account_service.model.User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(status);
        userRepository.save(user);
    }
}
