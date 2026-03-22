<script setup>

import { onErrorCaptured } from 'vue'

onErrorCaptured(() => false)

import {get} from "@/net"
import {useStore} from "@/stores/index.js";
import router from "@/router/index.js";
import {ElMessage} from "element-plus";
import request from "@/util/request.js";
const store = useStore()

if (store.auth.user==null) {
  request.get('/user/me', (message) => {
    store.auth.user=message
    router.push('/index')
  },(message) => {
        store.auth.user = null
        ElMessage.warning('未登录或登录已过期')
      },
      () => { // 捕获网络错误
        store.auth.user = null
        ElMessage.error('请求用户信息失败')
      })
}

</script>

<template>
  <router-view/>
</template>

<style scoped></style>
