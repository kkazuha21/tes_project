<template>
  <div class="my-bookings-container">
    <el-main class="main-content">
      <h2 class="page-title">
        <el-icon><Calendar /></el-icon>
        我的预约
      </h2>
      
      <el-table
        :data="bookings"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="预约ID" width="80" />
        
        <el-table-column label="设备名称" min-width="150">
          <template #default="{ row }">
            {{ row.deviceName || '-' }}
          </template>
        </el-table-column>
        
        <el-table-column label="设备位置" min-width="120">
          <template #default="{ row }">
            {{ row.deviceLocation || '-' }}
          </template>
        </el-table-column>
        
        <el-table-column label="开始时间" min-width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.startTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="结束时间" min-width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.endTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="创建时间" min-width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="canCancel(row)"
              type="danger"
              size="small"
              @click="handleCancel(row.id)"
            >
              取消
            </el-button>
            <span v-else style="color: #909399;">-</span>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="bookings.length === 0 && !loading" description="暂无预约记录" />
    </el-main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/services/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const bookings = ref([])
const loading = ref(false)

const loadBookings = async () => {
  loading.value = true
  try {
    const response = await api.get('/bookings/my')
    // 显示所有预约记录（包括历史记录），按创建时间倒序排列
    bookings.value = response.data.sort((a, b) => {
      return new Date(b.createdAt) - new Date(a.createdAt)
    })
  } catch (error) {
    ElMessage.error('加载预约列表失败')
  } finally {
    loading.value = false
  }
}

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return new Date(datetime).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getStatusType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'CONFIRMED': 'success',
    'CANCELLED': 'info',
    'COMPLETED': 'info',
    'EXPIRED': 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'PENDING': '待确认',
    'CONFIRMED': '已确认',
    'CANCELLED': '已取消',
    'COMPLETED': '已完成',
    'EXPIRED': '已过期'
  }
  return textMap[status] || status
}

const canCancel = (booking) => {
  if (booking.status === 'CANCELLED' || booking.status === 'COMPLETED' || booking.status === 'EXPIRED') {
    return false
  }
  // 只能取消未开始的预约
  return new Date(booking.startTime) > new Date()
}

const handleCancel = async (bookingId) => {
  try {
    await ElMessageBox.confirm('确定要取消这个预约吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await api.put(`/bookings/${bookingId}/cancel`)
    ElMessage.success('预约已取消')
    loadBookings()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.error || '取消预约失败')
    }
  }
}

onMounted(() => {
  loadBookings()
})
</script>

<style scoped>
.my-bookings-container {
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
</style>
