<template>
  <el-result icon="error" title="404" sub-title="Not Found">
    <template #extra>
      <el-button type="primary" @click="$router.push('/')">Home</el-button>
    </template>
  </el-result>
</template>