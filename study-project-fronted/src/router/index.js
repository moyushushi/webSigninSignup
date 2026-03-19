import { createRouter, createWebHistory } from 'vue-router'
import {useStore} from "@/stores/index.js";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
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
    if (store.auth.user!=null && to.path !== '/index'){
        next('/index')
    }else if(store.auth.user===null && to.fullPath.startsWith('/index')){
        next('/')
    }else if(to.matched.length===0){
        next('/index')
    }else
        next()
})

export default router
