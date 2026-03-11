<template>
  <div class="page">
    <div class="topbar">
      <el-button size="small" @click="goBack">返回</el-button>
      <div class="title">帖子</div>
      <div style="width: 60px"></div>
    </div>

    <!-- 帖子正文 -->
    <el-card class="post-card" shadow="never">
      <div class="post-header">
        <div class="avatar">{{ (postAuthor || "?").slice(0, 1).toUpperCase() }}</div>
        <div class="meta">
          <div class="author">{{ postAuthor }}</div>
          <div class="time">{{ postCreatedAt }}</div>
        </div>
      </div>

      <div class="post-title">{{ postTitle }}</div>
      <div class="post-content">{{ postContent }}</div>
    </el-card>

    <!-- 评论列表 -->
    <div class="section-title">评论</div>

    <div v-if="comments.length === 0" class="empty">暂无评论</div>

    <div v-else class="comments">
      <div v-for="c in comments" :key="c.id" class="comment-row">
        <div class="c-avatar">{{ (c.authorUsername || "?").slice(0, 1).toUpperCase() }}</div>
        <div class="bubble">
          <div class="bubble-top">
            <span class="c-author">{{ c.authorUsername }}</span>
            <span class="c-time">{{ c.createdAt || "-" }}</span>
          </div>
          <div class="c-content">{{ c.content }}</div>
        </div>
      </div>
    </div>

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

    <!-- 底部输入框（INS 风格） -->
    <div class="composer">
      <el-input
          v-model="commentForm.content"
          placeholder="写评论..."
          type="textarea"
          :rows="2"
      />
      <el-button type="primary" @click="submitComment">发送</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { createCircleComment, getCircleComments } from "@/api/circle";

const route = useRoute();
const router = useRouter();

const postId = computed(() => Number(route.params.id));

// 因为你目前没提供 “GET /api/circle/posts/{id}” 的接口示例，所以正文来自 query
const postTitle = computed(() => route.query.title || `Post #${postId.value}`);
const postContent = computed(() => route.query.content || "-");
const postAuthor = computed(() => route.query.authorUsername || "-");
const postCreatedAt = computed(() => route.query.createdAt || "-");

const commentForm = ref({ content: "" });

const comments = ref([]);
const total = ref(0);
const page = ref(0);
const size = ref(10);

function goBack() {
  router.push("/user/circle");
}

async function loadComments() {
  if (!postId.value) return;

  try {
    const resp = await getCircleComments(postId.value, { page: page.value, size: size.value });
    comments.value = resp.data.content || [];
    total.value = resp.data.totalElements || 0;
  } catch (e) {
    ElMessage.error("加载评论失败");
  }
}

function handlePageChange(p) {
  page.value = p - 1;
  loadComments();
}

async function submitComment() {
  if (!postId.value) {
    ElMessage.error("参数错误：缺少 postId");
    return;
  }
  if (!commentForm.value.content || !commentForm.value.content.trim()) {
    ElMessage.error("请输入评论内容");
    return;
  }

  try {
    await createCircleComment(postId.value, { content: commentForm.value.content });
    ElMessage.success("评论成功");
    commentForm.value.content = "";

    // 发完回到第一页刷新
    page.value = 0;
    await loadComments();
  } catch (e) {
    ElMessage.error("评论失败");
  }
}

onMounted(loadComments);
</script>

<style scoped>
.page {
  padding: 20px;
  max-width: 880px;
  margin: 0 auto;
  padding-bottom: 90px; /* 给底部输入框留空间 */
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

.post-card {
  border-radius: 10px;
  margin-bottom: 14px;
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
  margin-top: 2px;
}

.post-title {
  font-weight: 800;
  margin: 6px 0 8px;
  font-size: 18px;
}

.post-content {
  white-space: pre-wrap;
  line-height: 1.6;
}

.section-title {
  font-weight: 700;
  margin: 6px 0 10px;
}

.comments {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.comment-row {
  display: flex;
  gap: 10px;
  align-items: flex-start;
}

.c-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #f2f2f2;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  flex: 0 0 auto;
}

.bubble {
  background: #f7f7f7;
  border-radius: 12px;
  padding: 10px 12px;
  flex: 1;
}

.bubble-top {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 4px;
}

.c-author {
  font-weight: 700;
}

.c-time {
  font-size: 12px;
  color: #777;
}

.c-content {
  white-space: pre-wrap;
  line-height: 1.5;
}

.empty {
  padding: 18px 0;
  color: #888;
  text-align: center;
}

.pager {
  margin-top: 14px;
  text-align: right;
}

.composer {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background: #fff;
  border-top: 1px solid #eee;
  padding: 10px 14px;
  display: flex;
  gap: 10px;
  align-items: flex-end;
}

.composer :deep(.el-textarea__inner) {
  resize: none;
}
</style>