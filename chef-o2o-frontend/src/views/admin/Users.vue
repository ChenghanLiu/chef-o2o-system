<template>
  <div class="page">
    <div class="topbar">
      <div class="title">用户管理</div>
    </div>

    <el-card shadow="never">
      <el-table :data="list" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="username" label="用户名" width="180" />
        <el-table-column prop="phone" label="手机号" width="160" />

        <el-table-column label="角色" width="120">
          <template #default="scope">
            {{ ROLE_MAP[scope.row.role] || scope.row.role }}
          </template>
        </el-table-column>

        <el-table-column label="状态" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'ACTIVE' ? 'success' : 'danger'">
              {{ ACCOUNT_STATUS_MAP[scope.row.status] || scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createdAt" label="创建时间" width="200" />
        <el-table-column prop="updatedAt" label="更新时间" width="200" />

        <el-table-column label="操作" width="220">
          <template #default="scope">
            <el-button
                size="small"
                type="warning"
                :disabled="scope.row.status !== 'ACTIVE'"
                @click="doDisable(scope.row)"
            >
              禁用
            </el-button>
            <el-button
                size="small"
                type="success"
                :disabled="scope.row.status !== 'DISABLED'"
                @click="doEnable(scope.row)"
            >
              启用
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
import { getAdminUsersPage, disableUser, enableUser } from "@/api/adminUsers";
import { ACCOUNT_STATUS_MAP, ROLE_MAP } from "@/constants/status";

const list = ref([]);
const total = ref(0);
const page = ref(0);
const size = ref(10);

async function load() {
  try {
    const resp = await getAdminUsersPage({ page: page.value, size: size.value });
    list.value = resp.data.content || [];
    total.value = resp.data.totalElements || 0;
  } catch (e) {
    ElMessage.error("加载用户失败");
  }
}

function handlePageChange(p) {
  page.value = p - 1;
  load();
}

async function doDisable(row) {
  try {
    await ElMessageBox.confirm("确认禁用该账号？", "提示", { type: "warning" });
    await disableUser(row.id);
    ElMessage.success("已禁用");
    load();
  } catch (e) {}
}

async function doEnable(row) {
  try {
    await ElMessageBox.confirm("确认启用该账号？", "提示", { type: "warning" });
    await enableUser(row.id);
    ElMessage.success("已启用");
    load();
  } catch (e) {}
}

onMounted(load);
</script>

<style scoped>
.page { padding: 20px; }
.topbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.title { font-weight: 700; font-size: 18px; }
.pager { margin-top: 16px; text-align: right; }
</style>