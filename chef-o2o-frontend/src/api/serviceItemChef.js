import http from "@/utils/http";

// 4) 查看自己服务项目
export function getMyServiceItems(params) {
    return http.get("/api/service-items/my", { params });
}

// 5) 创建服务项目
export function createMyServiceItem(data) {
    return http.post("/api/service-items/my", data);
}

// 6) 更新服务项目
export function updateMyServiceItem(id, data) {
    return http.put(`/api/service-items/my/${id}`, data);
}

// 7) 下架
export function deactivateMyServiceItem(id) {
    return http.post(`/api/service-items/my/${id}/deactivate`);
}

// 8) 上架
export function activateMyServiceItem(id) {
    return http.post(`/api/service-items/my/${id}/activate`);
}