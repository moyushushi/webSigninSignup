<script setup>
import {ElMessage} from 'element-plus';
import router from "@/router/index.js";
import {useStore} from "@/stores/index.js";
import request from "@/util/request.js";
const store = useStore()
const logout = async () => {
  try {
    const res = await request.post('/my/logout');   // 改为 POST
    if (res.success) {
      ElMessage.success(res.message);
    } else {
      ElMessage.warning(res.message);
    }
  } catch (err) {
    ElMessage.error('退出失败');
  } finally {
    store.auth.user = null;
    router.push('/');
  }
};

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