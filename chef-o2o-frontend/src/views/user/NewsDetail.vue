<template>
  <div class="page">
    <div class="topbar">
      <el-button size="small" @click="goBack">返回</el-button>
      <div class="title">资讯详情</div>
      <div style="width: 60px"></div>
    </div>

    <el-card class="detail-card" shadow="never">
      <div v-if="loading">加载中...</div>
      <el-alert v-else-if="errorMsg" type="error" :title="errorMsg" show-icon />

      <template v-else>
        <div class="headline">{{ detail.title }}</div>
        <div class="meta">
          <span>发布时间：{{ detail.createdAt || "-" }}</span>
          <span class="dot">·</span>
          <span>状态：{{ detail.status || "-" }}</span>
        </div>

        <el-divider />

        <div class="content">{{ detail.content }}</div>
      </template>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { getNewsById } from "@/api/news";

const route = useRoute();
const router = useRouter();

const loading = ref(false);
const errorMsg = ref("");
const detail = ref({
  id: null,
  title: "",
  content: "",
  status: "",
  createdAt: "",
  updatedAt: ""
});

function goBack() {
  router.push("/user/news");
}

async function load() {
  loading.value = true;
  errorMsg.value = "";

  try {
    const id = Number(route.params.id);
    if (!id) {
      errorMsg.value = "参数错误：缺少资讯ID";
      return;
    }
    const resp = await getNewsById(id);
    detail.value = resp.data;
  } catch (e) {
    errorMsg.value = e?.response?.data?.message || e?.message || "加载失败";
    ElMessage.error(errorMsg.value);
  } finally {
    loading.value = false;
  }
}

onMounted(load);
</script>

<style scoped>
.page {
  padding: 20px;
  max-width: 880px;
  margin: 0 auto;
}

.topbar {
  display: grid;
  grid-template-columns: 60px 1fr 60px;
  align-items: center;
  margin-bottom: 12px;
}

.title {
  text-align: center;
  font-weight: 700;
}

.detail-card {
  border-radius: 10px;
}

.headline {
  font-size: 20px;
  font-weight: 800;
  line-height: 1.3;
}

.meta {
  margin-top: 10px;
  font-size: 12px;
  color: #777;
  display: flex;
  align-items: center;
  gap: 6px;
}

.dot {
  opacity: 0.8;
}

.content {
  white-space: pre-wrap;
  line-height: 1.7;
  color: #222;
}
</style>