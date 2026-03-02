<template>
  <div class="booking-calendar">
    <el-form :model="bookingForm" label-width="100px">
      <el-form-item label="开始时间">
        <el-date-picker
          v-model="bookingForm.startTime"
          type="datetime"
          placeholder="选择开始时间"
          :disabled-date="disabledDate"
          :disabled-hours="disabledHours"
          format="YYYY-MM-DD HH:mm"
          value-format="YYYY-MM-DDTHH:mm:ss"
          style="width: 100%"
        />
      </el-form-item>
      
      <el-form-item label="结束时间">
        <el-date-picker
          v-model="bookingForm.endTime"
          type="datetime"
          placeholder="选择结束时间"
          :disabled-date="disabledDate"
          :disabled-hours="disabledHours"
          format="YYYY-MM-DD HH:mm"
          value-format="YYYY-MM-DDTHH:mm:ss"
          style="width: 100%"
        />
      </el-form-item>
      
      <el-form-item>
        <el-button
          type="primary"
          @click="handleBooking"
          :loading="loading"
          :disabled="!canBook"
        >
          {{ loading ? '预约中...' : '立即预约' }}
        </el-button>
      </el-form-item>
    </el-form>
    
    <el-divider />
    
    <div class="bookings-list">
      <el-alert
        title="预约提示"
        type="info"
        :closable="false"
        style="margin-bottom: 15px;"
      >
        请避开以下已预约时段，选择空闲时间进行预约
      </el-alert>
      
      <h3>已有预约时段 ({{ sortedBookings.length }} 个)</h3>
      <el-timeline v-if="sortedBookings.length > 0">
        <el-timeline-item
          v-for="booking in sortedBookings"
          :key="booking.id"
          :timestamp="formatBookingTime(booking)"
          placement="top"
          :type="getBookingType(booking.status)"
        >
          <div class="booking-item">
            <el-tag :type="getStatusTagType(booking.status)" size="small">
              {{ getStatusText(booking.status) }}
            </el-tag>
            <span class="booking-user">预约人: {{ booking.username || '未知' }}</span>
          </div>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无预约记录，可以自由选择时间" :image-size="100" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import api from '@/services/api'

const props = defineProps({
  deviceId: {
    type: Number,
    required: true
  },
  bookings: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['booking-created'])

const loading = ref(false)
const bookingForm = ref({
  startTime: null,
  endTime: null
})

// 过滤掉已取消的预约，并按时间排序
const sortedBookings = computed(() => {
  return props.bookings
    .filter(b => b.status !== 'CANCELLED')
    .sort((a, b) => new Date(a.startTime) - new Date(b.startTime))
})

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 24 * 60 * 60 * 1000
}

// 禁用过去的小时
const disabledHours = () => {
  const hours = []
  const now = new Date()
  const currentHour = now.getHours()
  
  for (let i = 0; i < currentHour; i++) {
    hours.push(i)
  }
  return hours
}

// 检查是否可以预约
const canBook = computed(() => {
  return bookingForm.value.startTime && bookingForm.value.endTime
})

// 处理预约
const handleBooking = async () => {
  if (!bookingForm.value.startTime || !bookingForm.value.endTime) {
    ElMessage.warning('请选择预约时间')
    return
  }
  
  if (new Date(bookingForm.value.endTime) <= new Date(bookingForm.value.startTime)) {
    ElMessage.warning('结束时间必须晚于开始时间')
    return
  }
  
  loading.value = true
  
  try {
    await api.post('/bookings', {
      deviceId: props.deviceId,
      startTime: bookingForm.value.startTime,
      endTime: bookingForm.value.endTime
    })
    
    ElMessage.success('预约成功！')
    
    // 清空表单
    bookingForm.value = {
      startTime: null,
      endTime: null
    }
    
    // 通知父组件刷新
    emit('booking-created')
    
  } catch (error) {
    if (error.response?.status === 409) {
      // 时间冲突，给出更友好的提示
      const errorMsg = error.response.data.error || '该时间段已被预约'
      ElMessage({
        message: errorMsg + '。请查看下方已有预约时段，选择空闲时间。',
        type: 'warning',
        duration: 5000,
        showClose: true
      })
    } else {
      ElMessage.error(error.response?.data?.error || '预约失败，请重试')
    }
  } finally {
    loading.value = false
  }
}

const formatBookingTime = (booking) => {
  const start = new Date(booking.startTime).toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
  const end = new Date(booking.endTime).toLocaleString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
  return `${start} - ${end}`
}

const getBookingType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'CONFIRMED': 'success',
    'COMPLETED': 'info',
    'EXPIRED': 'info'
  }
  return typeMap[status] || 'info'
}

const getStatusTagType = (status) => {
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
</script>

<style scoped>
.booking-calendar {
  padding: 20px;
}

.bookings-list {
  margin-top: 20px;
}

.bookings-list h3 {
  font-size: 16px;
  color: #303133;
  margin-bottom: 15px;
}

.booking-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.booking-user {
  font-size: 14px;
  color: #606266;
}
</style>
