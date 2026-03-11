import http from "@/utils/http";

// POST /api/auth/login :contentReference[oaicite:1]{index=1}
export function login(data) {
    return http.post("/api/auth/login", data);
}

// GET /api/auth/me :contentReference[oaicite:2]{index=2}
export function getMe() {
    return http.get("/api/auth/me");
}
export function register(data) {
    return http.post("/api/auth/register", data);
}