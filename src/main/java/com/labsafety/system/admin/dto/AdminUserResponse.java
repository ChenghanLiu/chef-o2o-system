package com.labsafety.system.admin.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class AdminUserResponse {
    private Long id;
    private String username;
    private String phone;
    private String role;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;
}