import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'


// https://vite.dev/config/
export default defineConfig({
  plugins: [
      vue(),
      AutoImport({
          resolvers: [ElementPlusResolver()],
      }),
      Components({
          resolvers: [ElementPlusResolver()],
      }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
    server: {
        port: 3000,          // 前端容器端口（避开后端 8080）
        host: '0.0.0.0',     // Docker 必须设为 0.0.0.0，否则容器外无法访问
        open: false,         // 关闭自动开浏览器（容器环境无需）
        cors: true,          // 允许跨域（配合 proxy 更稳妥）
        // 配置接口代理，解决跨域（前端请求 /api 转发到后端 8080）
        proxy: {
            '/api': {          // 前端请求前缀为 /api 的接口都转发到后端
                target: 'http://java:8080', // 本地开发时的后端地址
                changeOrigin: true, // 开启跨域代理
                //rewrite: (path) => path, // 去掉 /api 前缀（根据后端实际路径调整）
                // 若后端是容器化部署，Docker 内需要用「容器名/网络别名」代替 localhost，参考第二步说明
                // target: 'http://springboot-backend:8080',
            }
        }
  },
    base: './'
})
