package com.labsafety.system.admin.stats.repo;

public interface AdminStatsView {

    Long getTotalAccounts();
    Long getActiveAccounts();
    Long getDisabledAccounts();

    Long getTotalUsers();
    Long getTotalChefs();
    Long getTotalAdmins();

    Long getChefProfilesTotal();
    Long getChefProfilesPending();
    Long getChefProfilesApproved();

    Long getOrdersTotal();
    Long getOrdersPending();
    Long getOrdersAccepted();
    Long getOrdersInProgress();
    Long getOrdersCompleted();
    Long getOrdersCancelled();
    Long getOrdersRejected();

    Long getRevenueCompletedCents();

    Long getTodayOrders();
    Long getTodayCompletedOrders();
    Long getTodayRevenueCents();

    Long getReviewsTotal();
    Double getAvgRatingAllReviews();
}