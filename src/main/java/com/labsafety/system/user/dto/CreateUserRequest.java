package com.labsafety.system.user.dto;

import jakarta.validation.constraints.Size;

public class CreateUserRequest {

    @Size(max = 100)
    private String username;

    @Size(max = 20)
    private String phone;

    @Size(min = 6, max = 100)
    private String password;

    // ADMIN / USER / CHEF
    private String role;

    public CreateUserRequest() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}