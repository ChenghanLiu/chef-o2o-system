package com.labsafety.system.admin.stats;

import com.labsafety.system.admin.stats.dto.AdminStatsResponse;
import com.labsafety.system.admin.stats.repo.AdminStatsJdbcRepository;
import com.labsafety.system.user.Role;
import com.labsafety.system.user.User;
import com.labsafety.system.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
public class AdminStatsService {

    private final AdminStatsJdbcRepository statsRepo;
    private final UserRepository userRepository;

    public AdminStatsService(AdminStatsJdbcRepository statsRepo, UserRepository userRepository) {
        this.statsRepo = statsRepo;
        this.userRepository = userRepository;
    }

    private User requireActiveUser(String identifier) {
        User me = userRepository.findByUsername(identifier)
                .orElseGet(() -> userRepository.findByPhone(identifier)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account not found")));
        if (!"ACTIVE".equals(me.getStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account disabled");
        }
        return me;
    }

    private void requireAdmin(String identifier) {
        User me = requireActiveUser(identifier);
        if (me.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only ADMIN");
        }
    }

    @Transactional(readOnly = true)
    public AdminStatsResponse getStats(String identifier) {
        requireAdmin(identifier);

        Map<String, Object> m = statsRepo.getStatsRow();
        AdminStatsResponse r = new AdminStatsResponse();

        r.totalAccounts = asLong(m.get("totalAccounts"));
        r.activeAccounts = asLong(m.get("activeAccounts"));
        r.disabledAccounts = asLong(m.get("disabledAccounts"));

        r.totalUsers = asLong(m.get("totalUsers"));
        r.totalChefs = asLong(m.get("totalChefs"));
        r.totalAdmins = asLong(m.get("totalAdmins"));

        r.chefProfilesTotal = asLong(m.get("chefProfilesTotal"));
        r.chefProfilesPending = asLong(m.get("chefProfilesPending"));
        r.chefProfilesApproved = asLong(m.get("chefProfilesApproved"));

        r.ordersTotal = asLong(m.get("ordersTotal"));
        r.ordersPending = asLong(m.get("ordersPending"));
        r.ordersAccepted = asLong(m.get("ordersAccepted"));
        r.ordersInProgress = asLong(m.get("ordersInProgress"));
        r.ordersCompleted = asLong(m.get("ordersCompleted"));
        r.ordersCancelled = asLong(m.get("ordersCancelled"));
        r.ordersRejected = asLong(m.get("ordersRejected"));

        r.revenueCompletedCents = asLong(m.get("revenueCompletedCents"));

        r.todayOrders = asLong(m.get("todayOrders"));
        r.todayCompletedOrders = asLong(m.get("todayCompletedOrders"));
        r.todayRevenueCents = asLong(m.get("todayRevenueCents"));

        r.reviewsTotal = asLong(m.get("reviewsTotal"));
        r.avgRatingAllReviews = asDouble(m.get("avgRatingAllReviews"));

        return r;
    }

    private long asLong(Object v) {
        if (v == null) return 0L;
        if (v instanceof Number n) return n.longValue();
        return Long.parseLong(String.valueOf(v));
    }

    private double asDouble(Object v) {
        if (v == null) return 0.0;
        if (v instanceof Number n) return n.doubleValue();
        return Double.parseDouble(String.valueOf(v));
    }
}