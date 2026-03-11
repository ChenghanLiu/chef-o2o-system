<template>
  <div class="register-container">
    <el-card class="card">
      <h2>注册</h2>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input type="password" v-model="form.password" show-password />
        </el-form-item>

        <!-- ✅ 只允许 USER / CHEF -->
        <el-form-item label="注册角色" prop="role">
          <el-select v-model="form.role" style="width: 100%">
            <el-option label="用户（下单预约）" value="USER" />
            <el-option label="厨师（需审核）" value="CHEF" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submit">注册</el-button>
          <el-button @click="goLogin">返回登录</el-button>
        </el-form-item>
      </el-form>

      <!-- 不做增强，只给一句静态说明 -->
      <div class="hint">提示：厨师注册后需要管理员审核通过才能接单。</div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { register } from "@/api/auth";
import { ElMessage } from "element-plus";

const router = useRouter();
const formRef = ref(null);

const form = ref({
  username: "",
  phone: "",
  password: "",
  role: "USER", // 默认 USER
});

const rules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  phone: [{ required: true, message: "请输入手机号", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
  role: [{ required: true, message: "请选择注册角色", trigger: "change" }],
};

async function submit() {
  await formRef.value.validate();
  try {
    await register(form.value);

    if (form.value.role === "CHEF") {
      ElMessage.success("注册成功：已提交审核（PENDING），请等待管理员审核");
    } else {
      ElMessage.success("注册成功，请登录");
    }
    router.push("/login");
  } catch (e) {
    ElMessage.error("注册失败");
  }
}

function goLogin() {
  router.push("/login");
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}
.card {
  width: 420px;
}
.hint {
  margin-top: 10px;
  font-size: 12px;
  color: #888;
}
</style>