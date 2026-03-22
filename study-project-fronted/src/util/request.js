import axios from 'axios'
import router from "@/router/index.js";
import {ElMessage} from "element-plus";


const request = axios.create({
    baseURL: '/api', // 自动给请求加 /api 前缀（核心！）
    timeout: 5000,
    withCredentials: true, // 必须加：适配后端 HttpOnly Cookie 鉴权
    headers: {
        'Content-Type': 'application/json;charset=utf-8'
    }
})

// 移除「手动加 Token」的逻辑（Token 在 Cookie 里，浏览器自动带）
request.interceptors.request.use(
    (config) => config,
    (error) => Promise.reject(error)
)

// 响应拦截器（处理 401/404 等报错）
request.interceptors.response.use(
    (response) => response.data,
    (error) => {
        if (error.response?.status === 401) {
            ElMessage.error('登录已过期，请重新登录！')
            // 清空用户信息，跳登录页
            router.push('/').then(r =>{
                console.log(r)
            } )
        }
        return Promise.reject(error)
    }
)

export default request


