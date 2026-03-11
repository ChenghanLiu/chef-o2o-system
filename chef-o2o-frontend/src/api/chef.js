import http from "@/utils/http";

/**
 * GET /api/chefs?page=0&size=10
 */
export function getChefs(params) {
    return http.get("/api/chefs", { params });
}

/**
 * GET /api/chefs/{id}
 */
export function getChefById(id) {
    return http.get(`/api/chefs/${id}`);
}

export function listChefs(params = {}) {
    return http.get("/api/chefs", { params });
}