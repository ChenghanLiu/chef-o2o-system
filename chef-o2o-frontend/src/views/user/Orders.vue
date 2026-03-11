<template>
  <div class="page">
    <div class="page-header">
      <div class="left">
        <div class="title">我的订单</div>
      </div>

      <div class="right">
        <el-button type="primary" plain @click="reload" :loading="loading">刷新</el-button>
      </div>
    </div>

    <el-skeleton v-if="loading" :rows="6" animated />

    <el-alert v-else-if="errorMsg" type="error" :title="errorMsg" show-icon class="mb16" />

    <el-empty v-else-if="orders.length === 0" description="暂无订单" />

    <div v-else class="card-list">
      <el-card v-for="o in orders" :key="o.id" shadow="hover" class="order-card">
        <div class="card-top">
          <div class="top-left">
            <div class="order-title">
              <span class="order-id">订单 #{{ o.id }}</span>
              <el-tag :type="statusTagType(o.status)" effect="light" class="ml8">
                {{ statusText(o.status) }}
              </el-tag>
            </div>

            <div class="meta">
              <span class="meta-item">下单：{{ formatDateTime(o.createdAt) }}</span>
              <span class="meta-dot">·</span>
              <span class="meta-item">预约：{{ formatDateTime(o.scheduledAt) }}</span>
            </div>
          </div>

          <div class="top-right">
            <div class="price">￥{{ formatMoney(o.totalPriceCents) }}</div>
            <div class="tiny">总价</div>
          </div>
        </div>

        <el-divider />

        <el-descriptions :column="2" size="small" class="desc">
          <el-descriptions-item label="厨师">
            <span class="strong">
              {{ chefNameMap.get(o.chefId) || `厨师#${o.chefId}` }}
            </span>
            <el-button link type="primary" class="ml8" @click="goChef(o.chefId)">
              查看
            </el-button>
          </el-descriptions-item>

          <el-descriptions-item label="服务项目">
            <span class="strong">
              {{ serviceItemTitleMap.get(o.serviceItemId) || `服务#${o.serviceItemId}` }}
            </span>
          </el-descriptions-item>

          <el-descriptions-item label="地址" :span="2">
            <span>{{ o.address || "-" }}</span>
          </el-descriptions-item>

          <el-descriptions-item label="备注" :span="2">
            <span class="muted">{{ o.note || "（无）" }}</span>
          </el-descriptions-item>
        </el-descriptions>

        <div class="card-actions">
          <el-button
              size="small"
              type="primary"
              plain
              @click="toggleReviews(o)"
              :loading="reviewLoadingMap.get(o.chefId) === true"
          >
            {{ showReviewSet.has(o.id) ? "收起评价" : "查看评价" }}
          </el-button>
        </div>

        <!-- 评价区 -->
        <div v-if="showReviewSet.has(o.id)" class="review-panel">
          <el-divider content-position="left">评价</el-divider>

          <!-- 写评价（仅已完成订单） -->
          <div v-if="canReview(o)" class="review-form">
            <div class="form-title">写评价（订单 #{{ o.id }}）</div>

            <el-form label-width="80px">
              <el-form-item label="评分">
                <el-rate v-model="reviewDraft[o.id].rating" />
              </el-form-item>

              <el-form-item label="内容">
                <el-input
                    v-model="reviewDraft[o.id].comment"
                    type="textarea"
                    :rows="3"
                    placeholder="请输入评价内容"
                    maxlength="500"
                    show-word-limit
                />
              </el-form-item>

              <el-form-item>
                <el-button
                    type="primary"
                    :loading="reviewDraft[o.id].submitting"
                    @click="submitReview(o)"
                >
                  提交评价
                </el-button>
              </el-form-item>
            </el-form>
          </div>

          <el-alert
              v-else
              type="info"
              show-icon
              title="该订单未完成，暂不可评价"
              class="mb12"
          />

          <!-- 评价列表（按 chefId 拉取） -->
          <el-skeleton
              v-if="reviewLoadingMap.get(o.chefId) === true"
              :rows="3"
              animated
          />

          <el-empty
              v-else-if="(reviewsByChefMap.get(o.chefId) || []).length === 0"
              description="暂无评价"
          />

          <div v-else class="review-list">
            <div
                v-for="r in (reviewsByChefMap.get(o.chefId) || [])"
                :key="r.id"
                class="review-item"
            >
              <div class="review-head">
                <div class="who">
                  <span class="name">{{ r.username || "匿名" }}</span>
                  <span class="dot">·</span>
                  <span class="time">{{ formatDateTime(r.createdAt) }}</span>
                </div>
                <el-rate :model-value="r.rating || 0" disabled />
                <el-button
                    v-if="meUsername && r.username === meUsername"
                    link
                    type="danger"
                    size="small"
                    @click="onDeleteReview(o.chefId, r.id)"
                >
                  删除
                </el-button>

              </div>
              <div class="review-content">{{ r.comment || "-" }}</div>
              <div class="review-meta">关联订单：#{{ r.orderId }}</div>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <div v-if="!loading && totalPages > 1" class="pager">
      <el-pagination
          background
          layout="prev, pager, next"
          :current-page="page + 1"
          :page-size="size"
          :total="totalElements"
          @current-change="onPageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import http from "@/utils/http";
import { getReviewsByChefId, createReview } from "@/api/review";
import { deleteReview } from "@/api/review";
import { getMyProfile } from "@/api/user";
const router = useRouter();

const loading = ref(false);
const errorMsg = ref("");

const orders = ref([]);
const page = ref(0);
const size = ref(10);
const totalElements = ref(0);
const totalPages = ref(0);

// 映射缓存
const chefNameMap = ref(new Map());          // chefId -> username
const serviceItemTitleMap = ref(new Map());  // serviceItemId -> title

// 评论相关（一定要初始化，否则就会 has undefined / get undefined）
const showReviewSet = ref(new Set());        // orderId Set
const reviewsByChefMap = ref(new Map());     // chefId -> reviews[]
const reviewLoadingMap = ref(new Map());     // chefId -> boolean
const reviewDraft = reactive({});            // orderId -> { rating, comment, submitting }
const meUsername = ref("");


async function loadMe() {
  try {
    const resp = await getMyProfile();
    meUsername.value = resp?.data?.username || "";
  } catch {
    meUsername.value = "";
  }
}

// ===== API（只用你提供/已存在的接口）=====
function apiGetMyOrders(p) {
  return http.get("/api/orders/me", { params: { page: p, size: size.value } });
}

function apiGetChefById(chefId) {
  return http.get(`/api/chefs/${chefId}`);
}

// GET /api/service-items?chefId=xxx&page=0&size=10
function apiGetServiceItemsByChef(chefId) {
  return http.get("/api/service-items", { params: { chefId, page: 0, size: 10 } });
}

// ===== UI helpers =====
function statusText(s) {
  const map = {
    PENDING: "待确认",
    ACCEPTED: "已接单",
    IN_PROGRESS: "服务中",
    COMPLETED: "已完成",
    CANCELLED: "已取消",
    REJECTED: "已拒绝"
  };
  return map[s] || s || "-";
}

function statusTagType(s) {
  switch (s) {
    case "PENDING":
      return "warning";
    case "ACCEPTED":
      return "info";
    case "IN_PROGRESS":
      return "primary";
    case "COMPLETED":
      return "success";
    case "CANCELLED":
    case "REJECTED":
      return "danger";
    default:
      return "";
  }
}

function canReview(order) {
  return order?.status === "COMPLETED";
}

function formatMoney(cents) {
  if (cents === null || cents === undefined) return "0.00";
  return (Number(cents) / 100).toFixed(2);
}

function formatDateTime(v) {
  if (!v) return "-";
  const d = new Date(v);
  if (isNaN(d.getTime())) return String(v);
  return d.toLocaleString("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit"
  });
}

function goChef(chefId) {
  router.push(`/user/chefs/${chefId}`);
}

// ===== 名称补齐（本页最多 10 条，按页做缓存）=====
async function hydrateNamesFromOrders(list) {
  // 1) 补齐厨师名
  const chefIds = Array.from(new Set(list.map(o => o.chefId).filter(Boolean)));
  const missingChefIds = chefIds.filter(id => !chefNameMap.value.has(id));

  await Promise.all(
      missingChefIds.map(async (id) => {
        try {
          const resp = await apiGetChefById(id);
          const name = resp?.data?.username;
          chefNameMap.value.set(id, name || `厨师#${id}`);
        } catch {
          chefNameMap.value.set(id, `厨师#${id}`);
        }
      })
  );

  // 2) 补齐服务项目名：按 chefId 拉 service-items（每个 chef 一次）
  const chefNeedFetch = chefIds.filter((chefId) => {
    for (const o of list) {
      if (o.chefId === chefId && o.serviceItemId && !serviceItemTitleMap.value.has(o.serviceItemId)) {
        return true;
      }
    }
    return false;
  });

  await Promise.all(
      chefNeedFetch.map(async (chefId) => {
        try {
          const resp = await apiGetServiceItemsByChef(chefId);
          const items = resp?.data?.content || [];
          for (const it of items) {
            if (it?.id != null) {
              serviceItemTitleMap.value.set(it.id, it.title || `服务#${it.id}`);
            }
          }
        } catch {
          // ignore
        }
      })
  );

  // 3) 仍缺失的 serviceItemId：回退显示，避免空白
  for (const o of list) {
    if (o.serviceItemId != null && !serviceItemTitleMap.value.has(o.serviceItemId)) {
      serviceItemTitleMap.value.set(o.serviceItemId, `服务#${o.serviceItemId}`);
    }
  }
}

// ===== Review helpers =====
function ensureDraft(orderId) {
  if (!reviewDraft[orderId]) {
    reviewDraft[orderId] = { rating: 5, comment: "", submitting: false };
  }
}

async function loadChefReviews(chefId) {
  if (!chefId) return;
  // 已有缓存就不再请求
  if (reviewsByChefMap.value.has(chefId)) return;

  reviewLoadingMap.value.set(chefId, true);
  try {
    const resp = await getReviewsByChefId(chefId);
    const arr = Array.isArray(resp?.data) ? resp.data : [];
    reviewsByChefMap.value.set(chefId, arr);
  } catch (e) {
    reviewsByChefMap.value.set(chefId, []);
    ElMessage.error(e?.response?.data?.message || e?.message || "获取评价失败");
  } finally {
    reviewLoadingMap.value.set(chefId, false);
  }
}

async function toggleReviews(order) {
  const orderId = order?.id;
  const chefId = order?.chefId;
  if (!orderId) return;

  if (showReviewSet.value.has(orderId)) {
    showReviewSet.value.delete(orderId);
    return;
  }

  showReviewSet.value.add(orderId);
  ensureDraft(orderId);
  await loadChefReviews(chefId);
}

async function submitReview(order) {
  const orderId = order?.id;
  const chefId = order?.chefId;
  if (!orderId || !chefId) return;

  ensureDraft(orderId);
  const draft = reviewDraft[orderId];

  if (!draft.rating || draft.rating < 1) {
    ElMessage.warning("请先选择评分");
    return;
  }
  if (!draft.comment || !draft.comment.trim()) {
    ElMessage.warning("请填写评价内容");
    return;
  }

  draft.submitting = true;
  try {
    await createReview({
      orderId,
      rating: draft.rating,
      comment: draft.comment.trim()
    });
    ElMessage.success("评价提交成功");

    // 重新拉取该厨师评价（保证页面能看到新增）
    reviewsByChefMap.value.delete(chefId);
    await loadChefReviews(chefId);

    // 清空输入
    draft.comment = "";
    draft.rating = 5;
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e?.message || "提交评价失败");
  } finally {
    draft.submitting = false;
  }
}

// ===== Load =====
async function load(p = page.value) {
  loading.value = true;
  errorMsg.value = "";
  try {
    const resp = await apiGetMyOrders(p);
    const data = resp?.data || {};

    orders.value = data.content || [];
    totalElements.value = data.totalElements ?? orders.value.length;
    totalPages.value = data.totalPages ?? 1;
    page.value = data.number ?? p;

    await hydrateNamesFromOrders(orders.value);

    // 为本页订单初始化 draft，避免模板访问 reviewDraft[o.id] 报错
    for (const o of orders.value) {
      ensureDraft(o.id);
    }
  } catch (e) {
    const msg = e?.response?.data?.message || e?.message || "加载订单失败";
    errorMsg.value = msg;
    ElMessage.error(msg);
  } finally {
    loading.value = false;
  }
}

function onPageChange(curPage) {
  const p = Math.max(0, Number(curPage) - 1);
  load(p);
}

async function onDeleteReview(chefId, reviewId) {
  try {
    await deleteReview(reviewId);
    ElMessage.success("删除成功");

    const list = reviewsByChefMap.value.get(chefId) || [];
    reviewsByChefMap.value.set(
        chefId,
        list.filter(x => x.id !== reviewId)
    );
    reviewsByChefMap.value = new Map(reviewsByChefMap.value);
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e?.message || "删除失败");
  }
}

function reload() {
  load(page.value);
}

onMounted(async () => {
  await loadMe();
  await load(0);
});
</script>

<style scoped>
.page {
  padding: 16px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 16px;
}

.title {
  font-size: 18px;
  font-weight: 600;
}

.sub {
  font-size: 12px;
  color: #888;
  margin-top: 6px;
}

.mb16 {
  margin-bottom: 16px;
}

.card-list {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
}

.order-card {
  border-radius: 10px;
}

.card-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.order-title {
  display: flex;
  align-items: center;
}

.order-id {
  font-weight: 600;
}

.ml8 {
  margin-left: 8px;
}

.meta {
  margin-top: 6px;
  font-size: 12px;
  color: #888;
}

.meta-dot {
  margin: 0 8px;
  color: #bbb;
}

.price {
  font-size: 18px;
  font-weight: 700;
  text-align: right;
}

.tiny {
  font-size: 12px;
  color: #888;
  text-align: right;
  margin-top: 4px;
}

.desc :deep(.el-descriptions__label) {
  color: #666;
}

.strong {
  font-weight: 600;
}

.muted {
  color: #666;
}

.pager {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.card-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}

.review-panel {
  margin-top: 10px;
}

.review-form {
  background: #fafafa;
  border: 1px solid #eee;
  border-radius: 10px;
  padding: 12px;
  margin-bottom: 12px;
}

.form-title {
  font-weight: 600;
  margin-bottom: 10px;
}

.review-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.review-item {
  border: 1px solid #eee;
  border-radius: 10px;
  padding: 10px 12px;
  background: #fff;
}

.review-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.who {
  font-size: 12px;
  color: #666;
}

.name {
  font-weight: 600;
  color: #333;
}

.dot {
  margin: 0 6px;
  color: #bbb;
}

.time {
  color: #888;
}

.review-content {
  font-size: 13px;
  line-height: 1.5;
  color: #333;
  margin-bottom: 6px;
  white-space: pre-wrap;
}

.review-meta {
  font-size: 12px;
  color: #888;
}
</style>