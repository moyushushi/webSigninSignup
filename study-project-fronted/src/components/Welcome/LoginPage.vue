<script setup>
import {User,Lock} from "@element-plus/icons-vue";
import {reactive} from "vue";
import {ElMessage} from "element-plus";
import {post} from "@/net/index.js";
import router from "@/router/index.js";

const form = reactive({
  username: '',
  password: '',
  remember: false,
})

const login = ()=> {
  if(!form.username||!form.password)
    ElMessage.warning('请填写用户名或密码!')
  else {
    post("/login",{
      username: form.username,
      password: form.password,
      remember: form.remember,
    },(message)=>{
      ElMessage.success(message);
      router.push("/index");
        }
    )

  }

}
</script>

<template>
  <div style="text-align:center; margin: 0 20px">
    <div style=" margin-top: 150px;">
      <div style="font-size: 25px;font-weight: bold">登录</div>
      <div style="font-size: 14px;color: gray">在进入系统前请先输入用户名和密码进行登录</div>
    </div>
    <div style="margin-top: 50px;">
      <el-input v-model="form.username" type="text" placeholder="用户名或邮箱">
        <template #prefix>
          <el-icon><User /></el-icon>
        </template>
      </el-input>
      <el-input v-model="form.password" type="password" style="margin-top: 10px" placeholder="密码">
        <template #prefix>
          <el-icon><Lock /></el-icon>
        </template>
      </el-input>
    </div>
    <el-row style="margin-top: 5px;font-size: 14px">
      <el-col :span="12" style="text-align: left">
        <el-checkbox v-model="form.remember" label="记住我" size="large" />
      </el-col>
      <el-col :span="12" style="text-align: right">
        <el-link @click="router.push('/forget')">忘记密码?</el-link>
      </el-col>
    </el-row>
    <div style="margin-top: 40px;">
      <el-button @click="login()" style="width: 270px;" type="success" plain>立即登录</el-button>
    </div>
    <el-divider>
      <span style="color:gray;font-size: 13px" >没有账号</span>
    </el-divider>
    <div style="margin-top: 20px;">
      <el-button style="width: 270px;" @click="router.push('/register')" type="warning" plain>注册账号</el-button>
    </div>
  </div>
</template>

<style scoped>

</style>