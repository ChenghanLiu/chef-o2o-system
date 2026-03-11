<template>
  <el-container class="app-shell">
    <el-aside width="220px" class="aside">
      <Sidebar />
    </el-aside>

    <el-container>
      <el-header class="header">
        <Topbar />
      </el-header>

      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import Sidebar from "./Sidebar.vue";
import Topbar from "./Topbar.vue";
</script>

<style scoped>
.app-shell { height: 100vh; }
.aside { border-right: 1px solid #eee; }
.header { border-bottom: 1px solid #eee; display:flex; align-items:center; }
.main { background: #fafafa; }
</style>