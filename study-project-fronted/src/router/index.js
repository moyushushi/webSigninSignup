import { createRouter, createWebHashHistory } from 'vue-router'
import {useStore} from "@/stores/index.js";

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: [
      {
          path: '/',
          name: 'welcome',
          component: () => import('@/views/WelcomeView.vue'),
          children: [
              {
                  path: '',
                  name: 'welcome-login',
                  component:() =>import('@/components/Welcome/LoginPage.vue')
              },{
                  path: 'register',
                  name: 'welcome-register',
                  component:() =>import('@/components/Welcome/RegisterPage.vue')
              },{
                  path: 'forget',
                  name: 'welcome-forgot',
                  component:() =>import('@/components/Welcome/ForgetPage.vue')
              }
          ]
      },{
            path: '/index',
            name: 'index',
            component: () => import('@/views/IndexView.vue'),
      }
  ],
})

router.beforeEach((to, from, next) => {
    const store = useStore()
    // 白名单：无需登录即可访问的页面
    const whiteList = ['/', '/register', '/forget']
    if (store.auth.user) { // 已登录
        if (whiteList.includes(to.path)) {
            next('/index') // 已登录访问白名单页面，跳转到首页
        } else {
            next() // 已登录访问其他页面（如/index），正常放行
        }
    } else { // 未登录
        if (whiteList.includes(to.path)) {
            next() // 未登录访问白名单页面，正常放行
        } else {
            next('/') // 未登录访问非白名单页面（如/index），跳转到登录页
        }
    }
})

export default router
