import http from "@/utils/http";

/**
 * GET /api/cuisines?page=0&size=10
 * 注意：后端返回的是数组，不是 Page
 */
export function listCuisines(params) {
    return http.get("/api/cuisines", { params });
}

/**
 * GET /api/cuisines?chefId=1&page=0&size=10
 * 注意：后端返回数组
 */
/**
 * GET /api/chefs/{chefId}/cuisines
 */
export function getCuisinesByChefId(chefId) {
    return http.get(`/api/chefs/${chefId}/cuisines`);
}