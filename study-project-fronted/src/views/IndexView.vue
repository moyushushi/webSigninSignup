<script setup>
import {ElMessage} from 'element-plus';
import router from "@/router/index.js";
import {get} from "@/net/index.js";

const logout=() =>{
  get("/my/logout",(message) =>{
    ElMessage.success(message)
    router.push('/');
  })
}

</script>

<template>
  <div style="text-align:center; margin: 0 20px">
    欢迎进入到学习平台
  </div>
  <div>
    <el-button @click="logout()" style="text-align:center;margin: 0 20px" type="danger" plain>退出登录</el-button>
  </div>
</template>

<style scoped>

</style>