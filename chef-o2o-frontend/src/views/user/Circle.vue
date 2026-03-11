<template>
  <div class="page">
    <div class="topbar">
      <div class="title">交流圈</div>
      <el-button type="primary" @click="goCreate">发帖</el-button>
    </div>

    <el-card v-for="p in list" :key="p.id" class="post-card" shadow="never">
      <div class="post-header">
        <div class="avatar">{{ (p.authorUsername || "?").slice(0, 1).toUpperCase() }}</div>
        <div class="meta">
          <div class="author">{{ p.authorUsername }}</div>
          <div class="time">{{ p.createdAt || "-" }}</div>
        </div>
      </div>

      <div class="post-body">
        <div class="post-title">{{ p.title }}</div>
        <div class="post-content clamp-3">{{ p.content }}</div>
      </div>

      <div class="post-actions">
        <div class="hint">评论 {{ p.commentCount }}</div>
        <el-button type="primary" plain size="small" @click="goDetail(p)">查看评论</el-button>
      </div>
    </el-card>

    <div v-if="!list.length" class="empty">暂无帖子</div>

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
import { getCirclePosts } from "@/api/circle";

const router = useRouter();

const list = ref([]);
const total = ref(0);
const page = ref(0);
const size = ref(10);

async function load() {
  try {
    const resp = await getCirclePosts({ page: page.value, size: size.value });
    list.value = resp.data.content || [];
    total.value = resp.data.totalElements || 0;
  } catch (e) {
    ElMessage.error("加载帖子失败");
  }
}

function handlePageChange(p) {
  page.value = p - 1;
  load();
}

function goDetail(row) {
  // 不新增“帖子详情 GET by id”接口，所以靠 query 带内容（你当前后端没提供该接口示例）
  router.push({
    path: `/user/circle/${row.id}`,
    query: {
      title: row.title,
      content: row.content,
      authorUsername: row.authorUsername,
      createdAt: row.createdAt
    }
  });
}

function goCreate() {
  router.push("/user/circle/create");
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

.post-card {
  margin-bottom: 12px;
  border-radius: 10px;
}

.post-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}

.avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: #f2f2f2;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
}

.meta .author {
  font-weight: 600;
  line-height: 1.2;
}

.meta .time {
  font-size: 12px;
  color: #777;
  line-height: 1.2;
  margin-top: 2px;
}

.post-body .post-title {
  font-weight: 700;
  margin-bottom: 6px;
}

.post-body .post-content {
  color: #333;
  line-height: 1.6;
  white-space: pre-wrap;
}

.clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-actions {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.hint {
  font-size: 12px;
  color: #666;
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