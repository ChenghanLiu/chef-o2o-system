<template>
  <div class="page">
    <div class="topbar">
      <div class="title">厨师审核</div>
    </div>

    <el-card shadow="never">
      <el-table :data="list" border style="width: 100%">
        <el-table-column prop="id" label="ChefID" width="100" />
        <el-table-column prop="username" label="用户名" width="160" />
        <el-table-column prop="phone" label="手机号" width="160" />

        <el-table-column label="状态" width="120">
          <template #default="scope">
            <el-tag type="warning">
              待审核
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="160">
          <template #default="scope">
            <el-button size="small" type="success" @click="doApprove(scope.row)">通过</el-button>
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
import { getAdminChefsPage, approveChef } from "@/api/adminChefs";

const list = ref([]);
const total = ref(0);
const page = ref(0);
const size = ref(10);

async function load() {
  try {
    const resp = await getAdminChefsPage({ status: "PENDING", page: page.value, size: size.value });
    list.value = resp.data.content || [];
    total.value = resp.data.totalElements || 0;
  } catch (e) {
    ElMessage.error("加载待审核厨师失败");
  }
}

function handlePageChange(p) {
  page.value = p - 1;
  load();
}

async function doApprove(row) {
  try {
    await ElMessageBox.confirm("确认通过该厨师审核？", "提示", { type: "warning" });
    await approveChef(row.id);
    ElMessage.success("已通过");
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