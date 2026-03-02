import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/services/api'

export const useAuthStore = defineStore('auth', () => {
  // State
  const token = ref(localStorage.getItem('token') || null)
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  // Getters
  const isAuthenticated = ref(!!token.value)
  const isAdmin = ref(user.value?.role === 'ADMIN')
  const isTeacher = ref(user.value?.role === 'TEACHER')
  const isStudent = ref(user.value?.role === 'STUDENT')

  // Actions
  const login = async (credentials) => {
    try {
      const response = await api.post('/auth/login', credentials)
      const data = response.data

      token.value = data.token
      user.value = {
        id: data.userId,
        username: data.username,
        email: data.email,
        role: data.role
      }

      // 保存到localStorage
      localStorage.setItem('token', data.token)
      localStorage.setItem('user', JSON.stringify(user.value))

      isAuthenticated.value = true
      isAdmin.value = user.value.role === 'ADMIN'
      isTeacher.value = user.value.role === 'TEACHER'
      isStudent.value = user.value.role === 'STUDENT'

      // 返回角色信息用于路由跳转
      return {
        success: true,
        role: user.value.role
      }
    } catch (error) {
      console.error('登录失败:', error)
      return {
        success: false,
        message: error.response?.data?.error || '登录失败，请检查用户名和密码'
      }
    }
  }

  const logout = () => {
    token.value = null
    user.value = null
    isAuthenticated.value = false
    isAdmin.value = false
    isTeacher.value = false
    isStudent.value = false

    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  const register = async (userData) => {
    try {
      const response = await api.post('/auth/register', userData)
      return { success: true, data: response.data }
    } catch (error) {
      return {
        success: false,
        message: error.response?.data?.error || '注册失败'
      }
    }
  }

  return {
    token,
    user,
    isAuthenticated,
    isAdmin,
    isTeacher,
    isStudent,
    login,
    logout,
    register
  }
})
