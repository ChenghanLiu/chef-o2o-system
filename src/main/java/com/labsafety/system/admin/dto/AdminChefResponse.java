package com.labsafety.system.admin.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class AdminChefResponse {
    private Long chefProfileId;
    private Long accountId;

    private String username;
    private String phone;

    private String status;       // PENDING / APPROVED / REJECTED

    private String avatarUrl;
    private String bio;
    private String serviceArea;
    private String workTimeDesc;

    private Integer yearsExperience;

    private double avgRating;
    private int totalOrders;

    private Instant createdAt;
    private Instant updatedAt;
}