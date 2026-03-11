<template>
  <div class="page">
    <el-card>
      <template #header>
        <div class="header">
          <span class="title">个人信息</span>

          <el-button
              v-if="!editing"
              type="primary"
              size="small"
              @click="startEdit"
          >
            修改
          </el-button>
        </div>
      </template>

      <!-- 查看模式 -->
      <div v-if="!editing" class="info-view">
        <p><strong>用户名：</strong>{{ form.username }}</p>
        <p><strong>手机号：</strong>{{ form.phone }}</p>
        <p><strong>角色：</strong>{{ formatRole(form.role) }}</p>
        <p><strong>状态：</strong>{{ formatStatus(form.status) }}</p>
      </div>

      <!-- 编辑模式 -->
      <el-form
          v-else
          :model="form"
          :rules="rules"
          ref="formRef"
          label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>

        <el-form-item label="角色">
          <el-input :model-value="formatRole(form.role)" disabled />
        </el-form-item>

        <el-form-item label="状态">
          <el-input :model-value="formatStatus(form.status)" disabled />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submit">保存</el-button>
          <el-button @click="cancelEdit">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { getMyProfile, updateMyProfile } from "@/api/user";
import { ElMessage } from "element-plus";

const formRef = ref(null);
const editing = ref(false);

const form = ref({
  username: "",
  phone: "",
  role: "",
  status: ""
});

const originalData = ref({});

const rules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  phone: [{ required: true, message: "请输入手机号", trigger: "blur" }]
};

async function load() {
  try {
    const resp = await getMyProfile();
    Object.assign(form.value, resp.data);
    originalData.value = { ...resp.data };
  } catch (e) {
    ElMessage.error("加载用户信息失败");
  }
}

function startEdit() {
  editing.value = true;
}
function formatRole(role) {
  const map = {
    USER: "用户",
    CHEF: "厨师",
    ADMIN: "管理员"
  };
  return map[role] || role;
}

function formatStatus(status) {
  const map = {
    ACTIVE: "已激活",
    PENDING: "待审核",
    DISABLED: "已禁用"
  };
  return map[status] || status;
}

function cancelEdit() {
  editing.value = false;
  form.value.username = originalData.value.username;
  form.value.phone = originalData.value.phone;
}

async function submit() {
  await formRef.value.validate();

  try {
    await updateMyProfile({
      username: form.value.username,
      phone: form.value.phone
    });

    ElMessage.success("修改成功");
    editing.value = false;
    originalData.value = { ...form.value };
  } catch (e) {
    ElMessage.error("修改失败");
  }
}

onMounted(load);
</script>

<style scoped>
.page {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-weight: 600;
}

.info-view p {
  margin: 10px 0;
}
</style>