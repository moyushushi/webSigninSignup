<script setup>
import {EditPen, Lock, Message, User} from "@element-plus/icons-vue";
import {reactive, ref} from "vue";
import router from "@/router/index.js";
import {ElMessage} from "element-plus";
import request from "@/util/request.js";

let timer = null;

const validateUsername = (rule,value, callback) => {
  if (!value) {
    callback(new Error('请输入用户名'))
  } else {
    if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
      callback(new Error('用户名不能有特殊字符'))
    }else
      callback()
  }
}

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

const rules = {
  username:[
    {validator: validateUsername, trigger: ['blur','change']},
    {min: 3, max: 14, message: '用户名长度要大于3小于14', trigger: 'blur'}
  ],
  password:[
    {validator: validatePassword, trigger: ['blur','change']},
    {min: 3, max: 14, message: '密码长度要大于3小于14', trigger: 'blur'}
  ],
  password_repeat:[
      {validator: validatePass2, trigger: ['blur','change']},
  ],
  email:[
    {required: true,message:'邮箱不能为空',trigger: ['blur','change']},
    {type: 'email', message: '请输入可用的邮箱地址', trigger: ['blur', 'change']}
  ],
  code:[
    {required: true,message:'请输入获取的验证码',trigger: ['blur','change']}
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

const register = async () => {
  // 表单验证（Element Plus 的 validate 是异步的，但这里用回调没问题）
  formRef.value.validate(async (isValid) => {
    if (isValid) {
      try {
        const res = await request.post('/register', {
          username: form.username,
          password: form.password,
          email: form.email,
          code: form.code,
        });
        // 假设后端返回 { success: true, message: "注册成功" }
        if (res.success) {
          ElMessage.success(res.message);
          router.push('/');
        } else {
          ElMessage.warning(res.message);
        }
      } catch (error) {
        console.error('注册失败:', error);
        ElMessage.error('网络错误，请稍后重试');
      }
    } else {
      ElMessage.warning('请完整填写信息');
    }
  });
};
const validateEmail = async () => {
  // 前置校验：确保邮箱不为空且格式正确
  if (!form.email) {
    ElMessage.warning('请先输入邮箱地址');
    return;
  }
  const emailReg = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  if (!emailReg.test(form.email)) {
    ElMessage.warning('请输入正确的邮箱格式');
    return;
  }

  try {
    // 发送验证码请求，等待 Promise 结果
    const res = await request.post("/vali-register-email", {
      email: form.email
    });

    // 根据后端实际返回结构判断成功
    // 假设后端返回 { success: true, message: "验证码已发送" }
    if (res.success) {
      ElMessage.success(res.message);
      coldTime.value = 60;
      if (timer) clearInterval(timer);
      timer = setInterval(() => {
        coldTime.value--;
        if (coldTime.value <= 0) {
          clearInterval(timer);
          timer = null;
        }
      }, 1000);
    } else {
      // 如果后端返回失败信息
      ElMessage.warning(res.message || '发送失败');
    }
  } catch (error) {
    console.error('发送验证码失败:', error);
    ElMessage.error('网络错误，请稍后重试');
  }
};

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
      <el-form :model="form" :rules="rules" ref="formRef" @validate="onValidate">
        <el-form-item prop="username" >
          <el-input v-model="form.username" :maxlength="14" type="text" placeholder="用户名" style="margin-top: 10px">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" :maxlength="14" type="password" placeholder="密码" >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password_repeat">
          <el-input v-model="form.password_repeat" :maxlength="14" type="password" placeholder="重复密码" >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
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
    <div style="margin-top: 100px">
      <div>
        <el-button style="width: 270px" type="warning" @click="register" plain>立即注册</el-button>
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