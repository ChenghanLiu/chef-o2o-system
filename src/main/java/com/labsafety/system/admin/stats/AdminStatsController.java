package com.labsafety.system.admin.stats;

import com.labsafety.system.admin.stats.dto.AdminStatsResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/stats")
public class AdminStatsController {

    private final com.labsafety.system.admin.stats.AdminStatsService statsService;

    public AdminStatsController(com.labsafety.system.admin.stats.AdminStatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping
    public AdminStatsResponse get(Authentication auth) {
        return statsService.getStats(auth.getName());
    }
}
