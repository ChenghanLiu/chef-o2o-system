package com.labsafety.system.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "orders",
        indexes = {
                @Index(name = "idx_orders_user_account_id", columnList = "user_account_id"),
                @Index(name = "idx_orders_chef_id", columnList = "chef_id"),
                @Index(name = "idx_orders_service_item_id", columnList = "service_item_id"),
                @Index(name = "idx_orders_status", columnList = "status")
        }
)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // customer account id -> accounts.id
    @Column(name = "user_account_id", nullable = false)
    private Long userAccountId;

    // chef profile id -> chef_profiles.id
    @Column(name = "chef_id", nullable = false)
    private Long chefId;

    // service item id -> service_items.id
    @Column(name = "service_item_id", nullable = false)
    private Long serviceItemId;

    // datetime in MySQL; map to LocalDateTime
    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    @Column(name = "address", nullable = false, length = 200)
    private String address;

    @Column(name = "note", length = 300)
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private OrderStatus status;

    @Column(name = "total_price_cents", nullable = false)
    private Integer totalPriceCents;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) status = OrderStatus.PENDING;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}