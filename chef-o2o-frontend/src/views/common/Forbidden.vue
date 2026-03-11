<template>
  <el-result icon="warning" title="403" sub-title="Forbidden">
    <template #extra>
      <el-button type="primary" @click="$router.push('/login')">Go Login</el-button>
    </template>
  </el-result>
</template>