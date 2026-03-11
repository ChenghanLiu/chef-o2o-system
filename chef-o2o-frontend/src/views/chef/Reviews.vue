<template>
  <div class="page">
    <div class="topbar">
      <div>
        <div class="title">我的评价</div>
        <div class="sub">查看用户对我的评分与评论</div>
      </div>
      <el-button @click="load" :loading="loading">刷新</el-button>
    </div>

    <el-alert v-if="err" type="error" :closable="false" :title="err" class="mb12" />

    <el-skeleton :loading="loading" animated>
      <template #default>
        <el-row :gutter="12">
          <el-col v-for="r in reviews" :key="r.id" :xs="24" :md="12" :lg="8">
            <el-card class="card" shadow="hover">
              <template #header>
                <div class="cardHeader">
                  <div class="name">{{ r.username || "用户" }}</div>
                  <el-rate :model-value="r.rating || 0" disabled />
                </div>
              </template>

              <div class="comment">{{ r.comment || "-" }}</div>
              <div class="meta">
                <span>订单ID：{{ r.orderId }}</span>
                <span>{{ r.createdAt || "" }}</span>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <div v-if="reviews.length === 0" class="empty">暂无评价</div>
      </template>
    </el-skeleton>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { getMyChefReviews } from "@/api/reviewChef";

const loading = ref(false);
const err = ref("");
const reviews = ref([]);

async function load() {
  loading.value = true;
  err.value = "";
  try {
    const resp = await getMyChefReviews();
    reviews.value = Array.isArray(resp.data) ? resp.data : [];
  } catch (e) {
    err.value = e?.response?.data?.message || e?.message || "加载失败";
  } finally {
    loading.value = false;
  }
}

onMounted(load);
</script>

<style scoped>
.page { padding:16px; }
.topbar { display:flex; justify-content:space-between; align-items:flex-end; margin-bottom:12px; }
.title { font-size:18px; font-weight:800; }
.sub { font-size:12px; color:#909399; margin-top:4px; }
.mb12 { margin-bottom:12px; }
.card { border-radius:12px; }
.cardHeader { display:flex; justify-content:space-between; align-items:center; gap:10px; }
.name { font-weight:800; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
.comment { font-size:13px; line-height:1.6; }
.meta { margin-top:10px; font-size:12px; color:#909399; display:flex; justify-content:space-between; gap:10px; }
.empty { margin-top: 20px; text-align:center; color:#909399; }
</style>