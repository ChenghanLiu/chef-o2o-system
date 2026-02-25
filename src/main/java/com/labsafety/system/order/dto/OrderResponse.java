package com.labsafety.system.order.dto;

import com.labsafety.system.order.OrderStatus;

import java.time.Instant;
import java.time.LocalDateTime;

public class OrderResponse {

    private Long id;

    private Long userAccountId;
    private Long chefId;
    private Long serviceItemId;

    private LocalDateTime scheduledAt;
    private String address;
    private String note;

    private OrderStatus status;
    private Integer totalPriceCents;

    private Instant createdAt;
    private Instant updatedAt;

    public OrderResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserAccountId() { return userAccountId; }
    public void setUserAccountId(Long userAccountId) { this.userAccountId = userAccountId; }

    public Long getChefId() { return chefId; }
    public void setChefId(Long chefId) { this.chefId = chefId; }

    public Long getServiceItemId() { return serviceItemId; }
    public void setServiceItemId(Long serviceItemId) { this.serviceItemId = serviceItemId; }

    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public Integer getTotalPriceCents() { return totalPriceCents; }
    public void setTotalPriceCents(Integer totalPriceCents) { this.totalPriceCents = totalPriceCents; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}