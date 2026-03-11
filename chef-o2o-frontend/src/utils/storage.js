const TOKEN_KEY = "token";
const ROLE_KEY = "role";

export const storage = {
    getToken() {
        return localStorage.getItem(TOKEN_KEY) || "";
    },
    setToken(token) {
        localStorage.setItem(TOKEN_KEY, token || "");
    },
    removeToken() {
        localStorage.removeItem(TOKEN_KEY);
    },

    getRole() {
        return localStorage.getItem(ROLE_KEY) || "";
    },
    setRole(role) {
        localStorage.setItem(ROLE_KEY, role || "");
    },
    removeRole() {
        localStorage.removeItem(ROLE_KEY);
    },

    clearAuth() {
        this.removeToken();
        this.removeRole();
    }
};