import http from "@/utils/http";

// CHEF：我的服务项目（分页）
export function getServiceItems(params) {
    return http.get("/api/service-items", { params });
}
export function getMyServiceItems(page = 0, size = 10) {
    return http.get("/api/service-items/me", { params: { page, size } });
}

// CHEF：创建服务项目
export function createServiceItem(data) {
    // { title, durationMinutes, priceCents, status }
    return http.post("/api/service-items", data);
}

// ✅ CHEF：更新我的服务项目
export function updateMyServiceItem(id, data) {
    // PUT /api/service-items/my/{id}
    // { title, durationMinutes, priceCents }
    return http.put(`/api/service-items/my/${id}`, data);
}

// ✅ CHEF：下架
export function deactivateServiceItem(id) {
    return http.post(`/api/service-items/my/${id}/deactivate`);
}

// ✅ CHEF：上架
export function activateServiceItem(id) {
    return http.post(`/api/service-items/my/${id}/activate`);
}