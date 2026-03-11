<template>
  <div class="page">
    <el-card>
      <template #header>
        <div class="header">
          <span class="title">发布帖子</span>
          <el-button size="small" @click="goBack">返回</el-button>
        </div>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>

        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="6" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submit">发布</el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { createCirclePost } from "@/api/circle";

const router = useRouter();
const formRef = ref(null);

const form = ref({
  title: "",
  content: ""
});

const rules = {
  title: [{ required: true, message: "请输入标题", trigger: "blur" }],
  content: [{ required: true, message: "请输入内容", trigger: "blur" }]
};

function goBack() {
  router.push("/user/circle");
}

async function submit() {
  await formRef.value.validate();
  try {
    await createCirclePost({
      title: form.value.title,
      content: form.value.content
    });
    ElMessage.success("发布成功");
    router.push("/user/circle");
  } catch (e) {
    ElMessage.error("发布失败");
  }
}
</script>

<style scoped>
.page { padding: 20px; }
.header { display: flex; justify-content: space-between; align-items: center; }
.title { font-weight: 600; }
</style>