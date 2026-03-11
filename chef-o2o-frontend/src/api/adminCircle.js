import http from "@/utils/http";

/**
 * GET /api/admin/circle/posts?page=0&size=10
 * GET /api/admin/circle/posts?status=PUBLISHED&page=0&size=10
 */
export function getAdminCirclePostsPage(params = {}) {
    return http.get("/api/admin/circle/posts", { params });
}

/** POST /api/admin/circle/posts/{id}/hide */
export function hideCirclePost(id) {
    return http.post(`/api/admin/circle/posts/${id}/hide`);
}

/** POST /api/admin/circle/posts/{id}/publish */
export function publishCirclePost(id) {
    return http.post(`/api/admin/circle/posts/${id}/publish`);
}