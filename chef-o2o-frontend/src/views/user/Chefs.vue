<template>
  <div class="page">
    <div class="page-header">
      <div class="title">厨师列表</div>
      <div class="sub">选择你喜欢的私厨</div>
    </div>

    <el-skeleton v-if="loading" :rows="6" animated />

    <el-empty v-else-if="chefs.length === 0" description="暂无厨师" />

    <div v-else class="grid">
      <el-card
          v-for="chef in chefs"
          :key="chef.id"
          shadow="hover"
          class="chef-card"
          @click="goDetail(chef.id)"
      >
        <div class="card-top">
          <img
              class="avatar"
              :src="chef.avatarUrl || defaultAvatar"
              alt="avatar"
          />

          <div class="info">
            <div class="name-row">
              <div class="name">{{ chef.username }}</div>
              <el-tag
                  v-if="chef.status === 'APPROVED'"
                  type="success"
                  size="small"
              >
                已认证
              </el-tag>
            </div>

            <div class="rating-row">
              <el-rate
                  :model-value="chef.avgRating || 0"
                  disabled
                  size="small"
              />
              <span class="orders">
                {{ chef.totalOrders || 0 }} 单
              </span>
            </div>
          </div>
        </div>

        <el-divider />

        <div class="meta">
          <div class="meta-item">
            <span class="label">服务区域：</span>
            <span>{{ chef.serviceArea || "未填写" }}</span>
          </div>

          <div class="meta-item">
            <span class="label">从业经验：</span>
            <span>
              {{ chef.yearsExperience ? chef.yearsExperience + " 年" : "未填写" }}
            </span>
          </div>

          <div class="meta-item price">
            <span class="label">起步价：</span>
            <span class="price-value">
              ￥{{ formatMoney(chef.basePriceCents) }}
            </span>
          </div>
        </div>
      </el-card>
    </div>

    <div v-if="totalPages > 1" class="pager">
      <el-pagination
          background
          layout="prev, pager, next"
          :current-page="page + 1"
          :page-size="size"
          :total="totalElements"
          @current-change="onPageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import http from "@/utils/http";
import { ElMessage } from "element-plus";

const router = useRouter();

const chefs = ref([]);
const loading = ref(false);

const page = ref(0);
const size = ref(10);
const totalElements = ref(0);
const totalPages = ref(0);

const defaultAvatar =
    "https://cdn-icons-png.flaticon.com/512/149/149071.png";

function formatMoney(cents) {
  if (!cents) return "0.00";
  return (Number(cents) / 100).toFixed(2);
}

async function load(p = page.value) {
  loading.value = true;
  try {
    const resp = await http.get("/api/chefs", {
      params: { page: p, size: size.value }
    });

    const data = resp?.data || {};
    chefs.value = data.content || [];
    totalElements.value = data.totalElements || 0;
    totalPages.value = data.totalPages || 1;
    page.value = data.number || 0;
  } catch (e) {
    ElMessage.error(
        e?.response?.data?.message || e?.message || "加载厨师失败"
    );
  } finally {
    loading.value = false;
  }
}

function goDetail(id) {
  router.push(`/user/chefs/${id}`);
}

function onPageChange(curPage) {
  const p = Math.max(0, Number(curPage) - 1);
  load(p);
}

onMounted(() => load(0));
</script>

<style scoped>
.page {
  padding: 16px;
}

.page-header {
  margin-bottom: 16px;
}

.title {
  font-size: 20px;
  font-weight: 700;
}

.sub {
  font-size: 12px;
  color: #888;
  margin-top: 4px;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.chef-card {
  cursor: pointer;
  border-radius: 14px;
  transition: all 0.2s ease;
}

.chef-card:hover {
  transform: translateY(-4px);
}

.card-top {
  display: flex;
  gap: 12px;
  align-items: center;
}

.avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  object-fit: cover;
}

.info {
  flex: 1;
}

.name-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.name {
  font-size: 16px;
  font-weight: 600;
}

.rating-row {
  margin-top: 6px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.orders {
  font-size: 12px;
  color: #888;
}

.meta {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 13px;
}

.meta-item {
  display: flex;
  justify-content: space-between;
}

.label {
  color: #666;
}

.price-value {
  font-weight: 700;
  color: #222;
}

.pager {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}
</style>