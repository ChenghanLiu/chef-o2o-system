<template>
  <div class="page">
    <div class="topbar">
      <div class="title">菜系管理</div>
      <el-button type="primary" @click="openCreate">新增菜系</el-button>
    </div>

    <el-card shadow="never">
      <el-table :data="list" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="100" />
        <el-table-column prop="name" label="名称" min-width="200" />
        <el-table-column prop="sortOrder" label="排序" width="120" />
        <el-table-column label="操作" width="220">
          <template #default="scope">
            <el-button size="small" @click="openEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="doDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="!list.length" class="empty">暂无菜系</div>
    </el-card>

    <el-dialog v-model="visible" :title="mode === 'create' ? '新增菜系' : '编辑菜系'" width="520px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" :step="1" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { getAdminCuisines, createAdminCuisine, updateAdminCuisine, deleteAdminCuisine } from "@/api/adminCuisines";

const list = ref([]);

async function load() {
  try {
    const resp = await getAdminCuisines();
    const data = resp.data;

    // 兼容：数组 / Spring Page / 包了一层 content
    if (Array.isArray(data)) {
      list.value = data;
    } else if (data && Array.isArray(data.content)) {
      list.value = data.content;
    } else {
      list.value = [];
    }
  } catch (e) {
    ElMessage.error("加载菜系失败");
    list.value = [];
  }
}

onMounted(load);

const visible = ref(false);
const mode = ref("create"); // create | edit
const formRef = ref(null);
const form = ref({ id: null, name: "", sortOrder: 0 });

const rules = {
  name: [{ required: true, message: "请输入名称", trigger: "blur" }],
  sortOrder: [{ required: true, message: "请输入排序", trigger: "blur" }]
};

function openCreate() {
  mode.value = "create";
  form.value = { id: null, name: "", sortOrder: 0 };
  visible.value = true;
}

function openEdit(row) {
  mode.value = "edit";
  form.value = { id: row.id, name: row.name, sortOrder: row.sortOrder };
  visible.value = true;
}

async function submit() {
  await formRef.value.validate();
  try {
    if (mode.value === "create") {
      await createAdminCuisine({ name: form.value.name, sortOrder: form.value.sortOrder });
      ElMessage.success("新增成功");
    } else {
      await updateAdminCuisine(form.value.id, { name: form.value.name, sortOrder: form.value.sortOrder });
      ElMessage.success("保存成功");
    }
    visible.value = false;
    load();
  } catch (e) {
    ElMessage.error("操作失败");
  }
}

async function doDelete(row) {
  try {
    await ElMessageBox.confirm("确认删除该菜系？", "提示", { type: "warning" });
    await deleteAdminCuisine(row.id);
    ElMessage.success("删除成功");
    load();
  } catch (e) {}
}
</script>

<style scoped>
.page { padding: 20px; }
.topbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.title { font-weight: 700; font-size: 18px; }
.empty { padding: 20px 0; text-align: center; color: #888; }
</style>