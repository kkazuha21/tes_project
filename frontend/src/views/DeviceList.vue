<template>
  <div class="device-list-container">
    <el-main class="main-content">
      <div class="page-header">
        <h2 class="page-title">
          <el-icon><Box /></el-icon>
          设备列表
        </h2>
        
        <div class="filters">
          <el-button v-if="isAdmin" type="primary" @click="openCreateDialog">
            新增设备
          </el-button>
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

    <!-- 新增设备（仅管理员） -->
    <el-dialog v-model="createDialogVisible" title="新增设备" width="520px">
      <el-form :model="createForm" label-width="90px">
        <el-form-item label="设备名称" required>
          <el-input v-model="createForm.name" placeholder="请输入设备名称" />
        </el-form-item>

        <el-form-item label="设备状态">
          <el-select v-model="createForm.status" style="width: 100%">
            <el-option label="可用" value="AVAILABLE" />
            <el-option label="使用中" value="IN_USE" />
            <el-option label="维护中" value="MAINTENANCE" />
          </el-select>
        </el-form-item>

        <el-form-item label="所属分类">
          <el-select
            v-model="createForm.categoryId"
            placeholder="可选"
            clearable
            style="width: 100%"
            :loading="categoriesLoading"
          >
            <el-option
              v-for="c in categories"
              :key="c.id"
              :label="c.name"
              :value="c.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="位置">
          <el-input v-model="createForm.location" placeholder="例如：A楼-101" />
        </el-form-item>

        <el-form-item label="描述">
          <el-input v-model="createForm.desc" type="textarea" :rows="3" placeholder="可选" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="creating" @click="handleCreateDevice">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, onMounted, computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useDeviceStore } from '@/store/device'
import { useAuthStore } from '@/store/auth'
import api from '@/services/api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const deviceStore = useDeviceStore()
const authStore = useAuthStore()

const filters = reactive({
  status: null,
  categoryId: null
})

const isAdmin = computed(() => authStore.user?.role === 'ADMIN')

const createDialogVisible = ref(false)
const creating = ref(false)
const categoriesLoading = ref(false)
const categories = ref([])

const createForm = reactive({
  name: '',
  status: 'AVAILABLE',
  categoryId: null,
  location: '',
  desc: ''
})

// 根据用户角色生成正确的路由前缀
const routePrefix = computed(() => {
  const role = authStore.user?.role
  if (role === 'ADMIN') return '/admin'
  if (role === 'TEACHER') return '/teacher'
  return '/student'
})

const loadCategories = async () => {
  if (!isAdmin.value) return
  categoriesLoading.value = true
  try {
    const response = await api.get('/categories')
    categories.value = response.data || []
  } catch (error) {
    console.error('加载分类失败:', error)
    ElMessage.error('加载分类失败')
  } finally {
    categoriesLoading.value = false
  }
}

const openCreateDialog = async () => {
  createForm.name = ''
  createForm.status = 'AVAILABLE'
  createForm.categoryId = null
  createForm.location = ''
  createForm.desc = ''
  createDialogVisible.value = true

  if (categories.value.length === 0) {
    await loadCategories()
  }
}

const handleCreateDevice = async () => {
  if (!createForm.name?.trim()) {
    ElMessage.warning('请输入设备名称')
    return
  }

  creating.value = true
  try {
    const payload = {
      name: createForm.name.trim(),
      status: createForm.status,
      location: createForm.location || null,
      desc: createForm.desc || null
    }

    if (createForm.categoryId) {
      payload.deviceCategory = { id: createForm.categoryId }
    }

    await api.post('/devices', payload)
    ElMessage.success('新增设备成功')
    createDialogVisible.value = false
    await deviceStore.fetchDevices(filters)
  } catch (error) {
    console.error('新增设备失败:', error)
    const msg = error.response?.data?.message || error.response?.data?.error || '新增设备失败'
    ElMessage.error(msg)
  } finally {
    creating.value = false
  }
}

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
  if (isAdmin.value) {
    loadCategories()
  }
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
