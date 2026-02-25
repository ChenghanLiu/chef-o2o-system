package com.labsafety.system.review.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ReviewResponse {

    private Long id;
    private Long orderId;
    private String username;
    private Integer rating;
    private String comment;
    private Instant createdAt;
}