<script setup>
import {EditPen, Lock, Message, User} from "@element-plus/icons-vue";
import { reactive} from "vue";
import router from "@/router/index.js";


const form = reactive({
  username: "",
  password: "",
  password_repeat: "",
  email: "",
  code: ""
})

</script>

<template>
  <div style="text-align:center; margin: 0 20px">
    <div style=" margin-top: 150px;">
      <div style="font-size: 25px;font-weight: bold">注册新用户</div>
      <div style="font-size: 14px;color: gray">欢迎注册学习平台，请填写相关信息</div>
    </div>
    <div style="margin-top: 40px">
      <el-form>
        <el-form-item>
          <el-input v-model="form.username" type="text" placeholder="用户名" style="margin-top: 10px">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="text" placeholder="密码" >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password_repeat" type="text" placeholder="重复密码" >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.email" type="text" placeholder="电子邮件">
            <template #prefix>
              <el-icon><Message /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-row gutter="10" style="width: 100%">
            <el-col :span="17">
              <el-input v-model="form.code" type="text" placeholder="请输入邮件验证码">
                <template #prefix>
                  <el-icon><EditPen /></el-icon>
                </template>
              </el-input>
            </el-col>
            <el-col :span="5">
              <el-button type="success">获取验证码</el-button>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
    </div>
    <div style="margin-top: 100px">
      <div>
        <el-button style="width: 270px" type="warning" plain>立即注册</el-button>
      </div>
      <div style="margin-top: 20px ;font-size: 14px">
        <span style="font-size: 14px;line-height: 15px;color: gray">已有账号?</span>
        <el-link type="primary" style="translate: 0 -2px" @click="router.push('/')">立即登录</el-link>
      </div>
    </div>
  </div>


</template>

<style scoped>

</style>