<template>
  <div class="page">
    <el-card>
      <template #header>
        <div class="header">
          <span class="title">菜系厨师列表（菜系ID：{{ cuisineId }}）</span>
          <el-button size="small" @click="goBack">返回菜系</el-button>
        </div>
      </template>

      <el-table :data="list" border style="width: 100%">
        <el-table-column prop="id" label="厨师ID" width="100" />
        <el-table-column prop="username" label="姓名" width="160" />
        <el-table-column prop="phone" label="电话" width="160" />
        <el-table-column prop="avgRating" label="评分" width="120" />
        <el-table-column prop="totalOrders" label="订单数" width="120" />
        <el-table-column prop="status" label="状态" width="140" />
        <el-table-column label="操作" width="140">
          <template #default="scope">
            <el-button type="primary" size="small" @click="goDetail(scope.row)">
              查看详情
            </el-button>
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { listChefs } from "@/api/chef"; // 你已有的 /api/chefs 封装

const route = useRoute();
const router = useRouter();

const cuisineId = computed(() => Number(route.params.id));

const list = ref([]);
const total = ref(0);
const page = ref(0);
const size = ref(10);

async function load() {
  if (!cuisineId.value) return;

  try {
    const resp = await listChefs({
      cuisineId: cuisineId.value,
      page: page.value,
      size: size.value
    });

    list.value = resp.data.content || [];
    total.value = resp.data.totalElements || 0;
  } catch (e) {
    ElMessage.error("加载厨师失败");
  }
}

function handlePageChange(p) {
  page.value = p - 1;
  load();
}

function goDetail(row) {
  router.push(`/user/chefs/${row.id}`);
}

function goBack() {
  router.push("/user/cuisines");
}

onMounted(load);

// 切换菜系 id 时重置分页并重新加载
watch(
    () => cuisineId.value,
    () => {
      page.value = 0;
      load();
    }
);
</script>

<style scoped>
.page { padding: 20px; }
.header { display: flex; justify-content: space-between; align-items: center; }
.title { font-weight: 600; }
.pager { margin-top: 20px; text-align: right; }
</style>