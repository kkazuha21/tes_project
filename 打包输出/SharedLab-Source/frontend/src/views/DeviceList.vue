<template>
  <div class="device-list-container">
    <el-main class="main-content">
      <div class="page-header">
        <h2 class="page-title">
          <el-icon><Box /></el-icon>
          设备列表
        </h2>
        
        <div class="filters">
          <el-select
            v-model="filters.status"
            placeholder="设备状态"
            clearable
            style="width: 150px"
            @change="handleFilter"
          >
            <el-option label="可用" value="AVAILABLE" />
            <el-option label="使用中" value="IN_USE" />
            <el-option label="维护中" value="MAINTENANCE" />
          </el-select>
        </div>
      </div>
      
      <el-row :gutter="20" v-loading="deviceStore.loading">
        <el-col
          v-for="device in deviceStore.devices"
          :key="device.id"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
        >
          <el-card shadow="hover" class="device-card" @click="goToDetail(device.id)">
            <div class="device-image">
              <i class="ri-microscope-line"></i>
            </div>
            <div class="device-info">
              <h3 class="device-name">{{ device.name }}</h3>
              <p class="device-location">
                <el-icon><Location /></el-icon>
                {{ device.location || '未指定位置' }}
              </p>
              <div class="device-footer">
                <el-tag :type="getStatusType(device.status)" size="small">
                  {{ getStatusText(device.status) }}
                </el-tag>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col v-if="deviceStore.devices.length === 0" :span="24">
          <el-empty description="暂无设备" />
        </el-col>
      </el-row>
    </el-main>
  </div>
</template>

<script setup>
import { reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useDeviceStore } from '@/store/device'
import { useAuthStore } from '@/store/auth'

const router = useRouter()
const deviceStore = useDeviceStore()
const authStore = useAuthStore()

const filters = reactive({
  status: null,
  categoryId: null
})

// 根据用户角色生成正确的路由前缀
const routePrefix = computed(() => {
  const role = authStore.user?.role
  if (role === 'ADMIN') return '/admin'
  if (role === 'TEACHER') return '/teacher'
  return '/student'
})

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

const handleFilter = () => {
  deviceStore.fetchDevices(filters)
}

const goToDetail = (id) => {
  router.push(`${routePrefix.value}/devices/${id}`)
}

onMounted(() => {
  deviceStore.fetchDevices()
})
</script>

<style scoped>
.device-list-container {
  width: 100%;
  height: 100%;
}

.main-content {
  padding: 30px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 24px;
  color: #303133;
  margin: 0;
}

.filters {
  display: flex;
  gap: 10px;
}

.device-card {
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 20px;
}

.device-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.device-image {
  height: 150px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  margin-bottom: 15px;
}

.device-image i {
  font-size: 64px;
  color: white;
}

.device-info {
  padding: 10px 0;
}

.device-name {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 10px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.device-location {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  color: #909399;
  margin: 0 0 10px 0;
}

.device-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
