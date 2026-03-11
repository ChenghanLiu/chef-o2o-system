import http from "@/utils/http";

/**
 * GET /api/admin/stats
 */
export function getAdminStats() {
    return http.get("/api/admin/stats");
}