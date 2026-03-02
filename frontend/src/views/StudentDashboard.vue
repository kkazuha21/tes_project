<template>
  <div class="student-dashboard">
    <el-container>
      <el-container>
        <!-- 侧边栏 -->
        <el-aside width="220px" class="sidebar">
          <!-- 侧边栏头部 -->
          <div class="sidebar-header">
            <i class="ri-graduation-cap-line"></i>
            <span class="sidebar-title">学生学习平台</span>
          </div>
          
          <!-- 用户信息 -->
          <div class="user-info">
            <el-avatar :size="40">{{ userStore.user?.username?.charAt(0) }}</el-avatar>
            <div class="user-details">
              <div class="user-name">{{ userStore.user?.username }}</div>
              <div class="user-role">学生</div>
            </div>
          </div>
          
          <!-- 菜单 -->
          <el-menu
            :default-active="activeMenu"
            router
            @select="handleMenuSelect"
          >
            <el-menu-item index="/student/devices">
              <i class="ri-computer-line"></i>
              <span>设备列表</span>
            </el-menu-item>
            <el-menu-item index="/student/bookings">
              <i class="ri-calendar-check-line"></i>
              <span>我的预约</span>
            </el-menu-item>
            <el-menu-item index="/student/tickets">
              <i class="ri-tools-line"></i>
              <span>我的报修</span>
            </el-menu-item>
            <el-menu-item index="/student/profile">
              <i class="ri-user-line"></i>
              <span>我的信息</span>
            </el-menu-item>
          </el-menu>
          
          <!-- 退出按钮 -->
          <div class="logout-section">
            <el-button @click="handleLogout" type="danger" style="width: 100%">
              <i class="ri-logout-box-line"></i>
              退出登录
            </el-button>
          </div>
        </el-aside>

        <!-- 主内容区 -->
        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useAuthStore()

const activeMenu = ref('/student/devices')

onMounted(() => {
  activeMenu.value = route.path
})

const handleMenuSelect = (index) => {
  activeMenu.value = index
}

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('退出成功')
  router.push('/login')
}
</script>

<style scoped>
.student-dashboard {
  height: 100vh;
  background-color: #f5f7fa;
}

.sidebar {
  background: white;
  box-shadow: 2px 0 4px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: bold;
}

.sidebar-header i {
  font-size: 24px;
}

.user-info {
  padding: 20px;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-details {
  flex: 1;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.user-role {
  font-size: 12px;
  color: #909399;
}

.el-menu {
  border-right: none;
  flex: 1;
}

.el-menu-item i {
  margin-right: 8px;
  font-size: 18px;
}

.logout-section {
  padding: 20px;
  border-top: 1px solid #e4e7ed;
}

.logout-section .el-button i {
  margin-right: 8px;
}

.main-content {
  padding: 0;
  background-color: #f5f7fa;
}
</style>
