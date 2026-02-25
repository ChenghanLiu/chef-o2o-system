package com.labsafety.system.order.dto;

import java.time.LocalDateTime;

public class OrderQuoteResponse {

    private boolean bookable;
    private String reason;

    private Long chefId;
    private Long serviceItemId;
    private LocalDateTime scheduledAt;

    private Integer totalPriceCents;

    public OrderQuoteResponse() {}

    public static OrderQuoteResponse ok(Long chefId, Long serviceItemId, LocalDateTime scheduledAt, Integer totalPriceCents) {
        OrderQuoteResponse r = new OrderQuoteResponse();
        r.bookable = true;
        r.reason = null;
        r.chefId = chefId;
        r.serviceItemId = serviceItemId;
        r.scheduledAt = scheduledAt;
        r.totalPriceCents = totalPriceCents;
        return r;
    }

    public static OrderQuoteResponse fail(Long chefId, Long serviceItemId, LocalDateTime scheduledAt, String reason) {
        OrderQuoteResponse r = new OrderQuoteResponse();
        r.bookable = false;
        r.reason = reason;
        r.chefId = chefId;
        r.serviceItemId = serviceItemId;
        r.scheduledAt = scheduledAt;
        r.totalPriceCents = null;
        return r;
    }

    public boolean isBookable() { return bookable; }
    public void setBookable(boolean bookable) { this.bookable = bookable; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Long getChefId() { return chefId; }
    public void setChefId(Long chefId) { this.chefId = chefId; }

    public Long getServiceItemId() { return serviceItemId; }
    public void setServiceItemId(Long serviceItemId) { this.serviceItemId = serviceItemId; }

    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }

    public Integer getTotalPriceCents() { return totalPriceCents; }
    public void setTotalPriceCents(Integer totalPriceCents) { this.totalPriceCents = totalPriceCents; }
}