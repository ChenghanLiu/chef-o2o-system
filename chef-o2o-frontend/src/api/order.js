import http from "@/utils/http";

/**
 * POST /api/orders
 * body: { chefId, serviceItemId, scheduledAt, address, note }
 */
export function createOrder(data) {
    return http.post("/api/orders", data);
}

export function getMyOrders(params) {
    return http.get("/api/orders/me", { params });
}