package com.labsafety.system.admin.stats.dto;

public class AdminStatsResponse {

    public long totalAccounts;
    public long activeAccounts;
    public long disabledAccounts;

    public long totalUsers;
    public long totalChefs;
    public long totalAdmins;

    public long chefProfilesTotal;
    public long chefProfilesPending;
    public long chefProfilesApproved;

    public long ordersTotal;
    public long ordersPending;
    public long ordersAccepted;
    public long ordersInProgress;
    public long ordersCompleted;
    public long ordersCancelled;
    public long ordersRejected;

    public long revenueCompletedCents;

    public long todayOrders;
    public long todayCompletedOrders;
    public long todayRevenueCents;

    public long reviewsTotal;
    public double avgRatingAllReviews;
}