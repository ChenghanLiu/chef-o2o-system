import { createRouter, createWebHistory } from "vue-router";
import { routes } from "./routes.js";
import { setupGuards } from "./guard.js";

const router = createRouter({
    history: createWebHistory(),
    routes
});

setupGuards(router);

export default router;