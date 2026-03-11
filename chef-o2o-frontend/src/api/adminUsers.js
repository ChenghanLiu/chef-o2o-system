import http from "@/utils/http";

/**
 * GET /api/admin/users?page=0&size=10
 */
export function getAdminUsersPage(params = {}) {
    return http.get("/api/admin/users", { params });
}

/**
 * PUT /api/admin/users/{id}/disable
 */
export function disableUser(id) {
    return http.put(`/api/admin/users/${id}/disable`);
}

/**
 * PUT /api/admin/users/{id}/enable
 */
export function enableUser(id) {
    return http.put(`/api/admin/users/${id}/enable`);
}