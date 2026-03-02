<template>
  <div class="booking-management">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
              <el-icon :size="32"><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ totalBookings }}</div>
              <div class="stat-label">预约总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
              <el-icon :size="32"><Box /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ totalDevices }}</div>
              <div class="stat-label">设备总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
              <el-icon :size="32"><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ confirmedBookings }}</div>
              <div class="stat-label">已确认</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);">
              <el-icon :size="32"><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ pendingBookings }}</div>
              <div class="stat-label">待确认</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选和搜索 -->
    <el-card shadow="never" class="filter-card">
      <el-form :inline="true">
        <el-form-item label="状态筛选">
          <el-select v-model="filters.status" placeholder="全部状态" clearable style="width: 150px" @change="loadBookings">
            <el-option label="待确认" value="PENDING" />
            <el-option label="已确认" value="CONFIRMED" />
            <el-option label="已取消" value="CANCELLED" />
            <el-option label="已完成" value="COMPLETED" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="设备筛选">
          <el-select v-model="filters.deviceId" placeholder="全部设备" clearable style="width: 200px" @change="loadBookings">
            <el-option 
              v-for="device in devices" 
              :key="device.id" 
              :label="device.name" 
              :value="device.id" 
            />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="loadBookings">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 预约列表 -->
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">预约列表</span>
        </div>
      </template>
      
      <el-table 
        :data="bookings" 
        v-loading="loading" 
        stripe 
        style="width: 100%"
        :default-sort="{ prop: 'createdAt', order: 'descending' }"
      >
        <el-table-column prop="id" label="预约ID" width="80" sortable />
        
        <el-table-column label="预约人" width="120">
          <template #default="{ row }">
            <div class="user-cell">
              <el-icon><User /></el-icon>
              <span>{{ row.username || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="设备名称" min-width="150">
          <template #default="{ row }">
            <div class="device-cell">
              <el-icon><Box /></el-icon>
              <span>{{ row.deviceName || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="设备位置" width="150">
          <template #default="{ row }">
            <div class="location-cell">
              <el-icon><Location /></el-icon>
              <span>{{ row.deviceLocation || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="开始时间" width="160" sortable prop="startTime">
          <template #default="{ row }">
            {{ formatDateTime(row.startTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="结束时间" width="160" sortable prop="endTime">
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
        
        <el-table-column label="创建时间" width="160" sortable prop="createdAt">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="canApprove(row)"
              type="success"
              size="small"
              @click="handleApprove(row.id)"
            >
              批准
            </el-button>
            <el-button
              v-if="canCancel(row)"
              type="danger"
              size="small"
              @click="handleCancel(row.id)"
            >
              取消
            </el-button>
            <el-button
              type="info"
              size="small"
              @click="viewDetail(row)"
            >
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-if="bookings.length > 0"
        class="pagination"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="totalBookings"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/services/api'

const loading = ref(false)
const allBookings = ref([]) // 保存所有预约数据
const bookings = ref([]) // 显示的预约数据（筛选后）
const devices = ref([])
const filters = ref({
  status: null,
  deviceId: null
})

const currentPage = ref(1)
const pageSize = ref(20)

// 统计数据（基于所有数据）
const totalBookings = computed(() => allBookings.value.length)
const totalDevices = computed(() => devices.value.length)
const confirmedBookings = computed(() => allBookings.value.filter(b => b.status === 'CONFIRMED').length)
const pendingBookings = computed(() => allBookings.value.filter(b => b.status === 'PENDING').length)

const loadBookings = async () => {
  loading.value = true
  try {
    const response = await api.get('/bookings/all')
    // 保存所有数据用于统计
    allBookings.value = response.data
    
    let filteredBookings = response.data
    
    // 应用状态筛选
    if (filters.value.status) {
      filteredBookings = filteredBookings.filter(b => b.status === filters.value.status)
    }
    
    // 应用设备筛选
    if (filters.value.deviceId) {
      filteredBookings = filteredBookings.filter(b => b.deviceId === filters.value.deviceId)
    }
    
    // 按创建时间倒序排列，最新创建的预约在上面
    bookings.value = filteredBookings.sort((a, b) => {
      return new Date(b.createdAt) - new Date(a.createdAt)
    })
  } catch (error) {
    ElMessage.error('加载预约列表失败')
    console.error('Load bookings error:', error)
  } finally {
    loading.value = false
  }
}

const loadDevices = async () => {
  try {
    const response = await api.get('/devices')
    devices.value = response.data
  } catch (error) {
    console.error('Load devices error:', error)
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

const canApprove = (booking) => {
  return booking.status === 'PENDING'
}

const canCancel = (booking) => {
  if (booking.status === 'CANCELLED' || booking.status === 'COMPLETED' || booking.status === 'EXPIRED') {
    return false
  }
  return new Date(booking.startTime) > new Date()
}

const handleApprove = async (bookingId) => {
  try {
    await ElMessageBox.confirm('确定要批准这个预约吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })
    
    // TODO: 调用批准接口
    ElMessage.success('预约已批准')
    loadBookings()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
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

const viewDetail = (booking) => {
  ElMessageBox.alert(`
    <div style="text-align: left;">
      <p><strong>预约ID:</strong> ${booking.id}</p>
      <p><strong>预约人:</strong> ${booking.username || '-'}</p>
      <p><strong>设备:</strong> ${booking.deviceName || '-'}</p>
      <p><strong>位置:</strong> ${booking.deviceLocation || '-'}</p>
      <p><strong>开始时间:</strong> ${formatDateTime(booking.startTime)}</p>
      <p><strong>结束时间:</strong> ${formatDateTime(booking.endTime)}</p>
      <p><strong>状态:</strong> ${getStatusText(booking.status)}</p>
      <p><strong>创建时间:</strong> ${formatDateTime(booking.createdAt)}</p>
    </div>
  `, '预约详情', {
    dangerouslyUseHTMLString: true,
    confirmButtonText: '关闭'
  })
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadBookings()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}

onMounted(() => {
  loadBookings()
  loadDevices()
})
</script>

<style scoped>
.booking-management {
  padding: 20px;
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
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.filter-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.user-cell,
.device-cell,
.location-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
