<template>
  <!-- 最后更新: 2025-11-09 02:50 -->
  <div class="user-management">
    <div class="page-header">
      <h2>👥 用户管理</h2>
      <el-button type="primary" @click="fetchUsers">
        <i class="ri-refresh-line"></i> 刷新
      </el-button>
    </div>
    
    <el-card class="user-card">
      <el-table 
        :data="users" 
        v-loading="loading" 
        stripe 
        style="width: 100%;"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="username" label="用户名" min-width="150" />
        <el-table-column prop="email" label="邮箱" min-width="200" />
        <el-table-column label="角色" width="120" align="center">
          <template #default="{ row }">
            <el-tag 
              :type="getRoleType(row.role)"
              effect="light"
            >
              {{ getRoleLabel(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'danger'" effect="light">
              {{ row.enabled ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column 
          prop="createdAt" 
          label="创建时间" 
          min-width="180"
        >
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button 
              size="small" 
              type="primary" 
              link
            >
              编辑
            </el-button>
            <el-button 
              size="small" 
              :type="row.enabled ? 'warning' : 'success'"
              link
            >
              {{ row.enabled ? '禁用' : '启用' }}
            </el-button>
            <el-button 
              size="small" 
              type="danger"
              link
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '@/services/api'

console.log('👥 UserManagement 组件已加载!')

const loading = ref(false)
const users = ref([])

const fetchUsers = async () => {
  console.log('开始获取用户列表...')
  loading.value = true
  try {
    const response = await api.get('/users')
    console.log('用户数据:', response.data)
    users.value = response.data
    ElMessage.success('获取成功')
  } catch (error) {
    console.error('获取用户失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const getRoleType = (role) => {
  const types = {
    ADMIN: 'danger',
    TEACHER: 'success',
    STUDENT: 'warning'
  }
  return types[role] || ''
}

const getRoleLabel = (role) => {
  const labels = {
    ADMIN: '管理员',
    TEACHER: '教师',
    STUDENT: '学生'
  }
  return labels[role] || role
}

const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  console.log('UserManagement 组件已挂载，开始获取数据')
  fetchUsers()
})
</script>

<style scoped>
.user-management {
  padding: 20px;
  height: 100%;
  overflow-y: auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.user-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.user-card :deep(.el-card__body) {
  padding: 0;
}

.user-management :deep(.el-table) {
  font-size: 14px;
}

.user-management :deep(.el-table th) {
  font-weight: 600;
}

.user-management :deep(.el-button + .el-button) {
  margin-left: 8px;
}
</style>