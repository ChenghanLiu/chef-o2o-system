import http from "@/utils/http";

/**
 * GET /api/admin/chefs?status=PENDING&page=0&size=10
 */
export function getAdminChefsPage(params = {}) {
    return http.get("/api/admin/chefs", { params });
}

/**
 * POST /api/admin/chefs/{chefId}/approve
 */
export function approveChef(chefId) {
    return http.post(`/api/admin/chefs/${chefId}/approve`);
}

/**
 * GET /api/admin/chefs/{chefId}/cuisines
 */
export function getAdminChefCuisines(chefId) {
    return http.get(`/api/admin/chefs/${chefId}/cuisines`);
}

/**
 * PUT /api/admin/chefs/{chefId}/cuisines
 * body: { cuisineIds: [1,2,3] }
 */
export function updateAdminChefCuisines(chefId, data) {
    return http.put(`/api/admin/chefs/${chefId}/cuisines`, data);
}