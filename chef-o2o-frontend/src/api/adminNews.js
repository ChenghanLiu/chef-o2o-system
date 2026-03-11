import http from "@/utils/http";

/**
 * GET /api/admin/news?page=0&size=10
 * GET /api/admin/news?status=PUBLISHED&page=0&size=10
 */
export function getAdminNewsPage(params = {}) {
    return http.get("/api/admin/news", { params });
}

/**
 * POST /api/admin/news
 * body: { title, content }
 */
export function createAdminNews(data) {
    return http.post("/api/admin/news", data);
}

/**
 * PUT /api/admin/news/{id}
 * body: { title, content }
 */
export function updateAdminNews(id, data) {
    return http.put(`/api/admin/news/${id}`, data);
}

/**
 * POST /api/admin/news/{id}/hide
 */
export function hideAdminNews(id) {
    return http.post(`/api/admin/news/${id}/hide`);
}

/**
 * DELETE /api/admin/news/{id}
 */
export function deleteAdminNews(id) {
    return http.delete(`/api/admin/news/${id}`);
}