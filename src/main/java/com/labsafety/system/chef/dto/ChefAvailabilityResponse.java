package com.labsafety.system.chef.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ChefAvailabilityResponse {

    private Long chefId;
    private int days;
    private List<LocalDateTime> slots;

    public ChefAvailabilityResponse() {}

    public ChefAvailabilityResponse(Long chefId, int days, List<LocalDateTime> slots) {
        this.chefId = chefId;
        this.days = days;
        this.slots = slots;
    }

    public Long getChefId() { return chefId; }
    public void setChefId(Long chefId) { this.chefId = chefId; }

    public int getDays() { return days; }
    public void setDays(int days) { this.days = days; }

    public List<LocalDateTime> getSlots() { return slots; }
    public void setSlots(List<LocalDateTime> slots) { this.slots = slots; }
}