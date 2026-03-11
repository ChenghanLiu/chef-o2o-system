import { useAuthStore } from "@/store/auth";

export function setupGuards(router) {
    router.beforeEach(async (to) => {
        const auth = useAuthStore();

        // allow login always
        if (to.path === "/login") return true;

        // auth required?
        if (to.meta?.requiresAuth) {
            if (!auth.isAuthed) return { path: "/login" };

            // role check
            const roles = to.meta?.roles;
            if (Array.isArray(roles) && roles.length > 0) {
                if (!roles.includes(auth.role)) return { path: "/403" };
            }
        }

        return true;
    });
}