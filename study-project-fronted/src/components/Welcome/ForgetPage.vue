<script setup>

import {EditPen, Lock, Message} from "@element-plus/icons-vue";
import {reactive, ref} from "vue";
import {post} from "@/net/index.js";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";


const active = ref(0)

const form = reactive({
  email: '',
  code: '',
  password: "",
  password_repeat: ""
})

const validatePassword = (rule,value, callback) => {
  if (!value) {
    callback(new Error('密码不能为空'))
  } else
    callback()
}

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入第二次密码'))
  } else if (value !== form.password) {
    callback(new Error("两次密码不一致！"))
  } else {
    callback()
  }
}
const rules ={
  email:[
    {required: true,message:'邮箱不能为空',trigger: ['blur','change']},
    {type: 'email', message: '请输入可用的邮箱地址', trigger: ['blur', 'change']}
  ],
  code:[
    {required: true,message:'请输入获取的验证码',trigger: ['blur','change']}
  ],
  password:[
    {validator: validatePassword, trigger: ['blur','change']},
    {min: 3, max: 14, message: '密码长度要大于3小于14', trigger: 'blur'}
  ],
  password_repeat:[
    {validator: validatePass2, trigger: ['blur','change']},
  ]
}

const isEmailValid = ref(false)
const formRef = ref()
const coldTime =ref(0)

const onValidate= (prop,isValid) => {
  if (prop === 'email') {
    isEmailValid.value = isValid;
  }
}

const validateEmail = () => {
  post("/vali-reset-email",{
    email: form.email
  },(message)=>{
    ElMessage.success(message)
    coldTime.value = 60;
    setInterval(()=>coldTime.value--,1000);
  })
}

const startReset = () => {
  formRef.value.validate((isValid)=> {
    if (isValid) {
      post('/start-reset', {
        email: form.email,
        code: form.code
      }, (message) => {
        ElMessage.success(message)
        active.value++
      })
    } else{
      ElMessage.warning('请完整填写信息')
    }
  })
}

const doRester =()=>{
  formRef.value.validate((isValid)=> {
    if (isValid) {
      post('/do-password', {
        password: form.password
      }, (message) => {
        ElMessage.success(message)
        active.value++
      })
    } else{
      ElMessage.warning('请填写新密码')
    }
  })
}

</script>

<template>
  <div style=" margin: 100px 20px;width: 90%">
    <el-steps style="max-width: 600px" :active="active" finish-status="success" align-center>
      <el-step title="验证电子邮件" finish-status="success" />
      <el-step title="设定密码" finish-status="success" />
    </el-steps>
  </div>
  <div>
    <transition name="el-fade-in-linear" mode="out-in">
      <div style="text-align:center; margin: 0 20px; height: 100%" v-if="active===0">
        <div style=" margin-top: 50px;">
          <div style="font-size: 25px;font-weight: bold">重置密码</div>
          <div style="font-size: 14px;color: gray">通过邮箱重置密码</div>
        </div>
        <div style="margin-top: 50px;">
          <el-form :model="form" :rules="rules" ref="formRef" @validate="onValidate">
            <el-form-item prop="email">
              <el-input v-model="form.email" type="text" placeholder="电子邮件">
                <template #prefix>
                  <el-icon><Message /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="code">
              <el-row gutter="10" style="width: 100%">
                <el-col :span="17" >
                  <el-input v-model="form.code" :maxlength="6" type="text" placeholder="请输入邮件验证码">
                    <template #prefix>
                      <el-icon><EditPen /></el-icon>
                    </template>
                  </el-input>
                </el-col>
                <el-col :span="5">
                  <el-button type="success" :disabled="!isEmailValid ||coldTime>0" @click="validateEmail">
                    {{coldTime>0 ? '请稍后' + coldTime +"秒" : '获取验证码'}}
                  </el-button>
                </el-col>
              </el-row>
            </el-form-item>
          </el-form>
        </div>
        <div style="margin-top: 70px;">
          <el-button type="danger" style="width: 270px" plain @click="startReset()">立即重置密码</el-button>
        </div>
        <div style="margin-top: 20px ;font-size: 14px">
          <span style="font-size: 14px;line-height: 15px;color: gray">已有账号?</span>
          <el-link type="primary" style="translate: 0 -2px" @click="router.push('/')">立即登录</el-link>
        </div>
      </div>
    </transition>
    <transition name="el-fade-in-linear" mode="out-in">
      <div style="text-align:center; margin: 0 20px" v-if="active===1">
        <div style=" margin-top: 50px;">
          <div style="font-size: 25px;font-weight: bold">重置密码</div>
          <div style="font-size: 14px;color: gray;margin-top: 20px">请填写您的新密码</div>
        </div>
        <div style="margin-top: 30px;">
          <el-form :model="form" :rules="rules" ref="formRef" @validate="onValidate">
            <el-form-item prop="password">
              <el-input v-model="form.password" :maxlength="14" type="password" placeholder="密码" >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="password_repeat">
              <el-input v-model="form.password_repeat" :maxlength="14" type="password" placeholder="重复新密码" >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-form>
        </div>
        <div style="margin-top: 70px;">
          <el-button @click="doRester()" type="danger" style="width: 270px" plain >重置密码</el-button>
        </div>
        <div style="margin-top: 20px ;font-size: 14px">
          <span style="font-size: 14px;line-height: 15px;color: gray">已有账号?</span>
          <el-link type="primary" style="translate: 0 -2px" @click="router.push('/')">立即登录</el-link>
        </div>
      </div>
    </transition>
  </div>




</template>

<style scoped>

</style>