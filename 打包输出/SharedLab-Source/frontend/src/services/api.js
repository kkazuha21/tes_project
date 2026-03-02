import axios from 'axios'
import { useAuthStore } from '@/store/auth'
import router from '@/router'

// 创建axios实例
const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器 - 自动附加token
api.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器 - 处理401/403错误
api.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    if (error.response) {
      const { status } = error.response
      
      // 401 Unauthorized - token过期或无效
      if (status === 401) {
        const authStore = useAuthStore()
        authStore.logout()
        router.push('/login')
      }
      
      // 403 Forbidden - 权限不足
      if (status === 403) {
        console.error('权限不足')
      }
    }
    
    return Promise.reject(error)
  }
)

export default api
