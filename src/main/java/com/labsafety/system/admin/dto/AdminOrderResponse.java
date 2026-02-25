package com.labsafety.system.admin.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
public class AdminOrderResponse {
    private Long id;

    private Long userAccountId;
    private Long chefId;
    private Long serviceItemId;

    private LocalDateTime scheduledAt;
    private String address;
    private String note;

    private String status; // OrderStatus as String
    private Integer totalPriceCents;

    private Instant createdAt;
    private Instant updatedAt;
}