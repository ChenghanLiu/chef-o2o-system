import http from "@/utils/http";

// 12) 查看别人给我的评价
export function getMyChefReviews() {
    return http.get("/api/reviews/chef/me");
}