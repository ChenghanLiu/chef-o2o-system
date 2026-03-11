<template>
  <div class="page">
    <el-card>
      <template #header>
        <div class="head">
          <div class="title">厨师详情</div>
          <el-button type="primary" @click="goCreateOrder">预约下单</el-button>
        </div>
      </template>

      <div v-if="loading">加载中...</div>

      <el-alert v-else-if="errorMsg" type="error" :title="errorMsg" show-icon />

      <template v-else>
        <el-descriptions v-if="chef" :column="2" border>
          <el-descriptions-item label="用户名">{{ chef.username }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ chef.phone }}</el-descriptions-item>
          <el-descriptions-item label="从业年限">{{ chef.yearsExperience }}</el-descriptions-item>
          <el-descriptions-item label="评分">{{ chef.avgRating }}</el-descriptions-item>
          <el-descriptions-item label="订单数">{{ chef.totalOrders }}</el-descriptions-item>
          <el-descriptions-item label="基础价格">{{ formatPrice(chef.basePriceCents) }}</el-descriptions-item>
          <el-descriptions-item label="服务区域">{{ chef.serviceArea }}</el-descriptions-item>
          <el-descriptions-item label="工作时间">{{ chef.workTimeDesc }}</el-descriptions-item>
          <el-descriptions-item label="简介" :span="2">{{ chef.bio }}</el-descriptions-item>
        </el-descriptions>

        <el-empty v-else description="暂无数据" />

        <el-divider />

        <div class="block">
          <div class="label">擅长菜系：</div>
          <div>
            <el-tag v-for="c in cuisines" :key="c.id" style="margin-right: 8px">
              {{ c.name }}
            </el-tag>
            <span v-if="!cuisines.length">-</span>
          </div>
        </div>

        <el-divider />

        <div class="block">
          <div class="label">用户评价：</div>

          <el-table :data="reviews" border style="width: 100%; margin-top: 10px">
            <el-table-column prop="username" label="用户" width="160" />
            <el-table-column prop="rating" label="评分" width="120" />
            <el-table-column prop="comment" label="评价内容" />
            <el-table-column prop="createdAt" label="时间" width="180" />
          </el-table>

          <div v-if="reviews.length === 0" style="margin-top: 10px">暂无评价</div>
        </div>
      </template>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";

import { getChefById } from "@/api/chef";
import { getCuisinesByChefId } from "@/api/cuisine";
import { getReviewsByChefId } from "@/api/review";

const route = useRoute();
const router = useRouter();

const chef = ref(null);
const cuisines = ref([]);
const reviews = ref([]);

const loading = ref(false);
const errorMsg = ref("");

function formatPrice(cents) {
  if (cents === null || cents === undefined) return "-";
  return "¥" + (cents / 100).toFixed(2);
}

function goCreateOrder() {
  router.push({
    path: "/user/orders/create",
    query: { chefId: String(route.params.id) }
  });
}

async function loadCuisines(chefId) {
  try {
    const resp = await getCuisinesByChefId(chefId);
    cuisines.value = Array.isArray(resp.data) ? resp.data : [];
  } catch (e) {
    ElMessage.error("加载菜系失败");
  }
}

async function loadReviews(chefId) {
  try {
    const resp = await getReviewsByChefId(chefId);
    reviews.value = Array.isArray(resp.data) ? resp.data : [];
  } catch (e) {
    ElMessage.error("加载评价失败");
  }
}

async function load() {
  loading.value = true;
  errorMsg.value = "";
  chef.value = null;
  cuisines.value = [];
  reviews.value = [];

  try {
    const id = Number(route.params.id);
    if (!id) {
      errorMsg.value = "参数错误：缺少厨师ID";
      return;
    }

    const resp = await getChefById(id);
    chef.value = resp.data;

    if (!chef.value) {
      errorMsg.value = "未获取到厨师数据";
      return;
    }

    // ✅ 用同一个 id 拉菜系与评价（接口：/api/chefs/{chefId}/cuisines、/api/reviews/chef/{chefId}）
    await loadCuisines(id);
    await loadReviews(id);
  } catch (e) {
    console.error("ChefDetail load error:", e);
    errorMsg.value = e?.response?.data?.message || e?.message || "加载失败";
    ElMessage.error(errorMsg.value);
  } finally {
    loading.value = false;
  }
}

onMounted(load);
</script>

<style scoped>
.page { padding: 20px; }
.head { display: flex; justify-content: space-between; align-items: center; }
.title { font-weight: 600; }
.block { margin: 10px 0; }
.label { font-weight: 600; margin-bottom: 6px; }
</style>