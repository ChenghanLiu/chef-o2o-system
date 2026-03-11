import Layout from "@/layout/Layout.vue";

const Login = () => import("@/views/auth/Login.vue");
const Forbidden = () => import("@/views/common/Forbidden.vue");
const NotFound = () => import("@/views/common/NotFound.vue");

// USER
const UserChefs = () => import("@/views/user/Chefs.vue");
const UserOrders = () => import("@/views/user/Orders.vue");
const UserCircle = () => import("@/views/user/Circle.vue");

// CHEF
const ChefProfile = () => import("@/views/chef/Profile.vue");
const ChefServiceItems = () => import("@/views/chef/ServiceItems.vue");
const ChefOrders = () => import("@/views/chef/Orders.vue");
const ChefReviews = () => import("@/views/chef/Reviews.vue");

// ADMIN
const AdminStats = () => import("@/views/admin/Stats.vue");
const AdminUsers = () => import("@/views/admin/Users.vue");
const AdminChefApprovals = () => import("@/views/admin/ChefApprovals.vue");
const AdminOrders = () => import("@/views/admin/Orders.vue");
const AdminCuisines = () => import("@/views/admin/Cuisines.vue");
const AdminNews = () => import("@/views/admin/News.vue");
const AdminCircle = () => import("@/views/admin/Circle.vue");

export const routes = [
    { path: "/login", name: "Login", component: Login },



    { path: "/403", name: "Forbidden", component: Forbidden },
    {
        path: "/register",
        component: () => import("@/views/auth/Register.vue")
    },

    {
        path: "/",
        component: Layout,
        meta: { requiresAuth: true },
        children: [
            // USER
            { path: "user/chefs", name: "UserChefs", component: UserChefs, meta: { roles: ["USER"] } },
            { path: "user/chefs/:id", name: "UserChefDetail", component: () => import("@/views/user/ChefDetail.vue"), meta: { roles: ["USER"] } },
            { path: "user/orders", name: "UserOrders", component: UserOrders, meta: { roles: ["USER"] } },
            { path: "user/orders/create", name: "UserCreateOrder", component: () => import("@/views/user/CreateOrder.vue"), meta: { roles: ["USER"] } },
            { path: "user/circle", name: "UserCircle", component: UserCircle, meta: { roles: ["USER"] } },
            {
                path: "user/profile",
                name: "UserProfile",
                component: () => import("@/views/user/Profile.vue"),
                meta: { roles: ["USER"] }
            },
            {
                path: "user/reviews/create",
                name: "UserReviewCreate",
                component: () => import("@/views/user/ReviewCreate.vue"),
                meta: { roles: ["USER"] }
            },
            { path: "user/circle", name: "UserCircle", component: () => import("@/views/user/Circle.vue"), meta: { roles: ["USER"] } },
            { path: "user/circle/create", name: "UserCircleCreate", component: () => import("@/views/user/CircleCreate.vue"), meta: { roles: ["USER"] } },
            { path: "user/circle/:id", name: "UserCircleDetail", component: () => import("@/views/user/CircleDetail.vue"), meta: { roles: ["USER"] } },
            { path: "user/news", name: "UserNews", component: () => import("@/views/user/News.vue"), meta: { roles: ["USER"] } },
            { path: "user/news/:id", name: "UserNewsDetail", component: () => import("@/views/user/NewsDetail.vue"), meta: { roles: ["USER"] } },
            { path: "user/cuisines", name: "UserCuisines", component: () => import("@/views/user/Cuisines.vue"), meta: { roles: ["USER"] } },
            { path: "user/cuisines/:id/chefs", name: "UserCuisineChefs", component: () => import("@/views/user/CuisineChefs.vue"), meta: { roles: ["USER"] } },

            // CHEF
            { path: "chef/profile", name: "ChefProfile", component: ChefProfile, meta: { roles: ["CHEF"] } },
            { path: "chef/service-items", name: "ChefServiceItems", component: ChefServiceItems, meta: { roles: ["CHEF"] } },
            { path: "chef/orders", name: "ChefOrders", component: ChefOrders, meta: { roles: ["CHEF"] } },
            // ===== Chef 公用 Circle & News =====

            {
                path: "chef/news",
                name: "ChefNews",
                component: () => import("@/views/user/News.vue"),
                meta: { roles: ["CHEF"] }
            },

            {
                path: "chef/news/:id",
                name: "ChefNewsDetail",
                component: () => import("@/views/user/NewsDetail.vue"),
                meta: { roles: ["CHEF"] }
            },

            {
                path: "chef/circle",
                name: "ChefCircle",
                component: () => import("@/views/user/Circle.vue"),
                meta: { roles: ["CHEF"] }
            },

            {
                path: "chef/circle/create",
                name: "ChefCircleCreate",
                component: () => import("@/views/user/CircleCreate.vue"),
                meta: { roles: ["CHEF"] }
            },

            {
                path: "chef/circle/:id",
                name: "ChefCircleDetail",
                component: () => import("@/views/user/CircleDetail.vue"),
                meta: { roles: ["CHEF"] }
            },
            { path: "chef/reviews", name: "ChefReviews", component: ChefReviews, meta: { roles: ["CHEF"] } },

            // ADMIN
            { path: "admin/stats", name: "AdminStats", component: AdminStats, meta: { roles: ["ADMIN"] } },
            { path: "admin/users", name: "AdminUsers", component: AdminUsers, meta: { roles: ["ADMIN"] } },
            { path: "admin/chef-approvals", name: "AdminChefApprovals", component: AdminChefApprovals, meta: { roles: ["ADMIN"] } },
            { path: "admin/orders", name: "AdminOrders", component: AdminOrders, meta: { roles: ["ADMIN"] } },
            { path: "admin/cuisines", name: "AdminCuisines", component: AdminCuisines, meta: { roles: ["ADMIN"] } },
            { path: "admin/news", name: "AdminNews", component: AdminNews, meta: { roles: ["ADMIN"] } },
            { path: "admin/circle", name: "AdminCircle", component: AdminCircle, meta: { roles: ["ADMIN"] } },
            { path: "admin/chef-profiles", name: "AdminChefProfiles", component: () => import("@/views/admin/ChefProfiles.vue"), meta: { roles: ["ADMIN"] } },
            { path: "admin/news", name: "AdminNews", component: () => import("@/views/admin/News.vue"), meta: { roles: ["ADMIN"] } }
        ]
    },

    { path: "/:pathMatch(.*)*", name: "NotFound", component: NotFound }
];