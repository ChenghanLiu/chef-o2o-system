<template>
  <div class="topbar">
    <div class="left">
      <span class="role">身份: {{ auth.role || "-" }}</span>
      <span class="user">用户: {{ auth.me?.username || "-" }}</span>
    </div>

    <div class="right">
      <el-button size="small" @click="onLogout">Logout</el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";
import { useAuthStore } from "@/store/auth";

const auth = useAuthStore();
const router = useRouter();

function onLogout() {
  auth.logout();
  router.push("/login");
}
</script>

<style scoped>
.topbar { width:100%; display:flex; justify-content:space-between; align-items:center; }
.left { display:flex; gap:12px; }
.role, .user { color:#666; }
</style>