<template>
  <!-- 最后更新: 2025-11-09 02:50 -->
  <div class="user-management">
    <div class="page-header">
      <h2>👥 用户管理</h2>
      <div class="header-actions">
        <el-button
          type="danger"
          :disabled="selectedUserIds.length === 0"
          :loading="bulkDeleting"
          @click="bulkDelete"
        >
          批量删除（{{ selectedUserIds.length }}）
        </el-button>
        <el-button type="primary" @click="fetchUsers">
          <i class="ri-refresh-line"></i> 刷新
        </el-button>
      </div>
    </div>
    
    <el-card class="user-card">
      <el-table 
        :data="users" 
        v-loading="loading" 
        stripe 
        style="width: 100%;"
        :row-key="(row) => row.id"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
        @selection-change="onSelectionChange"
      >
        <el-table-column type="selection" width="48" align="center" />
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
        <el-table-column 
          prop="createdAt" 
          label="创建时间" 
          min-width="180"
        >
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button 
              size="small" 
              type="primary" 
              link
              @click="openEdit(row)"
            >
              编辑
            </el-button>
            <el-button 
              size="small" 
              :type="isEnabled(row) ? 'warning' : 'success'"
              link
              @click="toggleStatus(row)"
            >
              {{ isEnabled(row) ? '禁用' : '启用' }}
            </el-button>
            <el-button 
              size="small" 
              type="danger"
              link
              @click="deleteUser(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑用户对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑用户"
      width="520px"
      destroy-on-close
    >
      <el-form :model="editForm" label-width="90px">
        <el-form-item label="用户名" required>
          <el-input v-model="editForm.username" placeholder="请输入用户名" />
        </el-form-item>

        <el-form-item label="邮箱" required>
          <el-input v-model="editForm.email" placeholder="请输入邮箱" />
        </el-form-item>

        <el-form-item label="角色" required>
          <el-select v-model="editForm.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="教师" value="TEACHER" />
            <el-option label="学生" value="STUDENT" />
          </el-select>
        </el-form-item>

        <el-form-item label="重置密码">
          <el-input
            v-model="editForm.password"
            placeholder="不修改请留空（至少6位）"
            show-password
            autocomplete="new-password"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/services/api'

console.log('👥 UserManagement 组件已加载!')

const loading = ref(false)
const users = ref([])
const saving = ref(false)
const bulkDeleting = ref(false)

const selectedUserIds = ref([])

const editDialogVisible = ref(false)
const editingUserId = ref(null)
const editForm = reactive({
  username: '',
  email: '',
  role: 'STUDENT',
  password: '',
  // 不在 UI 展示状态字段，但提交更新时需要保留
  status: 1
})

const isEnabled = (user) => {
  if (typeof user?.enabled === 'boolean') return user.enabled
  return Number(user?.status ?? 1) === 1
}

const onSelectionChange = (selection) => {
  selectedUserIds.value = (selection || []).map(item => item.id)
}

const fetchUsers = async () => {
  console.log('开始获取用户列表...')
  loading.value = true
  try {
    const response = await api.get('/users')
    console.log('用户数据:', response.data)
    users.value = response.data
  } catch (error) {
    console.error('获取用户失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const openEdit = (row) => {
  editingUserId.value = row.id
  editForm.username = row.username || ''
  editForm.email = row.email || ''
  editForm.role = row.role || 'STUDENT'
  editForm.password = ''
  editForm.status = Number(row.status ?? 1)
  editDialogVisible.value = true
}

const saveEdit = async () => {
  if (!editingUserId.value) return
  if (!editForm.username?.trim() || !editForm.email?.trim() || !editForm.role) {
    ElMessage.warning('请填写用户名、邮箱并选择角色')
    return
  }
  if (editForm.password && editForm.password.length > 0 && editForm.password.length < 6) {
    ElMessage.warning('密码长度至少为6位（不修改请留空）')
    return
  }

  const payload = {
    username: editForm.username.trim(),
    email: editForm.email.trim(),
    role: editForm.role,
    status: editForm.status
  }
  if (editForm.password && editForm.password.length >= 6) {
    payload.password = editForm.password
  }

  saving.value = true
  try {
    const response = await api.put(`/users/${editingUserId.value}`, payload)
    const updated = response.data
    const index = users.value.findIndex(u => u.id === updated.id)
    if (index !== -1) users.value[index] = updated
    ElMessage.success('保存成功')
    editDialogVisible.value = false
  } catch (error) {
    console.error('更新用户失败:', error)
    ElMessage.error('保存失败: ' + (error.response?.data?.message || error.message || '未知错误'))
  } finally {
    saving.value = false
  }
}

const toggleStatus = async (row) => {
  try {
    const nextText = isEnabled(row) ? '禁用' : '启用'
    await ElMessageBox.confirm(`确定要${nextText}该用户吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await api.put(`/users/${row.id}/toggle-status`)
    const updated = response.data
    const index = users.value.findIndex(u => u.id === updated.id)
    if (index !== -1) users.value[index] = updated
    ElMessage.success(`${nextText}成功`)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('切换用户状态失败:', error)
      ElMessage.error('操作失败: ' + (error.response?.data?.message || error.message || '未知错误'))
    }
  }
}

const deleteUser = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除用户“${row.username}”吗？该操作不可恢复。`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await api.delete(`/users/${row.id}`)
    users.value = users.value.filter(u => u.id !== row.id)
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除用户失败:', error)
      ElMessage.error('删除失败: ' + (error.response?.data?.message || error.message || '未知错误'))
    }
  }
}

const bulkDelete = async () => {
  if (selectedUserIds.value.length === 0) return

  const count = selectedUserIds.value.length
  try {
    await ElMessageBox.confirm(`确定要批量删除所选的 ${count} 个用户吗？该操作不可恢复。`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    bulkDeleting.value = true
    const ids = [...selectedUserIds.value]
    const results = await Promise.allSettled(ids.map(id => api.delete(`/users/${id}`)))

    const succeededIds = []
    const failed = []
    results.forEach((r, idx) => {
      const id = ids[idx]
      if (r.status === 'fulfilled') {
        succeededIds.push(id)
      } else {
        failed.push({ id, error: r.reason })
      }
    })

    if (succeededIds.length > 0) {
      users.value = users.value.filter(u => !succeededIds.includes(u.id))
    }
    selectedUserIds.value = []

    if (failed.length === 0) {
      ElMessage.success(`批量删除成功（${succeededIds.length}/${count}）`)
      return
    }

    const firstMsg = failed[0]?.error?.response?.data?.message || failed[0]?.error?.message
    ElMessage.warning(`部分删除失败（成功 ${succeededIds.length}，失败 ${failed.length}）。${firstMsg ? '原因示例：' + firstMsg : ''}`)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败: ' + (error.response?.data?.message || error.message || '未知错误'))
    }
  } finally {
    bulkDeleting.value = false
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

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
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