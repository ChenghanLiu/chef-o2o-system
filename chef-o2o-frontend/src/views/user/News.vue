<template>
  <div class="page">
    <div class="topbar">
      <div class="title">平台资讯</div>
    </div>

    <el-card v-for="n in list" :key="n.id" class="news-card" shadow="never" @click="goDetail(n)">
      <div class="row">
        <div class="left">
          <div class="news-title">{{ n.title }}</div>
          <div class="news-meta">
            <span>发布时间：{{ n.createdAt || "-" }}</span>
            <span class="dot">·</span>
            <span>状态：{{ n.status || "-" }}</span>
          </div>
          <div class="news-preview clamp-2">{{ n.content }}</div>
        </div>

        <div class="right">
          <el-button type="primary" plain size="small" @click.stop="goDetail(n)">查看</el-button>
        </div>
      </div>
    </el-card>

    <div v-if="!list.length" class="empty">暂无资讯</div>

    <div class="pager">
      <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="size"
          :current-page="page + 1"
          @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { getNewsPage } from "@/api/news";

const router = useRouter();

const list = ref([]);
const total = ref(0);
const page = ref(0);
const size = ref(10);

async function load() {
  try {
    const resp = await getNewsPage({ page: page.value, size: size.value });
    list.value = resp.data.content || [];
    total.value = resp.data.totalElements || 0;
  } catch (e) {
    ElMessage.error("加载资讯失败");
  }
}

function handlePageChange(p) {
  page.value = p - 1;
  load();
}

function goDetail(row) {
  router.push(`/user/news/${row.id}`);
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.title {
  font-weight: 700;
  font-size: 18px;
}

.news-card {
  margin-bottom: 12px;
  border-radius: 10px;
  cursor: pointer;
}

.row {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.left {
  flex: 1;
  min-width: 0;
}

.news-title {
  font-weight: 800;
  font-size: 16px;
  margin-bottom: 6px;
}

.news-meta {
  font-size: 12px;
  color: #777;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.dot {
  opacity: 0.8;
}

.news-preview {
  color: #333;
  line-height: 1.6;
  white-space: pre-wrap;
}

.clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.right {
  display: flex;
  align-items: flex-start;
}

.empty {
  padding: 30px 0;
  text-align: center;
  color: #888;
}

.pager {
  margin-top: 16px;
  text-align: right;
}
</style>