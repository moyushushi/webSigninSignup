<script setup>

import { onErrorCaptured } from 'vue'

onErrorCaptured(() => false)

import {get} from "@/net"
import {useStore} from "@/stores/index.js";
import router from "@/router/index.js";
const store = useStore()

if (store.auth.user==null) {
  get('user/me', (message) => {
    store.auth.user=message
    router.push('/index')
  }, () => {
    store.auth.user=null;
  })
}

</script>

<template>
  <router-view/>
</template>

<style scoped></style>
