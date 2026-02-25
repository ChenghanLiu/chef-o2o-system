package com.labsafety.system.cuisine.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChefPublicCardResponse {
    private Long chefId;
    private Long accountId;

    private String username;
    private String phone;

    private String avatarUrl;
    private String bio;
    private String serviceArea;
    private String workTimeDesc;

    private Integer yearsExperience;
    private Integer basePriceCents;

    private double avgRating;
    private int totalOrders;
}