import http from "@/utils/http";

/**
 * 获取当前用户信息
 */
export function getMyProfile() {
    return http.get("/api/users/me");
}

/**
 * 更新当前用户信息
 */
export function updateMyProfile(data) {
    return http.put("/api/users/me", data);
}