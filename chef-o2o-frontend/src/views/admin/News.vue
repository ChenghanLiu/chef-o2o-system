<template>
  <div class="page">
    <div class="topbar">
      <div class="title">资讯管理</div>

      <div class="actions">
        <el-select v-model="status" placeholder="状态" style="width: 160px" @change="handleFilterChange">
          <el-option label="全部" value="" />
          <el-option label="已发布" value="PUBLISHED" />
          <el-option label="已隐藏" value="HIDDEN" />
        </el-select>

        <el-button type="primary" @click="openCreate">发布资讯</el-button>
      </div>
    </div>

    <el-card shadow="never">
      <el-table :data="list" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="title" label="标题" min-width="240" />
        <el-table-column label="状态" width="120">
          <template #default="scope">
            {{ NEWS_STATUS_MAP[scope.row.status] || scope.row.status }}
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="200" />
        <el-table-column prop="updatedAt" label="更新时间" width="200" />
        <el-table-column label="操作" width="260">
          <template #default="scope">
            <el-button size="small" @click="openEdit(scope.row)">编辑</el-button>
            <el-button
                size="small"
                type="warning"
                :disabled="scope.row.status === 'HIDDEN'"
                @click="doHide(scope.row)"
            >
              隐藏
            </el-button>
            <el-button size="small" type="danger" @click="doDelete(scope.row)">删除</el-button>
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

    <!-- 新增弹窗 -->
    <el-dialog v-model="createVisible" title="发布资讯" width="600px">
      <el-form :model="createForm" :rules="rules" ref="createRef" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="createForm.title" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="createForm.content" type="textarea" :rows="8" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCreate">发布</el-button>
      </template>
    </el-dialog>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="editVisible" title="编辑资讯" width="600px">
      <el-form :model="editForm" :rules="rules" ref="editRef" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="editForm.title" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="editForm.content" type="textarea" :rows="8" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getAdminNewsPage,
  createAdminNews,
  updateAdminNews,
  hideAdminNews,
  deleteAdminNews
} from "@/api/adminNews";
import { NEWS_STATUS_MAP } from "@/constants/status";

const list = ref([]);
const total = ref(0);
const page = ref(0);
const size = ref(10);

// 筛选："" | "PUBLISHED" | "HIDDEN"
const status = ref("");

async function load() {
  try {
    const params = { page: page.value, size: size.value };
    if (status.value) params.status = status.value;

    const resp = await getAdminNewsPage(params);
    list.value = resp.data.content || [];
    total.value = resp.data.totalElements || 0;
  } catch (e) {
    ElMessage.error("加载资讯失败");
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

/** 新增 */
const createVisible = ref(false);
const createRef = ref(null);
const createForm = ref({ title: "", content: "" });

/** 编辑 */
const editVisible = ref(false);
const editRef = ref(null);
const editForm = ref({ id: null, title: "", content: "" });

const rules = {
  title: [{ required: true, message: "请输入标题", trigger: "blur" }],
  content: [{ required: true, message: "请输入内容", trigger: "blur" }]
};

function openCreate() {
  createForm.value = { title: "", content: "" };
  createVisible.value = true;
}

async function submitCreate() {
  await createRef.value.validate();
  try {
    await createAdminNews({
      title: createForm.value.title,
      content: createForm.value.content
    });
    ElMessage.success("发布成功");
    createVisible.value = false;
    page.value = 0;
    load();
  } catch (e) {
    ElMessage.error("发布失败");
  }
}

function openEdit(row) {
  editForm.value = { id: row.id, title: row.title, content: row.content };
  editVisible.value = true;
}

async function submitEdit() {
  await editRef.value.validate();
  try {
    await updateAdminNews(editForm.value.id, {
      title: editForm.value.title,
      content: editForm.value.content
    });
    ElMessage.success("保存成功");
    editVisible.value = false;
    load();
  } catch (e) {
    ElMessage.error("保存失败");
  }
}

/** 隐藏 */
async function doHide(row) {
  try {
    await ElMessageBox.confirm("确认隐藏该资讯？", "提示", { type: "warning" });
    await hideAdminNews(row.id);
    ElMessage.success("已隐藏");
    load();
  } catch (e) {
    // 用户取消也会走这里，不提示错误
  }
}

/** 删除 */
async function doDelete(row) {
  try {
    await ElMessageBox.confirm("确认删除该资讯？删除后不可恢复。", "提示", { type: "warning" });
    await deleteAdminNews(row.id);
    ElMessage.success("删除成功");
    // 如果删完当前页空了，回退一页（简单处理）
    if (list.value.length === 1 && page.value > 0) page.value -= 1;
    load();
  } catch (e) {
    // 用户取消也会走这里，不提示错误
  }
}

onMounted(load);
</script>

<style scoped>
.page { padding: 20px; }
.topbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.title { font-weight: 700; font-size: 18px; }
.actions { display: flex; gap: 10px; align-items: center; }
.pager { margin-top: 16px; text-align: right; }
</style>