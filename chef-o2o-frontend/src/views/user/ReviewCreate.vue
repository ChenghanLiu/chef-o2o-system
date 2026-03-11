<template>
  <div class="page">
    <el-card>
      <template #header>
        <div class="header">
          <span class="title">写评价</span>
          <el-button size="small" @click="goBack">返回</el-button>
        </div>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="订单ID">
          <el-input :model-value="String(orderId || '')" disabled />
        </el-form-item>

        <el-form-item label="评分" prop="rating">
          <el-rate v-model="form.rating" />
        </el-form-item>

        <el-form-item label="评价内容" prop="comment">
          <el-input
              v-model="form.comment"
              type="textarea"
              :rows="4"
              maxlength="500"
              show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submit">提交评价</el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { createReview } from "@/api/review";

const route = useRoute();
const router = useRouter();

const formRef = ref(null);

const orderId = computed(() => {
  const v = route.query.orderId;
  return v ? Number(v) : null;
});

const form = ref({
  rating: 5,
  comment: ""
});

const rules = {
  rating: [{ required: true, message: "请选择评分", trigger: "change" }],
  comment: [{ required: true, message: "请输入评价内容", trigger: "blur" }]
};

function goBack() {
  router.push("/user/orders");
}

async function submit() {
  if (!orderId.value) {
    ElMessage.error("缺少订单ID");
    return;
  }

  await formRef.value.validate();

  try {
    await createReview({
      orderId: orderId.value,
      rating: form.value.rating,
      comment: form.value.comment
    });

    ElMessage.success("评价提交成功");
    router.push("/user/orders");
  } catch (e) {
    ElMessage.error("评价提交失败");
  }
}
</script>

<style scoped>
.page { padding: 20px; }
.header { display: flex; justify-content: space-between; align-items: center; }
.title { font-weight: 600; }
</style>