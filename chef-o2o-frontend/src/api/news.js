import http from "@/utils/http";

/**
 * GET /api/news?page=0&size=10
 */
export function getNewsPage(params = {}) {
    return http.get("/api/news", { params });
}

/**
 * GET /api/news/{id}
 */
export function getNewsById(id) {
    return http.get(`/api/news/${id}`);
}