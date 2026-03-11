import { defineStore } from "pinia";
import { storage } from "@/utils/storage";
import { login as apiLogin, getMe as apiGetMe } from "@/api/auth";

export const useAuthStore = defineStore("auth", {
    state: () => ({
        token: storage.getToken(),
        role: storage.getRole(),
        me: null,
    }),
    getters: {
        isAuthed: (s) => !!s.token,
        isUser: (s) => s.role === "USER",
        isChef: (s) => s.role === "CHEF",
        isAdmin: (s) => s.role === "ADMIN",
    },
    actions: {
        async login(identifier, password) {
            const resp = await apiLogin({ identifier, password });
            const { token, role } = resp.data || {}; // 你说后端返回 token+role
            storage.setToken(token || "");
            storage.setRole(role || "");
            this.token = token || "";
            this.role = role || "";

            // 拉取 /me（可选但建议）
            const meResp = await apiGetMe();
            this.me = meResp.data || null;
            if (this.me?.role && this.me.role !== this.role) {
                this.role = this.me.role;
                storage.setRole(this.role);
            }
        },
        logout() {
            storage.clearAuth();
            this.token = "";
            this.role = "";
            this.me = null;
        },
    },
});