package com.labsafety.system.admin.stats.repo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class AdminStatsJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public AdminStatsJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> getStatsRow() {
        String sql = """
            SELECT
              (SELECT COUNT(*) FROM accounts) AS totalAccounts,
              (SELECT COUNT(*) FROM accounts WHERE status = 'ACTIVE') AS activeAccounts,
              (SELECT COUNT(*) FROM accounts WHERE status <> 'ACTIVE') AS disabledAccounts,

              (SELECT COUNT(*) FROM accounts WHERE role = 'USER') AS totalUsers,
              (SELECT COUNT(*) FROM accounts WHERE role = 'CHEF') AS totalChefs,
              (SELECT COUNT(*) FROM accounts WHERE role = 'ADMIN') AS totalAdmins,

              (SELECT COUNT(*) FROM chef_profiles) AS chefProfilesTotal,
              (SELECT COUNT(*) FROM chef_profiles WHERE status = 'PENDING') AS chefProfilesPending,
              (SELECT COUNT(*) FROM chef_profiles WHERE status = 'APPROVED') AS chefProfilesApproved,

              (SELECT COUNT(*) FROM orders) AS ordersTotal,
              (SELECT COUNT(*) FROM orders WHERE status = 'PENDING') AS ordersPending,
              (SELECT COUNT(*) FROM orders WHERE status = 'ACCEPTED') AS ordersAccepted,
              (SELECT COUNT(*) FROM orders WHERE status = 'IN_PROGRESS') AS ordersInProgress,
              (SELECT COUNT(*) FROM orders WHERE status = 'COMPLETED') AS ordersCompleted,
              (SELECT COUNT(*) FROM orders WHERE status = 'CANCELLED') AS ordersCancelled,
              (SELECT COUNT(*) FROM orders WHERE status = 'REJECTED') AS ordersRejected,

              (SELECT COALESCE(SUM(total_price_cents), 0) FROM orders WHERE status = 'COMPLETED') AS revenueCompletedCents,

              (SELECT COUNT(*) FROM orders WHERE DATE(created_at) = CURDATE()) AS todayOrders,
              (SELECT COUNT(*) FROM orders WHERE DATE(created_at) = CURDATE() AND status = 'COMPLETED') AS todayCompletedOrders,
              (SELECT COALESCE(SUM(total_price_cents), 0) FROM orders WHERE DATE(created_at) = CURDATE() AND status = 'COMPLETED') AS todayRevenueCents,

              (SELECT COUNT(*) FROM reviews) AS reviewsTotal,
              (SELECT COALESCE(AVG(rating), 0) FROM reviews) AS avgRatingAllReviews
            """;

        return jdbcTemplate.queryForMap(sql);
    }
}