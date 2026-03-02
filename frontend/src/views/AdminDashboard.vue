<template>
  <div class="admin-dashboard">
    <div style="display: flex; height: 100vh;">
      <div class="sidebar" style="width: 220px; background: white;">
        <div class="sidebar-header">
          <i class="ri-shield-star-line"></i>
          <span class="sidebar-title">🛡️ 管理员控制台</span>
        </div>
        
        <div class="user-info">
          <el-avatar :size="40">{{ userStore.user?.username?.charAt(0) }}</el-avatar>
          <div class="user-details">
            <div class="user-name">{{ userStore.user?.username }}</div>
            <div class="user-role">管理员</div>
          </div>
        </div>
        
        <el-menu
          :default-active="activeMenu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="/admin/dashboard">
            <i class="ri-dashboard-line"></i>
            <span>数据概览</span>
          </el-menu-item>
          <el-menu-item index="/admin/users">
            <i class="ri-user-settings-line"></i>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/devices">
            <i class="ri-computer-line"></i>
            <span>设备管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/bookings">
            <i class="ri-calendar-line"></i>
            <span>预约管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/tickets">
            <i class="ri-tools-line"></i>
            <span>报修管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/categories">
            <i class="ri-list-check"></i>
            <span>设备分类</span>
          </el-menu-item>
          <el-menu-item index="/admin/logs">
            <i class="ri-file-list-line"></i>
            <span>系统日志</span>
          </el-menu-item>
          <el-menu-item index="/admin/profile">
            <i class="ri-user-line"></i>
            <span>我的信息</span>
          </el-menu-item>
        </el-menu>
        
        <div class="logout-section">
          <el-button @click="handleLogout" type="danger" style="width: 100%">
            <i class="ri-logout-box-line"></i>
            退出登录
          </el-button>
        </div>
      </div>

      <div style="flex: 1; background: #f5f7fa; overflow: auto; padding: 20px;">
        <router-view :key="$route.fullPath" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useAuthStore()

const activeMenu = ref('/admin/dashboard')

onMounted(() => {
  activeMenu.value = route.path
  console.log('AdminDashboard mounted, current route:', route.path)
})

// 监听路由变化
watch(() => route.path, (newPath) => {
  console.log('Route changed to:', newPath)
  activeMenu.value = newPath
})

const handleMenuSelect = (index) => {
  console.log('Menu selected:', index)
  activeMenu.value = index
  router.push(index)
}

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('退出成功')
  router.push('/login')
}
</script>

<style scoped>
.admin-dashboard {
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
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
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
