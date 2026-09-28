package com.ridelink.account_service.dto;

import com.ridelink.account_service.model.AccountStatus;
import com.ridelink.account_service.model.Role;
import com.ridelink.account_service.model.User;

public class UserResponse {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String profileImage;
    private Role role;
    private AccountStatus status;

    public UserResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.profileImage = user.getProfileImage();
        this.role = user.getRole();
        this.status = user.getStatus();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getProfileImage() { return profileImage; }
    public Role getRole() { return role; }
    public AccountStatus getStatus() { return status; }
}
