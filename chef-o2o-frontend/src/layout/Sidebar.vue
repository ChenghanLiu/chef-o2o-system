<template>
  <div class="sidebar">
    <div class="brand">舌尖到家厨师预约平台</div>

    <el-menu :default-active="active" router>
      <template v-if="auth.isUser">
        <el-menu-item index="/user/profile">个人主页</el-menu-item>
        <el-menu-item index="/user/chefs">浏览厨师</el-menu-item>
        <el-menu-item index="/user/orders">我的订单</el-menu-item>
        <el-menu-item index="/user/circle">交流圈</el-menu-item>
        <el-menu-item index="/user/cuisines">菜系分类</el-menu-item>
        <el-menu-item index="/user/news">新闻</el-menu-item>

      </template>

      <template v-else-if="auth.isChef">
        <el-menu-item index="/chef/profile">厨师主页</el-menu-item>
        <el-menu-item index="/chef/service-items">服务项目</el-menu-item>
        <el-menu-item index="/chef/orders">我的订单</el-menu-item>
        <el-menu-item index="/chef/reviews">我的评价</el-menu-item>
        <el-menu-item index="/chef/circle">交流圈</el-menu-item>
        <el-menu-item index="/chef/news">新闻</el-menu-item>

      </template>

      <template v-else-if="auth.isAdmin">
        <el-menu-item index="/admin/stats">数据统计</el-menu-item>
        <el-menu-item index="/admin/users">用户管理</el-menu-item>
        <el-menu-item index="/admin/chef-approvals">厨师审核</el-menu-item>
        <el-menu-item index="/admin/orders">订单管理</el-menu-item>
        <el-menu-item index="/admin/cuisines">菜系管理</el-menu-item>
        <el-menu-item index="/admin/news">新闻管理</el-menu-item>
        <el-menu-item index="/admin/circle">交流圈管理</el-menu-item>
        <el-menu-item index="/admin/chef-profiles">厨师档案</el-menu-item>
      </template>
    </el-menu>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useRoute } from "vue-router";
import { useAuthStore } from "@/store/auth";

const auth = useAuthStore();
const route = useRoute();
const active = computed(() => route.path);
</script>

<style scoped>
.sidebar { height:100%; display:flex; flex-direction:column; }
.brand { padding: 16px; font-weight: 700; border-bottom: 1px solid #eee; }
</style>