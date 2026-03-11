package com.labsafety.system.review.dto;

import java.time.LocalDateTime;
import java.time.Instant;



public class ChefReviewResponse {

    private Long id;
    private Integer rating;
    private String comment;
    private String username;
    private Instant createdAt;

    public ChefReviewResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}