<template>
  <div class="page">
    <div class="topbar">
      <div>
        <div class="title">我的订单</div>
        <div class="sub">查看并处理用户订单</div>
      </div>
      <el-button @click="load" :loading="loading">刷新</el-button>
    </div>

    <el-alert v-if="err" type="error" :closable="false" :title="err" class="mb12" />

    <el-skeleton :loading="loading" animated>
      <template #default>
        <el-row :gutter="12">
          <el-col v-for="o in orders" :key="o.id" :xs="24" :md="12" :lg="8">
            <el-card shadow="hover" class="card">
              <template #header>
                <div class="cardHeader">
                  <div class="name">订单 #{{ o.id }}</div>
                  <el-tag :type="statusTag(o.status)">{{ statusText(o.status) }}</el-tag>
                </div>
              </template>

              <div class="line"><span class="k">服务时间</span><span class="v">{{ o.scheduledAt || "-" }}</span></div>
              <div class="line"><span class="k">地址</span><span class="v">{{ o.address || "-" }}</span></div>
              <div class="line"><span class="k">服务项目</span><span class="v">{{ serviceItemName(o.serviceItemId) }}</span></div>
              <div class="line"><span class="k">总价</span><span class="v">{{ cents(o.totalPriceCents) }}</span></div>

              <div class="actions">
                <el-button size="small" @click="openDetail(o.id)">详情</el-button>

                <template v-if="o.status === 'PENDING'">
                  <el-button size="small" type="success" :loading="btnLoadingId===o.id" @click="doAction(o.id,'ACCEPT')">
                    接受
                  </el-button>
                  <el-button size="small" type="danger" :loading="btnLoadingId===o.id" @click="doAction(o.id,'REJECT')">
                    拒绝
                  </el-button>
                </template>

                <template v-else-if="o.status === 'ACCEPTED'">
                  <el-button size="small" type="primary" :loading="btnLoadingId===o.id" @click="doAction(o.id,'START')">
                    开始服务
                  </el-button>
                </template>

                <template v-else-if="o.status === 'IN_PROGRESS'">
                  <el-button size="small" type="primary" :loading="btnLoadingId===o.id" @click="doAction(o.id,'COMPLETE')">
                    完成
                  </el-button>
                </template>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <div class="pager">
          <el-pagination
              v-model:current-page="page1"
              :page-size="10"
              layout="prev, pager, next"
              :total="totalElements"
              @current-change="load"
          />
        </div>
      </template>
    </el-skeleton>

    <el-drawer v-model="drawerVisible" title="订单详情" size="520px">
      <el-skeleton :loading="detailLoading" animated>
        <template #default>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="订单ID">{{ detail?.id ?? "-" }}</el-descriptions-item>
            <el-descriptions-item label="用户账号ID">{{ detail?.userAccountId ?? "-" }}</el-descriptions-item>
            <el-descriptions-item label="厨师ID">{{ detail?.chefId ?? "-" }}</el-descriptions-item>
            <el-descriptions-item label="服务项目">{{ serviceItemName(detail?.serviceItemId) }}</el-descriptions-item>
            <el-descriptions-item label="状态">{{ statusText(detail?.status) }}</el-descriptions-item>
            <el-descriptions-item label="服务时间">{{ detail?.scheduledAt ?? "-" }}</el-descriptions-item>
            <el-descriptions-item label="地址">{{ detail?.address ?? "-" }}</el-descriptions-item>
            <el-descriptions-item label="备注">{{ detail?.note ?? "-" }}</el-descriptions-item>
            <el-descriptions-item label="总价">{{ cents(detail?.totalPriceCents) }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ detail?.createdAt ?? "-" }}</el-descriptions-item>
            <el-descriptions-item label="更新时间">{{ detail?.updatedAt ?? "-" }}</el-descriptions-item>
          </el-descriptions>
        </template>
      </el-skeleton>
    </el-drawer>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import { getChefOrders, getOrderDetail, chefOrderAction } from "@/api/orderChef";
import { getMyServiceItems } from "@/api/serviceItemChef";

const loading = ref(false);
const err = ref("");

const orders = ref([]);
const totalElements = ref(0);
const page1 = ref(1);
const page0 = computed(() => page1.value - 1);

const btnLoadingId = ref(null);

// 用“自己服务项目列表”做 id->title 映射（你明确说没有批量字典接口）
const serviceItemMap = ref(new Map());

function cents(v) {
  const n = Number(v || 0);
  return `¥ ${(n / 100).toFixed(2)}`;
}

function statusText(s) {
  const map = {
    PENDING: "待处理",
    ACCEPTED: "已接受",
    REJECTED: "已拒绝",
    IN_PROGRESS: "进行中",
    COMPLETED: "已完成",
    CANCELLED: "已取消",
  };
  return map[s] || s || "-";
}
function statusTag(s) {
  if (s === "PENDING") return "warning";
  if (s === "ACCEPTED") return "success";
  if (s === "IN_PROGRESS") return "primary";
  if (s === "COMPLETED") return "success";
  if (s === "REJECTED") return "danger";
  if (s === "CANCELLED") return "info";
  return "info";
}

function serviceItemName(id) {
  if (id === null || id === undefined) return "-";
  return serviceItemMap.value.get(Number(id)) || `#${id}`;
}

async function loadServiceItemsForMap() {
  try {
    const resp = await getMyServiceItems({ page: 0, size: 10 });
    const arr = resp.data?.content || [];
    const m = new Map();
    arr.forEach((it) => m.set(Number(it.id), it.title));
    serviceItemMap.value = m;
  } catch (e) {
    // 不阻断订单页面，只是映射显示退化成 #id
  }
}

async function load() {
  loading.value = true;
  err.value = "";
  try {
    await loadServiceItemsForMap();
    const resp = await getChefOrders({ page: page0.value, size: 10 });
    orders.value = resp.data?.content || [];
    totalElements.value = resp.data?.totalElements || 0;
  } catch (e) {
    err.value = e?.response?.data?.message || e?.message || "加载失败";
  } finally {
    loading.value = false;
  }
}

// detail
const drawerVisible = ref(false);
const detailLoading = ref(false);
const detail = ref(null);

async function openDetail(orderId) {
  drawerVisible.value = true;
  detailLoading.value = true;
  detail.value = null;
  try {
    const resp = await getOrderDetail(orderId);
    detail.value = resp.data;
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e?.message || "加载详情失败");
  } finally {
    detailLoading.value = false;
  }
}

async function doAction(orderId, action) {
  btnLoadingId.value = orderId;
  try {
    await chefOrderAction(orderId, action);
    ElMessage.success("操作成功");
    await load();
    // 若抽屉打开的是同一个订单，顺便刷新详情
    if (drawerVisible.value && detail.value?.id === orderId) {
      await openDetail(orderId);
    }
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e?.message || "操作失败");
  } finally {
    btnLoadingId.value = null;
  }
}

onMounted(load);
</script>

<style scoped>
.page { padding:16px; }
.topbar { display:flex; justify-content:space-between; align-items:flex-end; margin-bottom:12px; }
.title { font-size:18px; font-weight:800; }
.sub { font-size:12px; color:#909399; margin-top:4px; }
.mb12 { margin-bottom:12px; }
.card { border-radius:12px; }
.cardHeader { display:flex; justify-content:space-between; align-items:center; gap:10px; }
.name { font-weight:800; }
.line { display:flex; justify-content:space-between; padding:6px 0; gap:10px; }
.k { color:#909399; font-size:12px; flex:0 0 auto; }
.v { font-size:13px; text-align:right; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
.actions { display:flex; flex-wrap:wrap; gap:8px; margin-top:12px; }
.pager { display:flex; justify-content:center; margin-top: 14px; }
</style>