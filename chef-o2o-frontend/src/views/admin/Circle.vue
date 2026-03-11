<template>
  <div class="page">
    <div class="topbar">
      <div class="title">社区管理</div>

      <div class="actions">
        <el-select v-model="status" style="width: 160px" @change="handleFilterChange">
          <el-option label="全部" value="" />
          <el-option label="已发布" value="PUBLISHED" />
          <el-option label="已隐藏" value="HIDDEN" />
        </el-select>
      </div>
    </div>

    <el-card shadow="never">
      <el-table :data="list" border style="width: 100%">
        <el-table-column prop="id" label="帖子ID" width="100" />
        <el-table-column prop="authorUsername" label="作者" width="160" />
        <el-table-column prop="title" label="标题" min-width="220" />
        <el-table-column prop="commentCount" label="评论数" width="100" />

        <el-table-column label="状态" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'PUBLISHED' ? 'success' : 'info'">
              {{ CIRCLE_POST_STATUS_MAP[scope.row.status] || scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createdAt" label="创建时间" width="200" />
        <el-table-column label="操作" width="260">
          <template #default="scope">
            <el-button
                size="small"
                type="warning"
                :disabled="scope.row.status === 'HIDDEN'"
                @click="doHide(scope.row)"
            >
              隐藏
            </el-button>

            <el-button
                size="small"
                type="success"
                :disabled="scope.row.status === 'PUBLISHED'"
                @click="doPublish(scope.row)"
            >
              恢复发布
            </el-button>
          </template>
        </el-table-column>
      </el-table>

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
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { getAdminCirclePostsPage, hideCirclePost, publishCirclePost } from "@/api/adminCircle";
import { CIRCLE_POST_STATUS_MAP } from "@/constants/status";

const list = ref([]);
const total = ref(0);
const page = ref(0);
const size = ref(10);
const status = ref("");

async function load() {
  try {
    const params = { page: page.value, size: size.value };
    if (status.value) params.status = status.value;

    const resp = await getAdminCirclePostsPage(params);
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

function handleFilterChange() {
  page.value = 0;
  load();
}

async function doHide(row) {
  try {
    await ElMessageBox.confirm("确认隐藏该帖子？", "提示", { type: "warning" });
    await hideCirclePost(row.id);
    ElMessage.success("已隐藏");
    load();
  } catch (e) {}
}

async function doPublish(row) {
  try {
    await ElMessageBox.confirm("确认恢复发布该帖子？", "提示", { type: "warning" });
    await publishCirclePost(row.id);
    ElMessage.success("已发布");
    load();
  } catch (e) {}
}

onMounted(load);
</script>

<style scoped>
.page { padding: 20px; }
.topbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.title { font-weight: 700; font-size: 18px; }
.actions { display: flex; gap: 10px; align-items: center; }
.pager { margin-top: 16px; text-align: right; }
</style>