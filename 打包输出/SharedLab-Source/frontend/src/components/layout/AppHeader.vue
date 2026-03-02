<template>
  <el-header class="app-header">
    <div class="header-left">
      <i class="ri-flask-line logo-icon"></i>
      <h1 class="logo-text">共享Lab</h1>
    </div>
    
    <el-menu
      :default-active="activeIndex"
      class="header-menu"
      mode="horizontal"
      @select="handleSelect"
      :ellipsis="false"
    >
      <el-menu-item index="/">
        <el-icon><HomeFilled /></el-icon>
        <span>首页</span>
      </el-menu-item>
      <el-menu-item index="/devices">
        <el-icon><Box /></el-icon>
        <span>设备列表</span>
      </el-menu-item>
      <el-menu-item index="/bookings">
        <el-icon><Calendar /></el-icon>
        <span>我的预约</span>
      </el-menu-item>
      <el-menu-item index="/tickets">
        <el-icon><DocumentCopy /></el-icon>
        <span>我的报修</span>
      </el-menu-item>
      <el-menu-item v-if="authStore.isAdmin" index="/admin">
        <el-icon><Setting /></el-icon>
        <span>管理后台</span>
      </el-menu-item>
    </el-menu>
    
    <div class="header-right">
      <el-dropdown @command="handleCommand">
        <span class="user-info">
          <el-icon><UserFilled /></el-icon>
          <span>{{ authStore.user?.username }}</span>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item disabled>
              <el-tag :type="roleTagType">{{ roleText }}</el-tag>
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </el-header>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const activeIndex = computed(() => route.path)

const roleText = computed(() => {
  const roleMap = {
    'ADMIN': '管理员',
    'TEACHER': '教师',
    'STUDENT': '学生'
  }
  return roleMap[authStore.user?.role] || '用户'
})

const roleTagType = computed(() => {
  const typeMap = {
    'ADMIN': 'danger',
    'TEACHER': 'warning',
    'STUDENT': 'success'
  }
  return typeMap[authStore.user?.role] || 'info'
})

const handleSelect = (key) => {
  router.push(key)
}

const handleCommand = (command) => {
  if (command === 'logout') {
    authStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  }
}
</script>

<style scoped>
.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 0 20px;
  height: 60px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo-icon {
  font-size: 32px;
  color: #409eff;
}

.logo-text {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.header-menu {
  flex: 1;
  margin: 0 40px;
  border-bottom: none;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f7fa;
}
</style>
