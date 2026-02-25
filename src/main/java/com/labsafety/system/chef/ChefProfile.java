package com.labsafety.system.chef;

import com.labsafety.system.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "chef_profiles")
public class ChefProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK -> accounts(id)
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false, unique = true)
    private User account;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Column(length = 500)
    private String bio;

    @Column(name = "service_area", length = 100)
    private String serviceArea;

    @Column(name = "work_time_desc", length = 100)
    private String workTimeDesc;

    @Column(name = "years_experience")
    private Integer yearsExperience;

    @Column(name = "base_price_cents")
    private Integer basePriceCents;

    @Column(name = "avg_rating", nullable = false)
    private Double avgRating = 0.0;

    @Column(name = "total_orders", nullable = false)
    private Integer totalOrders = 0;

    // PENDING / APPROVED / REJECTED / OFFLINE
    @Column(nullable = false, length = 20)
    private String status = "PENDING";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
        if (avgRating == null) avgRating = 0.0;
        if (totalOrders == null) totalOrders = 0;
        if (status == null || status.isBlank()) status = "PENDING";
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}