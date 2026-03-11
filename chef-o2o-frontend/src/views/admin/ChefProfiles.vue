<template>
  <div class="page">
    <div class="topbar">
      <div class="title">厨师档案</div>

      <div class="actions">
        <el-select v-model="status" style="width: 160px" @change="handleFilterChange">
          <el-option label="待审核" value="PENDING" />
          <el-option label="已通过" value="APPROVED" />
          <el-option label="已拒绝" value="REJECTED" />
        </el-select>
        <el-select
            v-model="filterCuisineId"
            clearable
            style="width: 200px"
            placeholder="按菜系筛选"
            @change="handleFilterChange"
        >
          <el-option v-for="c in allCuisines" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </div>
    </div>

    <el-card shadow="never">
      <el-table :data="list" border style="width: 100%">
        <el-table-column prop="chefProfileId" label="档案ID" width="110" />
        <el-table-column prop="accountId" label="账号ID" width="110" />
        <el-table-column prop="username" label="用户名" width="160" />
        <el-table-column prop="phone" label="手机号" width="160" />


        <el-table-column label="状态" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'PENDING' ? 'warning' : (scope.row.status === 'APPROVED' ? 'success' : 'info')">
              {{ chefStatusLabel(scope.row.status) }}
            </el-tag>

          </template>

        </el-table-column>

        <el-table-column label="菜系" min-width="220">
          <template #default="scope">
            <span v-if="cuisineNameMap[scope.row.chefProfileId]">
              {{ cuisineNameMap[scope.row.chefProfileId] }}
            </span>
            <span v-else style="color:#999;">未加载</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="240">
          <template #default="scope">
            <el-button size="small" @click="loadChefCuisines(scope.row)">查看菜系</el-button>
            <el-button size="small" type="primary" @click="openCuisines(scope.row)">编辑菜系</el-button>
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

    <el-dialog v-model="cuisineVisible" title="编辑厨师菜系" width="600px">
      <div v-if="cuisineLoading">加载中...</div>

      <div v-else>
        <div style="margin-bottom: 10px; color:#666; font-size:12px;">
          档案ID：{{ currentChefProfileId }}
        </div>

        <el-select v-model="selectedCuisineIds" multiple filterable style="width: 100%">
          <el-option v-for="c in allCuisines" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </div>

      <template #footer>
        <el-button @click="cuisineVisible = false">取消</el-button>
        <el-button type="primary" :disabled="cuisineLoading" @click="submitCuisines">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { getAdminChefsPage, getAdminChefCuisines, updateAdminChefCuisines } from "@/api/adminChefs";
import { getAdminCuisines } from "@/api/adminCuisines";

const list = ref([]);
const total = ref(0);
const page = ref(0);
const size = ref(10);
const status = ref("APPROVED");
const filterCuisineId = ref(null);

function chefStatusLabel(s) {
  if (s === "PENDING") return "待审核";
  if (s === "APPROVED") return "已通过";
  if (s === "REJECTED") return "已拒绝";
  return s;
}

/** 用 chefProfileId 做 key：显示“菜系名称列表” */
const cuisineNameMap = ref({}); // { [chefProfileId]: "Sichuan, BBQ" }

async function load() {
  try {
    const resp = await getAdminChefsPage({ status: status.value, page: page.value, size: size.value });
    const rows = resp.data.content || [];
    total.value = resp.data.totalElements || 0;

    list.value = rows;

    if (!allCuisines.value.length) {
      const allResp = await getAdminCuisines();
      allCuisines.value = allResp.data?.content || [];
    }

    if (!filterCuisineId.value) return;

    const keep = [];
    for (const row of rows) {
      const chefProfileId = row.chefProfileId;
      if (!chefProfileId) continue;

      try {
        const cuisinesResp = await getAdminChefCuisines(chefProfileId);
        const cuisines = cuisinesResp.data || [];

        cuisineNameMap.value = {
          ...cuisineNameMap.value,
          [chefProfileId]: cuisines.map(x => x.name).join(", ")
        };

        const hit = cuisines.some(x => x.id === filterCuisineId.value);
        if (hit) keep.push(row);
      } catch (e) {
        // 单条失败跳过
      }
    }
    list.value = keep;
  } catch (e) {
    ElMessage.error("加载厨师档案失败");
    list.value = [];
    total.value = 0;
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

/** 查看菜系：加载一次并显示在表格里 */
async function loadChefCuisines(row) {
  const chefProfileId = row.chefProfileId;
  if (!chefProfileId) {
    ElMessage.error("缺少 chefProfileId");
    return;
  }
  try {
    const resp = await getAdminChefCuisines(chefProfileId);
    const arr = resp.data || [];
    cuisineNameMap.value = {
      ...cuisineNameMap.value,
      [chefProfileId]: arr.map(x => x.name).join(", ")
    };
    ElMessage.success("已加载菜系");
  } catch (e) {
    ElMessage.error("获取菜系失败");
  }
}

/** 编辑菜系弹窗 */
const cuisineVisible = ref(false);
const cuisineLoading = ref(false);
const currentChefProfileId = ref(null);
const allCuisines = ref([]);
const selectedCuisineIds = ref([]);

async function openCuisines(row) {
  const chefProfileId = row.chefProfileId;
  if (!chefProfileId) {
    ElMessage.error("缺少 chefProfileId");
    return;
  }

  cuisineVisible.value = true;
  cuisineLoading.value = true;
  currentChefProfileId.value = chefProfileId;
  selectedCuisineIds.value = [];
  allCuisines.value = [];

  try {
    const allResp = await getAdminCuisines();
    // admin cuisines 是 Page：{content:[...]}
    allCuisines.value = allResp.data?.content || [];

    const chefResp = await getAdminChefCuisines(chefProfileId);
    const chefCuisines = chefResp.data || [];
    selectedCuisineIds.value = chefCuisines.map(x => x.id);
  } catch (e) {
    ElMessage.error("获取菜系失败");
  } finally {
    cuisineLoading.value = false;
  }
}

async function submitCuisines() {
  try {
    await updateAdminChefCuisines(currentChefProfileId.value, { cuisineIds: selectedCuisineIds.value });
    ElMessage.success("保存成功");
    cuisineVisible.value = false;

    // 保存后顺手刷新表格显示（不额外请求，只用已选的 ids 映射 name）
    const names = allCuisines.value
        .filter(c => selectedCuisineIds.value.includes(c.id))
        .map(c => c.name)
        .join(", ");
    cuisineNameMap.value = { ...cuisineNameMap.value, [currentChefProfileId.value]: names };
  } catch (e) {
    ElMessage.error("保存失败");
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