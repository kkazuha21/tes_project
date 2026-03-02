<template>
  <div class="ticket-management">
    <el-main class="main-content">
      <h2 class="page-title">
        <el-icon><DocumentCopy /></el-icon>
        报修单管理
      </h2>
      
      <el-table
        :data="tickets"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="60" />
        
        <el-table-column prop="title" label="标题" min-width="180" />
        
        <el-table-column label="设备" min-width="150">
          <template #default="{ row }">
            {{ row.device?.name || '-' }}
          </template>
        </el-table-column>
        
        <el-table-column label="提交人" width="120">
          <template #default="{ row }">
            {{ row.user?.username || '-' }}
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
        
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="text" @click="viewDetail(row)">查看</el-button>
            <el-button
              v-if="row.status === 'SUBMITTED'"
              type="text"
              @click="updateStatus(row.id, 'PROCESSING')"
            >
              处理中
            </el-button>
            <el-button
              v-if="row.status === 'PROCESSING'"
              type="text"
              @click="updateStatus(row.id, 'CLOSED')"
            >
              关闭
            </el-button>
            <el-button
              v-if="row.status === 'SUBMITTED'"
              type="text"
              style="color: #f56c6c;"
              @click="updateStatus(row.id, 'REJECTED')"
            >
              拒绝
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-main>
    
    <!-- 详情对话框 -->
    <el-dialog
      v-model="showDetailDialog"
      title="报修单详情"
      width="700px"
    >
      <el-descriptions v-if="currentTicket" :column="2" border>
        <el-descriptions-item label="报修ID" :span="2">
          {{ currentTicket.id }}
        </el-descriptions-item>
        <el-descriptions-item label="标题" :span="2">
          {{ currentTicket.title }}
        </el-descriptions-item>
        <el-descriptions-item label="设备">
          {{ currentTicket.device?.name }}
        </el-descriptions-item>
        <el-descriptions-item label="设备位置">
          {{ currentTicket.device?.location || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="提交人">
          {{ currentTicket.user?.username }}
        </el-descriptions-item>
        <el-descriptions-item label="提交人邮箱">
          {{ currentTicket.user?.email }}
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
        <el-descriptions-item label="详细描述" :span="2">
          {{ currentTicket.content || '无' }}
        </el-descriptions-item>
        <el-descriptions-item label="提交时间" :span="2">
          {{ formatDateTime(currentTicket.createdAt) }}
        </el-descriptions-item>
        <el-descriptions-item v-if="currentTicket.closedAt" label="关闭时间" :span="2">
          {{ formatDateTime(currentTicket.closedAt) }}
        </el-descriptions-item>
      </el-descriptions>
      
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/services/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const tickets = ref([])
const loading = ref(false)
const showDetailDialog = ref(false)
const currentTicket = ref(null)

const loadTickets = async () => {
  loading.value = true
  try {
    const response = await api.get('/tickets')
    // 按提交时间倒序排列，最新的在上面
    tickets.value = response.data.sort((a, b) => {
      return new Date(b.submittedAt) - new Date(a.submittedAt)
    })
  } catch (error) {
    ElMessage.error('加载报修单失败')
  } finally {
    loading.value = false
  }
}

const viewDetail = (ticket) => {
  currentTicket.value = ticket
  showDetailDialog.value = true
}

const updateStatus = async (ticketId, newStatus) => {
  try {
    const statusText = getStatusText(newStatus)
    await ElMessageBox.confirm(`确定要将状态更新为"${statusText}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await api.put(`/tickets/${ticketId}/status?status=${newStatus}`)
    
    ElMessage.success('状态更新成功')
    loadTickets()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Update status error:', error)
      ElMessage.error('状态更新失败: ' + (error.response?.data?.message || error.message || '未知错误'))
    }
  }
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
})
</script>

<style scoped>
.ticket-management {
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
