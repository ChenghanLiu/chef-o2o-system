<template>
  <div class="page">
    <el-card class="card">
      <template #header>
        <div class="title">登录</div>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="账号（用户名或手机号）" prop="identifier">
          <el-input v-model="form.identifier" autocomplete="username" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password autocomplete="current-password" />
        </el-form-item>

        <el-button type="primary" :loading="loading" style="width:100%" @click="onSubmit">
          登录
        </el-button>
        <el-button type="text" @click="$router.push('/register')">
          没有账号？去注册
        </el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/store/auth";

const router = useRouter();
const auth = useAuthStore();

const formRef = ref();
const loading = ref(false);

const form = reactive({
  identifier: "",
  password: "",
});

const rules = {
  identifier: [{ required: true, message: "请输入账号", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
};

function redirectByRole(role) {
  if (role === "USER") return "/user/chefs";
  if (role === "CHEF") return "/chef/profile";
  if (role === "ADMIN") return "/admin/stats";
  return "/403";
}

async function onSubmit() {
  console.log("LOGIN CLICK"); // ✅ 用来验证按钮是否触发
  try {
    await formRef.value.validate();
    loading.value = true;

    await auth.login(form.identifier, form.password);

    ElMessage.success("登录成功");
    router.push(redirectByRole(auth.role));
  } catch (e) {
    // 表单校验失败也会进 catch（那是正常的）
    const msg = e?.response?.data?.message || e?.message || "登录失败";
    ElMessage.error(msg);
    console.error("LOGIN ERROR:", e);
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.page { height: 100vh; display:flex; align-items:center; justify-content:center; background:#fafafa; }
.card { width: 420px; }
.title { font-weight: 700; }
</style>
