<template>
  <div class="device-detail-container">
    <el-main class="main-content" v-loading="loading">
      <el-button
        type="text"
        @click="router.back()"
        style="margin-bottom: 20px"
      >
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
      
      <el-row :gutter="20" v-if="device">
        <el-col :span="10">
          <el-card>
            <div class="device-image-large">
              <i class="ri-microscope-line"></i>
            </div>
            <div class="device-detail-info">
              <h1>{{ device.name }}</h1>
              <p class="device-location">
                <el-icon><Location /></el-icon>
                {{ device.location || '未指定位置' }}
              </p>
              <div style="display: flex; align-items: center; gap: 10px; margin: 15px 0;">
                <el-tag :type="getStatusType(device.status)" size="large">
                  {{ getStatusText(device.status) }}
                </el-tag>
                <el-button 
                  v-if="isAdmin" 
                  type="primary" 
                  size="small"
                  @click="handleEditStatus"
                >
                  修改状态
                </el-button>
              </div>
              <div class="device-description">
                <h3>设备描述</h3>
                <p>{{ device.desc || '暂无描述' }}</p>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="14">
          <el-card>
            <template #header>
              <span>预约设备</span>
            </template>
            
            <BookingCalendar
              :device-id="deviceId"
              :bookings="bookings"
              @booking-created="handleBookingCreated"
            />
          </el-card>
        </el-col>
      </el-row>
    </el-main>

    <!-- 管理员编辑状态对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="修改设备状态"
      width="400px"
    >
      <el-form label-width="80px">
        <el-form-item label="设备状态">
          <el-select v-model="editForm.status" style="width: 100%;">
            <el-option label="可用" value="AVAILABLE" />
            <el-option label="使用中" value="IN_USE" />
            <el-option label="维修中" value="MAINTENANCE" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleUpdateStatus">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useDeviceStore } from '@/store/device'
import { useAuthStore } from '@/store/auth'
import BookingCalendar from '@/components/common/BookingCalendar.vue'
import api from '@/services/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const deviceStore = useDeviceStore()
const authStore = useAuthStore()

const deviceId = computed(() => Number(route.params.id))
const device = ref(null)
const bookings = ref([])
const loading = ref(false)
const editDialogVisible = ref(false)
const editForm = ref({
  status: ''
})

const isAdmin = computed(() => authStore.user?.role === 'ADMIN')

const getStatusType = (status) => {
  const typeMap = {
    'AVAILABLE': 'success',
    'IN_USE': 'warning',
    'MAINTENANCE': 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'AVAILABLE': '可用',
    'IN_USE': '使用中',
    'MAINTENANCE': '维护中'
  }
  return textMap[status] || status
}

const loadDeviceDetail = async () => {
  loading.value = true
  try {
    const result = await deviceStore.fetchDeviceById(deviceId.value)
    if (result.success) {
      device.value = result.data
    }
  } catch (error) {
    ElMessage.error('加载设备详情失败')
  } finally {
    loading.value = false
  }
}

const loadBookings = async () => {
  try {
    const response = await api.get('/bookings', {
      params: { deviceId: deviceId.value }
    })
    const now = new Date()
    
    // 只显示未开始和进行中的预约（过滤掉已完成的预约）
    bookings.value = response.data.filter(booking => {
      const endTime = new Date(booking.endTime)
      return endTime > now
    })
  } catch (error) {
    console.error('加载预约记录失败:', error)
  }
}

const handleBookingCreated = () => {
  loadBookings()
}

const handleEditStatus = () => {
  editForm.value.status = device.value.status
  editDialogVisible.value = true
}

const handleUpdateStatus = async () => {
  try {
    // 发送完整的设备对象，只修改状态字段
    const updatedDevice = {
      ...device.value,
      status: editForm.value.status
    }
    await api.put(`/devices/${deviceId.value}`, updatedDevice)
    ElMessage.success('设备状态更新成功')
    editDialogVisible.value = false
    loadDeviceDetail()
  } catch (error) {
    console.error('更新设备状态失败:', error)
    const errorMsg = error.response?.data?.message || error.response?.data || error.message || '未知错误'
    ElMessage.error('更新失败：' + errorMsg)
  }
}

onMounted(() => {
  loadDeviceDetail()
  loadBookings()
})
</script>

<style scoped>
.device-detail-container {
  width: 100%;
  height: 100%;
}

.main-content {
  padding: 30px;
}

.device-image-large {
  height: 300px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  margin-bottom: 20px;
}

.device-image-large i {
  font-size: 120px;
  color: white;
}

.device-detail-info h1 {
  font-size: 28px;
  color: #303133;
  margin: 0 0 10px 0;
}

.device-location {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 16px;
  color: #909399;
  margin: 0 0 15px 0;
}

.device-description {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.device-description h3 {
  font-size: 16px;
  color: #303133;
  margin: 0 0 10px 0;
}

.device-description p {
  color: #606266;
  line-height: 1.6;
}
</style>
