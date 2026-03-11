import http from "@/utils/http";

/**
 * GET /api/reviews/chef/{chefId}
 */
export function getReviewsByChefId(chefId) {
    return http.get(`/api/reviews/chef/${chefId}`);
}

/**
 * POST /api/reviews
 * body: { orderId, rating, comment }
 */
export function createReview(data) {
    return http.post("/api/reviews", data);
}

// 删除评价（假设：DELETE /api/reviews/{id}）
export function deleteReview(reviewId) {
    return http.delete(`/api/reviews/${reviewId}`);
}