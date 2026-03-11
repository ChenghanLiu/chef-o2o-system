<template>
  <div class="page">
    <div class="topbar">
      <div>
        <div class="title">厨师主页</div>
        <div class="sub">查看资料与设置擅长菜系</div>
      </div>
      <el-tag v-if="chef?.status" :type="statusTag(chef.status)">
        {{ statusText(chef.status) }}
      </el-tag>
    </div>

    <el-skeleton :loading="loading" animated>
      <template #default>
        <el-alert v-if="err" type="error" :closable="false" :title="err" class="mb12" />

        <el-row :gutter="16">
          <el-col :xs="24" :lg="12">
            <el-card shadow="hover" class="card">
              <template #header>
                <div class="cardHeader">基本信息</div>
              </template>

              <el-descriptions :column="1" border>
                <el-descriptions-item label="ChefId">{{ chef?.id ?? "-" }}</el-descriptions-item>
                <el-descriptions-item label="账号ID">{{ chef?.accountId ?? "-" }}</el-descriptions-item>
                <el-descriptions-item label="用户名">{{ chef?.username ?? "-" }}</el-descriptions-item>
                <el-descriptions-item label="手机号">{{ chef?.phone ?? "-" }}</el-descriptions-item>
                <el-descriptions-item label="平均评分">{{ chef?.avgRating ?? 0 }}</el-descriptions-item>
                <el-descriptions-item label="总订单">{{ chef?.totalOrders ?? 0 }}</el-descriptions-item>
              </el-descriptions>
            </el-card>
          </el-col>

          <el-col :xs="24" :lg="12">
            <el-card shadow="hover" class="card">
              <template #header>
                <div class="cardHeader" style="display:flex;justify-content:space-between;align-items:center;">
                  <span>擅长菜系</span>
                  <div style="display:flex;gap:8px;">
                    <el-button size="small" :loading="loadingCuisines" @click="reloadCuisines">刷新</el-button>
                    <el-button type="primary" size="small" :loading="savingCuisines" @click="saveCuisines">
                      保存菜系
                    </el-button>
                  </div>
                </div>
              </template>

              <div class="hint">多选后保存（PUT /api/chefs/me/cuisines，body 为数组）</div>

              <el-form label-width="90px">
                <el-form-item label="选择菜系">
                  <el-select
                      v-model="selectedCuisineIds"
                      multiple
                      filterable
                      clearable
                      placeholder="请选择菜系"
                      style="width:100%;"
                  >
                    <el-option v-for="c in allCuisines" :key="c.id" :label="c.name" :value="c.id" />
                  </el-select>
                </el-form-item>
              </el-form>

              <div class="chips">
                <el-tag v-for="c in selectedCuisineObjects" :key="c.id" class="chip">{{ c.name }}</el-tag>
                <div v-if="selectedCuisineIds.length === 0" class="empty">暂无已选菜系</div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </template>
    </el-skeleton>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import http from "@/utils/http";

import { getChefMe, getChefMeCuisines, updateChefMeCuisines } from "@/api/chefSelf";

const loading = ref(false);
const err = ref("");

const chef = ref(null);

// 菜系（公共接口：GET /api/cuisines?page=0&size=10 ——你前面已验证）
const allCuisines = ref([]);
const selectedCuisineIds = ref([]);
const loadingCuisines = ref(false);
const savingCuisines = ref(false);

const selectedCuisineObjects = computed(() => {
  const set = new Set(selectedCuisineIds.value);
  return (allCuisines.value || []).filter((c) => set.has(c.id));
});

function statusText(s) {
  const map = { PENDING: "待审核", APPROVED: "已通过", REJECTED: "已拒绝" };
  return map[s] || s || "-";
}
function statusTag(s) {
  if (s === "APPROVED") return "success";
  if (s === "PENDING") return "warning";
  if (s === "REJECTED") return "danger";
  return "info";
}

async function loadChef() {
  const resp = await getChefMe();
  chef.value = resp.data;
}

async function loadAllCuisines() {
  const resp = await http.get("/api/cuisines", { params: { page: 0, size: 10 } });
  allCuisines.value = Array.isArray(resp.data) ? resp.data : (resp.data?.content || []);
}

async function loadMyCuisines() {
  const resp = await getChefMeCuisines();
  const arr = Array.isArray(resp.data) ? resp.data : [];
  selectedCuisineIds.value = arr.map((x) => x.id);
}

async function reloadCuisines() {
  loadingCuisines.value = true;
  try {
    await loadAllCuisines();
    await loadMyCuisines();
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e?.message || "加载菜系失败");
  } finally {
    loadingCuisines.value = false;
  }
}

async function saveCuisines() {
  savingCuisines.value = true;
  try {
    await updateChefMeCuisines(selectedCuisineIds.value); // ✅ body 数组
    ElMessage.success("菜系已保存");
    await loadMyCuisines();
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e?.message || "保存失败");
  } finally {
    savingCuisines.value = false;
  }
}

async function load() {
  loading.value = true;
  err.value = "";
  try {
    await loadChef();
    await reloadCuisines();
  } catch (e) {
    err.value = e?.response?.data?.message || e?.message || "加载失败";
  } finally {
    loading.value = false;
  }
}

onMounted(load);
</script>

<style scoped>
.page { padding: 16px; }
.topbar { display:flex; justify-content:space-between; align-items:flex-end; margin-bottom:12px; }
.title { font-size:18px; font-weight:800; }
.sub { font-size:12px; color:#909399; margin-top:4px; }
.card { border-radius: 12px; }
.cardHeader { font-weight: 700; }
.hint { font-size:12px; color:#909399; margin-bottom:10px; }
.chips { display:flex; flex-wrap:wrap; gap:8px; }
.chip { border-radius:999px; }
.empty { font-size:12px; color:#909399; }
.mb12 { margin-bottom: 12px; }
</style>