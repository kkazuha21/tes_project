<template>
  <div class="admin-overview">
    <el-main class="main-content">
      <h2 class="page-title">
        <el-icon><Setting /></el-icon>
        管理后台
      </h2>
      
      <el-row :gutter="20" class="stats-row">
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background-color: #409eff;">
                <el-icon><Box /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.totalDevices }}</div>
                <div class="stat-label">设备总数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background-color: #67c23a;">
                <el-icon><Calendar /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.totalBookings }}</div>
                <div class="stat-label">预约总数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background-color: #e6a23c;">
                <el-icon><DocumentCopy /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.totalTickets }}</div>
                <div class="stat-label">报修总数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background-color: #f56c6c;">
                <el-icon><Warning /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.pendingTickets }}</div>
                <div class="stat-label">待处理报修</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card>
            <template #header>
              <span>快捷操作</span>
            </template>
            <div class="quick-actions">
              <el-button type="primary" @click="router.push('/admin/tickets')">
                <el-icon><DocumentCopy /></el-icon>
                管理报修单
              </el-button>
              <el-button type="success">
                <el-icon><Box /></el-icon>
                管理设备
              </el-button>
              <el-button type="warning">
                <el-icon><Calendar /></el-icon>
                查看所有预约
              </el-button>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="12">
          <el-card>
            <template #header>
              <span>最近报修单</span>
            </template>
            <el-timeline v-if="recentTickets.length > 0">
              <el-timeline-item
                v-for="ticket in recentTickets"
                :key="ticket.id"
                :timestamp="formatDateTime(ticket.createdAt)"
                placement="top"
              >
                <div class="ticket-item">
                  <span>{{ ticket.title }}</span>
                  <el-tag :type="getStatusType(ticket.status)" size="small">
                    {{ getStatusText(ticket.status) }}
                  </el-tag>
                </div>
              </el-timeline-item>
            </el-timeline>
            <el-empty v-else description="暂无报修记录" :image-size="100" />
          </el-card>
        </el-col>
      </el-row>
    </el-main>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'

const router = useRouter()

const stats = reactive({
  totalDevices: 0,
  totalBookings: 0,
  totalTickets: 0,
  pendingTickets: 0
})

const recentTickets = ref([])

const loadStats = async () => {
  try {
    const [devicesRes, bookingsRes, ticketsRes] = await Promise.all([
      api.get('/devices'),
      api.get('/bookings/all'),
      api.get('/tickets')
    ])
    
    stats.totalDevices = devicesRes.data.length
    stats.totalBookings = bookingsRes.data.length
    stats.totalTickets = ticketsRes.data.length
    stats.pendingTickets = ticketsRes.data.filter(t => 
      t.status === 'SUBMITTED' || t.status === 'PROCESSING'
    ).length
    
    recentTickets.value = ticketsRes.data.slice(0, 5)
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return new Date(datetime).toLocaleString('zh-CN')
}

const getStatusType = (status) => {
  const typeMap = {
    'SUBMITTED': 'warning',
    'PROCESSING': 'primary',
    'CLOSED': 'success',
    'REJECTED': 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'SUBMITTED': '已提交',
    'PROCESSING': '处理中',
    'CLOSED': '已关闭',
    'REJECTED': '已拒绝'
  }
  return textMap[status] || status
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.admin-overview {
  width: 100%;
  height: 100%;
}

.main-content {
  padding: 30px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 24px;
  color: #303133;
  margin-bottom: 30px;
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
  flex-direction: column;
  gap: 10px;
}

.quick-actions .el-button {
  width: 100%;
  justify-content: flex-start;
}

.ticket-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
