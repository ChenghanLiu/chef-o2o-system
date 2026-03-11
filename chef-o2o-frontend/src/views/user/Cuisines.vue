<template>
  <div class="page">
    <el-card>
      <template #header>
        <div class="title">菜系分类</div>
      </template>

      <el-table :data="list" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="100" />
        <el-table-column prop="name" label="菜系名称" />
        <el-table-column prop="sortOrder" label="排序" width="120" />
        <el-table-column label="操作" width="140">
          <template #default="scope">
            <el-button type="primary" size="small" @click="goChefs(scope.row)">
              查看厨师
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { listCuisines } from "@/api/cuisine";

const router = useRouter();
const list = ref([]);

async function load() {
  try {
    const resp = await listCuisines({ page: 0, size: 10 });
    list.value = Array.isArray(resp.data) ? resp.data : [];
  } catch (e) {
    ElMessage.error("加载菜系失败");
  }
}

function goChefs(row) {
  router.push(`/user/cuisines/${row.id}/chefs`);
}

onMounted(load);
</script>

<style scoped>
.page { padding: 20px; }
.title { font-weight: 600; }
</style>