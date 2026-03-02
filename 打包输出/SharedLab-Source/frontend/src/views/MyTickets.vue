<template>
  <div class="my-tickets-container">
    <el-main class="main-content">
      <div class="page-header">
        <h2 class="page-title">
          <el-icon><DocumentCopy /></el-icon>
          我的报修
        </h2>
        
        <el-button type="primary" @click="showCreateDialog = true">
          <el-icon><Plus /></el-icon>
          新建报修单
        </el-button>
      </div>
      
      <el-table
        :data="tickets"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="报修ID" width="80" />
        
        <el-table-column prop="title" label="标题" min-width="200" />
        
        <el-table-column label="设备名称" min-width="150">
          <template #default="{ row }">
            {{ row.device?.name || '-' }}
          </template>
        </el-table-column>
        
        <el-table-column label="严重程度" width="100">
          <template #default="{ row }">
            <el-tag :type="getSeverityType(row.severity)" size="small">
              {{ row.severity || '普通' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="提交时间" min-width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="text" @click="viewDetail(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="tickets.length === 0 && !loading" description="暂无报修记录" />
    </el-main>
    
    <!-- 新建报修单对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      title="提交报修单"
      width="600px"
    >
      <el-form
        ref="ticketFormRef"
        :model="ticketForm"
        :rules="ticketRules"
        label-width="100px"
      >
        <el-form-item label="设备" prop="deviceId">
          <el-select
            v-model="ticketForm.deviceId"
            placeholder="请选择设备"
            style="width: 100%"
            filterable
          >
            <el-option
              v-for="device in devices"
              :key="device.id"
              :label="device.name"
              :value="device.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="标题" prop="title">
          <el-input v-model="ticketForm.title" placeholder="请输入报修标题" />
        </el-form-item>
        
        <el-form-item label="严重程度" prop="severity">
          <el-select v-model="ticketForm.severity" placeholder="请选择严重程度">
            <el-option label="低" value="低" />
            <el-option label="中" value="中" />
            <el-option label="高" value="高" />
            <el-option label="紧急" value="紧急" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="详细描述" prop="content">
          <el-input
            v-model="ticketForm.content"
            type="textarea"
            :rows="5"
            placeholder="请详细描述设备故障情况"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
      </template>
    </el-dialog>
    
    <!-- 查看详情对话框 -->
    <el-dialog
      v-model="showDetailDialog"
      title="报修单详情"
      width="600px"
    >
      <el-descriptions v-if="currentTicket" :column="1" border>
        <el-descriptions-item label="报修ID">
          {{ currentTicket.id }}
        </el-descriptions-item>
        <el-descriptions-item label="标题">
          {{ currentTicket.title }}
        </el-descriptions-item>
        <el-descriptions-item label="设备">
          {{ currentTicket.device?.name }}
        </el-descriptions-item>
        <el-descriptions-item label="严重程度">
          <el-tag :type="getSeverityType(currentTicket.severity)">
            {{ currentTicket.severity || '普通' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentTicket.status)">
            {{ getStatusText(currentTicket.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="详细描述">
          {{ currentTicket.content || '无' }}
        </el-descriptions-item>
        <el-descriptions-item label="提交时间">
          {{ formatDateTime(currentTicket.createdAt) }}
        </el-descriptions-item>
        <el-descriptions-item v-if="currentTicket.closedAt" label="关闭时间">
          {{ formatDateTime(currentTicket.closedAt) }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import api from '@/services/api'
import { ElMessage } from 'element-plus'

const tickets = ref([])
const devices = ref([])
const loading = ref(false)
const showCreateDialog = ref(false)
const showDetailDialog = ref(false)
const currentTicket = ref(null)
const ticketFormRef = ref(null)

const ticketForm = reactive({
  deviceId: null,
  title: '',
  content: '',
  severity: '中'
})

const ticketRules = {
  deviceId: [{ required: true, message: '请选择设备', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }]
}

const loadTickets = async () => {
  loading.value = true
  try {
    const response = await api.get('/tickets/my')
    // 按提交时间倒序排列，最新的在上面
    tickets.value = response.data.sort((a, b) => {
      return new Date(b.submittedAt) - new Date(a.submittedAt)
    })
  } catch (error) {
    console.error('加载报修单失败:', error)
    const errorMsg = error.response?.data?.message || error.message || '加载报修单失败'
    ElMessage.error(`加载报修单错误: ${errorMsg}`)
  } finally {
    loading.value = false
  }
}

const loadDevices = async () => {
  try {
    const response = await api.get('/devices')
    devices.value = response.data
  } catch (error) {
    console.error('加载设备列表失败:', error)
  }
}

const handleSubmit = async () => {
  if (!ticketFormRef.value) return
  
  await ticketFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    try {
      await api.post('/tickets', ticketForm)
      ElMessage.success('报修单提交成功')
      showCreateDialog.value = false
      
      // 重置表单
      Object.assign(ticketForm, {
        deviceId: null,
        title: '',
        content: '',
        severity: '中'
      })
      
      loadTickets()
    } catch (error) {
      ElMessage.error('提交失败，请重试')
    }
  })
}

const viewDetail = (ticket) => {
  currentTicket.value = ticket
  showDetailDialog.value = true
}

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return new Date(datetime).toLocaleString('zh-CN')
}

const getSeverityType = (severity) => {
  const typeMap = {
    '低': 'success',
    '中': 'warning',
    '高': 'danger',
    '紧急': 'danger'
  }
  return typeMap[severity] || 'info'
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
  loadTickets()
  loadDevices()
})
</script>

<style scoped>
.my-tickets-container {
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
</style>
