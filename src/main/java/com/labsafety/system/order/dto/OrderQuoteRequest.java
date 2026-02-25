package com.labsafety.system.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class OrderQuoteRequest {

    @NotNull
    private Long chefId;

    @NotNull
    private Long serviceItemId;

    @NotNull
    private LocalDateTime scheduledAt;

    @NotBlank
    private String address;

    public OrderQuoteRequest() {}

    public Long getChefId() { return chefId; }
    public void setChefId(Long chefId) { this.chefId = chefId; }

    public Long getServiceItemId() { return serviceItemId; }
    public void setServiceItemId(Long serviceItemId) { this.serviceItemId = serviceItemId; }

    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}