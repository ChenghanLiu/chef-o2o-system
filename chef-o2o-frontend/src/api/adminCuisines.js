import http from "@/utils/http";

/** GET /api/admin/cuisines */
export function getAdminCuisines() {
    return http.get("/api/admin/cuisines");
}

/** POST /api/admin/cuisines  body:{name,sortOrder} */
export function createAdminCuisine(data) {
    return http.post("/api/admin/cuisines", data);
}

/** PUT /api/admin/cuisines/{id}  body:{name,sortOrder} */
export function updateAdminCuisine(id, data) {
    return http.put(`/api/admin/cuisines/${id}`, data);
}

/** DELETE /api/admin/cuisines/{id} */
export function deleteAdminCuisine(id) {
    return http.delete(`/api/admin/cuisines/${id}`);
}