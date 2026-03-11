import http from "@/utils/http";

// 1) 查看自己资料
export function getChefMe() {
    return http.get("/api/chefs/me");
}

// 2) 查看自己菜系
export function getChefMeCuisines() {
    return http.get("/api/chefs/me/cuisines");
}

// 3) 更新自己菜系（Body: [1,2,3]）
export function updateChefMeCuisines(cuisineIds) {
    return http.put("/api/chefs/me/cuisines", cuisineIds);
}