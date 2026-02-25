package com.labsafety.system.user.dto;

public class UserResponse {

    private Long id;

    // Login/display identity
    private String username;

    // Chef-O2O primary contact (phone-based)
    private String phone;

    // Keep for backward compatibility (old code/frontends may still expect "email")
    private String email;

    // "ADMIN" / "USER" / "CHEF"
    private String role;

    // "ACTIVE" / "DISABLED"
    private String status;

    public UserResponse() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}