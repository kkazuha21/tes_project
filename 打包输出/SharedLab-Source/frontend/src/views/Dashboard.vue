<template>
  <div class="dashboard-layout">
    <AppHeader />
    
    <el-main class="dashboard-main">
      <el-row :gutter="20">
        <el-col :span="24">
          <h2 class="page-title">
            <el-icon><HomeFilled /></el-icon>
            欢迎回来，{{ authStore.user?.username }}
          </h2>
        </el-col>
      </el-row>
      
      <el-row :gutter="20" class="stats-row">
        <el-col :span="8">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background-color: #409eff;">
                <el-icon><Box /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ deviceCount }}</div>
                <div class="stat-label">可用设备</div>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="8">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background-color: #67c23a;">
                <el-icon><Calendar /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ bookingCount }}</div>
                <div class="stat-label">我的预约</div>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="8">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background-color: #e6a23c;">
                <el-icon><DocumentCopy /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ ticketCount }}</div>
                <div class="stat-label">待处理报修</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <el-row :gutter="20">
        <el-col :span="24">
          <el-card>
            <template #header>
              <span>快速入口</span>
            </template>
            <div class="quick-actions">
              <el-button type="primary" @click="router.push('/devices')">
                <el-icon><Box /></el-icon>
                浏览设备
              </el-button>
              <el-button type="success" @click="router.push('/bookings')">
                <el-icon><Calendar /></el-icon>
                我的预约
              </el-button>
              <el-button type="warning" @click="router.push('/tickets')">
                <el-icon><DocumentCopy /></el-icon>
                提交报修
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import AppHeader from '@/components/layout/AppHeader.vue'
import api from '@/services/api'

const router = useRouter()
const authStore = useAuthStore()

const deviceCount = ref(0)
const bookingCount = ref(0)
const ticketCount = ref(0)

onMounted(async () => {
  // 获取统计数据
  try {
    const devicesRes = await api.get('/devices')
    deviceCount.value = devicesRes.data.filter(d => d.status === 'AVAILABLE').length
    
    const bookingsRes = await api.get('/bookings/my')
    // 只统计未完成的预约
    const now = new Date()
    bookingCount.value = bookingsRes.data.filter(b => {
      const endTime = new Date(b.endTime)
      return endTime > now && (b.status === 'CONFIRMED' || b.status === 'PENDING')
    }).length
    
    const ticketsRes = await api.get('/tickets/my')
    ticketCount.value = ticketsRes.data.filter(t => 
      t.status === 'SUBMITTED' || t.status === 'PROCESSING'
    ).length
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
})
</script>

<style scoped>
.dashboard-layout {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.dashboard-main {
  padding: 30px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 30px;
  font-size: 24px;
  color: #303133;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.quick-actions {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}
</style>
