import http from "@/utils/http";

/**
 * GET /api/admin/orders?page=0&size=10
 * GET /api/admin/orders?status=PENDING&page=0&size=10
 */
export function getAdminOrdersPage(params = {}) {
    return http.get("/api/admin/orders", { params });
}

/**
 * PUT /api/admin/orders/{orderId}/status
 * body: { status: "COMPLETED" }
 */
export function updateAdminOrderStatus(orderId, data) {
    return http.put(`/api/admin/orders/${orderId}/status`, data);
}