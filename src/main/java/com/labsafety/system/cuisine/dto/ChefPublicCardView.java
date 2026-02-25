package com.labsafety.system.cuisine.dto;

public interface ChefPublicCardView {
    Long getChefId();
    Long getAccountId();

    String getUsername();
    String getPhone();

    String getAvatarUrl();
    String getBio();
    String getServiceArea();
    String getWorkTimeDesc();

    Integer getYearsExperience();
    Integer getBasePriceCents();

    Double getAvgRating();
    Integer getTotalOrders();
}