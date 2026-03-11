import http from "@/utils/http";

// 9) 查看自己的订单
export function getChefOrders(params) {
    return http.get("/api/orders/chef/me", { params });
}

// 10) 查看订单详情
export function getOrderDetail(orderId) {
    return http.get(`/api/orders/chef/${orderId}`);
}

// 11) 处理订单（Body: { action: "ACCEPT" | "REJECT" | "START" | "COMPLETE" }）
export function chefOrderAction(orderId, action) {
    return http.post(`/api/orders/${orderId}/chef/action`, { action });
}