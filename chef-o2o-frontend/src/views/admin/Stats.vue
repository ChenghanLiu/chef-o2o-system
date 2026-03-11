<template>
  <div class="page">
    <div class="topbar">
      <div class="title">数据统计</div>
      <el-button size="small" @click="load">刷新</el-button>
    </div>

    <el-card shadow="never">
      <el-descriptions :column="3" border>
        <el-descriptions-item label="总账号数">{{ data.totalAccounts }}</el-descriptions-item>
        <el-descriptions-item label="活跃账号">{{ data.activeAccounts }}</el-descriptions-item>
        <el-descriptions-item label="禁用账号">{{ data.disabledAccounts }}</el-descriptions-item>

        <el-descriptions-item label="用户数">{{ data.totalUsers }}</el-descriptions-item>
        <el-descriptions-item label="厨师数">{{ data.totalChefs }}</el-descriptions-item>
        <el-descriptions-item label="管理员数">{{ data.totalAdmins }}</el-descriptions-item>

        <el-descriptions-item label="厨师档案总数">{{ data.chefProfilesTotal }}</el-descriptions-item>
        <el-descriptions-item label="待审核厨师">{{ data.chefProfilesPending }}</el-descriptions-item>
        <el-descriptions-item label="已通过厨师">{{ data.chefProfilesApproved }}</el-descriptions-item>

        <el-descriptions-item label="订单总数">{{ data.ordersTotal }}</el-descriptions-item>
        <el-descriptions-item label="待接单">{{ data.ordersPending }}</el-descriptions-item>
        <el-descriptions-item label="已接单">{{ data.ordersAccepted }}</el-descriptions-item>

        <el-descriptions-item label="进行中">{{ data.ordersInProgress }}</el-descriptions-item>
        <el-descriptions-item label="已完成">{{ data.ordersCompleted }}</el-descriptions-item>
        <el-descriptions-item label="已取消">{{ data.ordersCancelled }}</el-descriptions-item>

        <el-descriptions-item label="已拒绝">{{ data.ordersRejected }}</el-descriptions-item>
        <el-descriptions-item label="完成订单收入(分)">{{ data.revenueCompletedCents }}</el-descriptions-item>
        <el-descriptions-item label="评价总数">{{ data.reviewsTotal }}</el-descriptions-item>

        <el-descriptions-item label="全站平均评分">{{ data.avgRatingAllReviews }}</el-descriptions-item>
        <el-descriptions-item label="今日订单">{{ data.todayOrders }}</el-descriptions-item>
        <el-descriptions-item label="今日完成订单">{{ data.todayCompletedOrders }}</el-descriptions-item>

        <el-descriptions-item label="今日收入(分)">{{ data.todayRevenueCents }}</el-descriptions-item>
        <el-descriptions-item label="-">-</el-descriptions-item>
        <el-descriptions-item label="-">-</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { getAdminStats } from "@/api/adminStats";

const data = ref({
  totalAccounts: 0,
  activeAccounts: 0,
  disabledAccounts: 0,
  totalUsers: 0,
  totalChefs: 0,
  totalAdmins: 0,
  chefProfilesTotal: 0,
  chefProfilesPending: 0,
  chefProfilesApproved: 0,
  ordersTotal: 0,
  ordersPending: 0,
  ordersAccepted: 0,
  ordersInProgress: 0,
  ordersCompleted: 0,
  ordersCancelled: 0,
  ordersRejected: 0,
  revenueCompletedCents: 0,
  todayOrders: 0,
  todayCompletedOrders: 0,
  todayRevenueCents: 0,
  reviewsTotal: 0,
  avgRatingAllReviews: 0
});

async function load() {
  try {
    const resp = await getAdminStats();
    data.value = resp.data || data.value;
  } catch (e) {
    ElMessage.error("加载统计失败");
  }
}

onMounted(load);
</script>

<style scoped>
.page { padding: 20px; }
.topbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.title { font-weight: 700; font-size: 18px; }
</style>