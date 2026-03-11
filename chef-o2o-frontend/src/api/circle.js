import http from "@/utils/http";

export function getCirclePosts(params = {}) {
    return http.get("/api/circle/posts", { params });
}

export function createCirclePost(data) {
    return http.post("/api/circle/posts", data);
}

export function createCircleComment(postId, data) {
    return http.post(`/api/circle/posts/${postId}/comments`, data);
}

/**
 * GET /api/circle/posts/{postId}/comments?page=0&size=10
 */
export function getCircleComments(postId, params = {}) {
    return http.get(`/api/circle/posts/${postId}/comments`, { params });
}