import http from "@/utils/http";

export function getChefById(chefId) {
    return http.get(`/api/chefs/${chefId}`);
}