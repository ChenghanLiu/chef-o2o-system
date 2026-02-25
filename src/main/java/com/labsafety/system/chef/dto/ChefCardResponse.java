package com.labsafety.system.chef.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ChefCardResponse {

    private Long chefProfileId;
    private Long accountId;

    private String username;
    private String phone;

    private String avatarUrl;
    private String bio;
    private String serviceArea;
    private String workTimeDesc;

    private Integer yearsExperience;

    private double avgRating;
    private int totalOrders;

    // commerce
    private Integer minPriceCents;     // null if no active items
    private long serviceItemCount;     // 0 if none

    // availability preview (next 3)
    private List<LocalDateTime> nextSlots;

    // simple “can user book”
    private boolean bookable;

    private String status; // APPROVED etc

    public ChefCardResponse() {}

    public Long getChefProfileId() { return chefProfileId; }
    public void setChefProfileId(Long chefProfileId) { this.chefProfileId = chefProfileId; }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getServiceArea() { return serviceArea; }
    public void setServiceArea(String serviceArea) { this.serviceArea = serviceArea; }

    public String getWorkTimeDesc() { return workTimeDesc; }
    public void setWorkTimeDesc(String workTimeDesc) { this.workTimeDesc = workTimeDesc; }

    public Integer getYearsExperience() { return yearsExperience; }
    public void setYearsExperience(Integer yearsExperience) { this.yearsExperience = yearsExperience; }

    public double getAvgRating() { return avgRating; }
    public void setAvgRating(double avgRating) { this.avgRating = avgRating; }

    public int getTotalOrders() { return totalOrders; }
    public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }

    public Integer getMinPriceCents() { return minPriceCents; }
    public void setMinPriceCents(Integer minPriceCents) { this.minPriceCents = minPriceCents; }

    public long getServiceItemCount() { return serviceItemCount; }
    public void setServiceItemCount(long serviceItemCount) { this.serviceItemCount = serviceItemCount; }

    public List<LocalDateTime> getNextSlots() { return nextSlots; }
    public void setNextSlots(List<LocalDateTime> nextSlots) { this.nextSlots = nextSlots; }

    public boolean isBookable() { return bookable; }
    public void setBookable(boolean bookable) { this.bookable = bookable; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}