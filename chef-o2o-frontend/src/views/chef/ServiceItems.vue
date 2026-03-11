<template>
  <div class="page">
    <div class="topbar">
      <div>
        <div class="title">服务项目</div>
        <div class="sub">管理我的服务项目（上架/下架/编辑）</div>
      </div>
      <el-button type="primary" @click="openCreate">新增服务项目</el-button>
    </div>

    <el-alert v-if="err" type="error" :closable="false" :title="err" class="mb12" />

    <el-skeleton :loading="loading" animated>
      <template #default>
        <el-row :gutter="12">
          <el-col v-for="it in items" :key="it.id" :xs="24" :md="12" :lg="8">
            <el-card shadow="hover" class="card">
              <template #header>
                <div class="cardHeader">
                  <div class="name">{{ it.title }}</div>
                  <el-tag :type="it.status === 'ACTIVE' ? 'success' : 'info'">
                    {{ it.status === "ACTIVE" ? "上架" : "下架" }}
                  </el-tag>
                </div>
              </template>

              <div class="line"><span class="k">时长</span><span class="v">{{ it.durationMinutes }} 分钟</span></div>
              <div class="line"><span class="k">价格</span><span class="v">{{ cents(it.priceCents) }}</span></div>
              <div class="line"><span class="k">ID</span><span class="v">{{ it.id }}</span></div>

              <div class="actions">
                <el-button size="small" @click="openEdit(it)">编辑</el-button>
                <el-button
                    v-if="it.status === 'ACTIVE'"
                    size="small"
                    type="warning"
                    :loading="btnLoadingId === it.id"
                    @click="doDeactivate(it)"
                >
                  下架
                </el-button>
                <el-button
                    v-else
                    size="small"
                    type="success"
                    :loading="btnLoadingId === it.id"
                    @click="doActivate(it)"
                >
                  上架
                </el-button>
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

    <!-- Dialog -->
    <el-dialog v-model="dialogVisible" :title="dialogMode === 'create' ? '新增服务项目' : '编辑服务项目'" width="520px">
      <el-form label-width="90px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="例如：上门私厨" />
        </el-form-item>
        <el-form-item label="时长(分钟)">
          <el-input-number v-model="form.durationMinutes" :min="1" :step="10" style="width:100%;" />
        </el-form-item>
        <el-form-item label="价格(分)">
          <el-input-number v-model="form.priceCents" :min="0" :step="100" style="width:100%;" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import {
  getMyServiceItems,
  createMyServiceItem,
  updateMyServiceItem,
  activateMyServiceItem,
  deactivateMyServiceItem,
} from "@/api/serviceItemChef";

const loading = ref(false);
const err = ref("");

const items = ref([]);
const totalElements = ref(0);

const page1 = ref(1);
const page0 = computed(() => page1.value - 1);

const btnLoadingId = ref(null);

// dialog
const dialogVisible = ref(false);
const dialogMode = ref("create"); // create | edit
const saving = ref(false);
const editingId = ref(null);

const form = reactive({
  title: "",
  durationMinutes: 120,
  priceCents: 19900,
});

function cents(v) {
  const n = Number(v || 0);
  return `¥ ${(n / 100).toFixed(2)}`;
}

function openCreate() {
  dialogMode.value = "create";
  editingId.value = null;
  form.title = "";
  form.durationMinutes = 120;
  form.priceCents = 19900;
  dialogVisible.value = true;
}

function openEdit(it) {
  dialogMode.value = "edit";
  editingId.value = it.id;
  form.title = it.title ?? "";
  form.durationMinutes = it.durationMinutes ?? 120;
  form.priceCents = it.priceCents ?? 0;
  dialogVisible.value = true;
}

async function load() {
  loading.value = true;
  err.value = "";
  try {
    const resp = await getMyServiceItems({ page: page0.value, size: 10 });
    items.value = resp.data?.content || [];
    totalElements.value = resp.data?.totalElements || 0;
  } catch (e) {
    err.value = e?.response?.data?.message || e?.message || "加载失败";
  } finally {
    loading.value = false;
  }
}

async function save() {
  saving.value = true;
  try {
    if (dialogMode.value === "create") {
      await createMyServiceItem({
        title: form.title,
        durationMinutes: form.durationMinutes,
        priceCents: form.priceCents,
      });
      ElMessage.success("已创建");
    } else {
      await updateMyServiceItem(editingId.value, {
        title: form.title,
        durationMinutes: form.durationMinutes,
        priceCents: form.priceCents,
      });
      ElMessage.success("已更新");
    }
    dialogVisible.value = false;
    await load();
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e?.message || "保存失败");
  } finally {
    saving.value = false;
  }
}

async function doDeactivate(it) {
  btnLoadingId.value = it.id;
  try {
    await deactivateMyServiceItem(it.id);
    ElMessage.success("已下架");
    await load();
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || e?.message || "操作失败");
  } finally {
    btnLoadingId.value = null;
  }
}

async function doActivate(it) {
  btnLoadingId.value = it.id;
  try {
    await activateMyServiceItem(it.id);
    ElMessage.success("已上架");
    await load();
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
.mb12 { margin-bottom: 12px; }
.card { border-radius: 12px; }
.cardHeader { display:flex; justify-content:space-between; align-items:center; gap:10px; }
.name { font-weight:800; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
.line { display:flex; justify-content:space-between; padding:6px 0; }
.k { color:#909399; font-size:12px; }
.v { font-size:13px; }
.actions { display:flex; gap:8px; margin-top:12px; }
.pager { display:flex; justify-content:center; margin-top: 14px; }
</style>