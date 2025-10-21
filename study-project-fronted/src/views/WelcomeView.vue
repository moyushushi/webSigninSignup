<script setup>
</script>

<template>
  <div style="width: 100vw; height: 100vh; overflow: hidden; display:flex">
    <div style="flex: 1;background-color:red ;height:100%">
      <el-image style="width: 100%;height: 100%" fit="cover"
                src="https://image.baidu.com/search/down?url=https://tvax1.sinaimg.cn/large/ea98bce0gy1h099h4b1wwj21hc0xcqpd.jpg"/>

    </div>
    <div class="welcome-title">
      <div style="font-size: 30px;font-weight: bold">这是一个个人搭建的网页</div>
      <div style="margin-top: 10px;font-size: 30px">实现登陆注册功能</div>
      <div style="margin-top: 5px;font-size: 30px">搭建中。。。</div>

    </div>
    <div style="width: 400px;background-color: white; z-index: 1">
      <router-view v-slot="{Component}">
        <transition mode="out-in" name="el-fade-in-linear">
          <component :is="Component"></component>
        </transition>
      </router-view>

    </div>
  </div>
</template>

<style scoped>
.welcome-title{
  position: absolute;
  bottom: 30px;
  left: 30px;
  color: white;
  text-shadow: 0 0 10px black;

}
</style>