<template>
  <div class="page">
    <div class="topbar">
      <div class="title">订单管理</div>

      <div class="actions">
        <el-select v-model="status" style="width: 160px" @change="handleFilterChange">
          <el-option label="全部" value="" />
          <el-option label="待接单" value="PENDING" />
          <el-option label="已接单" value="ACCEPTED" />
          <el-option label="已拒绝" value="REJECTED" />
          <el-option label="进行中" value="IN_PROGRESS" />
          <el-option label="已完成" value="COMPLETED" />
          <el-option label="已取消" value="CANCELLED" />
        </el-select>
      </div>
    </div>

    <el-card shadow="never">
      <el-table :data="list" border style="width: 100%">
        <el-table-column prop="id" label="订单ID" width="100" />
        <el-table-column prop="userAccountId" label="用户ID" width="100" />
        <el-table-column prop="chefId" label="厨师ID" width="100" />
        <el-table-column prop="serviceItemId" label="服务项目ID" width="120" />
        <el-table-column prop="scheduledAt" label="预约时间" width="180" />
        <el-table-column prop="address" label="地址" min-width="220" />

        <el-table-column label="状态" width="120">
          <template #default="scope">
            <el-tag>
              {{ ORDER_STATUS_MAP[scope.row.status] || scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="totalPriceCents" label="金额(分)" width="120" />

        <el-table-column label="操作" width="140">
          <template #default="scope">
            <el-button size="small" @click="openStatus(scope.row)">改状态</el-button>
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

    <el-dialog v-model="statusVisible" title="修改订单状态" width="420px">
      <div style="margin-bottom: 10px; color:#666; font-size:12px;">
        订单ID：{{ currentOrderId }}
      </div>

      <el-select v-model="newStatus" style="width: 100%">
        <el-option label="待接单" value="PENDING" />
        <el-option label="已接单" value="ACCEPTED" />
        <el-option label="已拒绝" value="REJECTED" />
        <el-option label="进行中" value="IN_PROGRESS" />
        <el-option label="已完成" value="COMPLETED" />
        <el-option label="已取消" value="CANCELLED" />
      </el-select>

      <template #footer>
        <el-button @click="statusVisible = false">取消</el-button>
        <el-button type="primary" @click="submitStatus">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { getAdminOrdersPage, updateAdminOrderStatus } from "@/api/adminOrders";
import { ORDER_STATUS_MAP } from "@/constants/status";

const list = ref([]);
const total = ref(0);
const page = ref(0);
const size = ref(10);
const status = ref("");

async function load() {
  try {
    const params = { page: page.value, size: size.value };
    if (status.value) params.status = status.value;

    const resp = await getAdminOrdersPage(params);
    list.value = resp.data.content || [];
    total.value = resp.data.totalElements || 0;
  } catch (e) {
    ElMessage.error("加载订单失败");
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

const statusVisible = ref(false);
const currentOrderId = ref(null);
const newStatus = ref("PENDING");

function openStatus(row) {
  currentOrderId.value = row.id;
  newStatus.value = row.status;
  statusVisible.value = true;
}

async function submitStatus() {
  try {
    await updateAdminOrderStatus(currentOrderId.value, { status: newStatus.value });
    ElMessage.success("修改成功");
    statusVisible.value = false;
    load();
  } catch (e) {
    ElMessage.error("修改失败");
  }
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