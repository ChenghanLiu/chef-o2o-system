<template>
  <div class="page">
    <el-card>
      <template #header>
        <div class="title">创建订单</div>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="厨师ID" prop="chefId">
          <el-input v-model="form.chefId" disabled />
        </el-form-item>

        <el-form-item label="服务项目" prop="serviceItemId">
          <el-select
              v-model="form.serviceItemId"
              placeholder="请选择服务项目"
              style="width: 100%"
              :loading="loadingItems"
          >
            <el-option
                v-for="item in serviceItems"
                :key="item.id"
                :label="serviceItemLabel(item)"
                :value="item.id"
            />
            <el-option
                v-for="item in serviceItems"
                :key="item.id"
                :label="item.title + ' - ¥' + (item.priceCents/100).toFixed(2)"
                :value="item.id"
            />
          </el-select>

        </el-form-item>


        <el-form-item label="预约时间" prop="scheduledAt">
          <el-date-picker
              v-model="form.scheduledAt"
              type="datetime"
              value-format="YYYY-MM-DDTHH:mm:ss"
              style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="服务地址" prop="address">
          <el-input v-model="form.address" />
        </el-form-item>

        <el-form-item label="备注" prop="note">
          <el-input type="textarea" v-model="form.note" />
        </el-form-item>

        <el-button type="primary" :loading="submitting" @click="submit">
          提交订单
        </el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { getServiceItems } from "@/api/serviceItem";
import { createOrder } from "@/api/order";

const route = useRoute();
const router = useRouter();

/**
 * 统一从 query 里拿 chefId
 * 你之前是 route.query.chefId
 */
const chefId = Number(route.query.chefId);

const formRef = ref(null);
const loadingItems = ref(false);
const submitting = ref(false);

const serviceItems = ref([]); // ✅ 必须存在

// 表单
const form = reactive({
  chefId: chefId,
  serviceItemId: null,
  scheduledAt: "",
  address: "",
  note: ""
});

const rules = {
  chefId: [{ required: true, message: "缺少厨师ID", trigger: "change" }],
  serviceItemId: [{ required: true, message: "请选择服务项目", trigger: "change" }],
  scheduledAt: [{ required: true, message: "请选择预约时间", trigger: "change" }],
  address: [{ required: true, message: "请输入服务地址", trigger: "blur" }]
};
function priceText(cents) {
  if (cents === null || cents === undefined) return "-";
  return "¥" + (cents / 100).toFixed(2);
}

function serviceItemLabel(item) {
  // 字段严格用你给的：title / durationMinutes / priceCents
  return `${item.title} | ${item.durationMinutes}分钟 | ${priceText(item.priceCents)}`;
}

async function loadServiceItems() {
  if (!chefId) return;

  loadingItems.value = true;
  try {
    const resp = await getServiceItems({
      chefId: chefId,
      page: 0,
      size: 10
    });

    serviceItems.value = resp.data?.content || [];
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || "加载服务项目失败");
  } finally {
    loadingItems.value = false;
  }
}

async function submit() {
  try {
    await formRef.value.validate();
    submitting.value = true;

    await createOrder({
      chefId: form.chefId,
      serviceItemId: form.serviceItemId,
      scheduledAt: form.scheduledAt,
      address: form.address,
      note: form.note
    });

    ElMessage.success("订单创建成功");
    router.push("/user/orders");
  } catch (e) {
    // 表单校验失败也会进来
    const msg = e?.response?.data?.message || e?.message || "创建订单失败";
    ElMessage.error(msg);
  } finally {
    submitting.value = false;
  }
}

onMounted(loadServiceItems);
</script>

<style scoped>
.page { padding: 20px; }
.title { font-weight: 600; }
</style>